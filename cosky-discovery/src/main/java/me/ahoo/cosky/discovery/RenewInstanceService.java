/*
 *
 *  * Copyright [2021-2021] [ahoo wang <ahoowang@qq.com> (https://github.com/Ahoo-Wang)].
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package me.ahoo.cosky.discovery;

import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import lombok.var;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ahoo wang
 */
@Slf4j
public class RenewInstanceService {

    private volatile boolean running;
    private final RenewProperties renewProperties;
    private final ServiceRegistry serviceRegistry;
    private final ScheduledExecutorService scheduledExecutorService;
    private final AtomicInteger renewCounter = new AtomicInteger();

    public RenewInstanceService(RenewProperties renewProperties, ServiceRegistry serviceRegistry) {
        this(renewProperties,
                serviceRegistry,
                new ScheduledThreadPoolExecutor(1, createThreadFactory()));
    }

    public RenewInstanceService(RenewProperties renewProperties, ServiceRegistry serviceRegistry, ScheduledExecutorService scheduledExecutorService) {
        this.renewProperties = renewProperties;
        this.serviceRegistry = serviceRegistry;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    public void start() {
        if (isRunning()) {
            return;
        }
        if (log.isInfoEnabled()) {
            log.info("start.");
        }
        running = true;

        scheduledExecutorService.scheduleAtFixedRate(this::renew, renewProperties.getInitialDelay(), renewProperties.getPeriod(), TimeUnit.SECONDS);
    }

    public boolean isRunning() {
        return running;
    }

    public void stop() {
        if (!running) {
            return;
        }
        if (log.isInfoEnabled()) {
            log.info("stop.");
        }
        running = false;
        scheduledExecutorService.shutdown();
    }

    private void renew() {
        final int times = renewCounter.incrementAndGet();
        final Stopwatch stopwatch = Stopwatch.createStarted();
        final Map<NamespacedInstanceId, ServiceInstance> instances = serviceRegistry.getRegisteredEphemeralInstances();
        if (log.isDebugEnabled()) {
            log.debug("renew - instances size:{} start - times@[{}] .", instances.size(), times);
        }

        if (instances.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("renew - instances size:{} end - times@[{}] .", instances.size(), times);
            }
            return;
        }

        CompletableFuture<Boolean>[] renewFutures = new CompletableFuture[instances.size()];

        var instanceIterator = instances.entrySet().iterator();
        for (int i = 0; i < renewFutures.length; i++) {
            var namespacedServiceInstance = instanceIterator.next();
            renewFutures[i] = serviceRegistry.renew(namespacedServiceInstance.getKey().getNamespace(), namespacedServiceInstance.getValue())
                    .exceptionally((ex) -> {
                        if (log.isWarnEnabled()) {
                            log.warn("renew - failed.", ex);
                        }
                        return Boolean.FALSE;
                    });
        }
        CompletableFuture.allOf(renewFutures).thenAccept((nil) -> {
            if (log.isDebugEnabled()) {
                log.debug("renew - instances size:{} start - times@[{}] taken:[{}ms].", instances.size(), times, stopwatch.elapsed(TimeUnit.MILLISECONDS));
            }
        });
    }

    private static ThreadFactory createThreadFactory() {
        return new ThreadFactoryBuilder()
                .setNameFormat(RenewInstanceService.class.getSimpleName() + "-%d")
                .setDaemon(true)
                .build();
    }
}
