/*
 * Copyright [2021-present] [ahoo wang <ahoowang@qq.com> (https://github.com/Ahoo-Wang)].
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.ahoo.cosky.discovery;

import me.ahoo.cosky.core.NamespacedContext;
import me.ahoo.cosky.core.redis.RedisNamespaceService;
import me.ahoo.cosky.core.test.AbstractReactiveRedisTest;
import me.ahoo.cosky.discovery.redis.ConsistencyRedisServiceDiscovery;
import me.ahoo.cosky.discovery.redis.RedisServiceDiscovery;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author ahoo wang
 */
public class ServiceTopologyTest extends AbstractReactiveRedisTest {
    private final static String namespace = "topology";
    private ConsistencyRedisServiceDiscovery consistencyRedisServiceDiscovery;
    
    @BeforeEach
    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        RedisNamespaceService redisNamespaceService = new RedisNamespaceService(redisTemplate);
        redisNamespaceService.setNamespace(namespace).block();
        RedisServiceDiscovery redisServiceDiscovery = new RedisServiceDiscovery(redisTemplate);
        consistencyRedisServiceDiscovery = new ConsistencyRedisServiceDiscovery(redisServiceDiscovery, redisTemplate, listenerContainer);
    }
    
    @AfterEach
    @Override
    public void destroy() {
        super.destroy();
    }
    
    @Test
    public void buildTopologyData() {
        NamespacedContext.GLOBAL.setCurrentContextNamespace(namespace);
        int serviceSize = 20;
        for (int i = 0; i < serviceSize; i++) {
            ServiceInstance currentInstance = new ServiceInstance();
            
            currentInstance.setServiceId("service-" + i);
            ServiceInstanceContext.CURRENT.setServiceInstance(currentInstance);
            
            for (int j = 0; j < 5; j++) {
                int depServiceId = ThreadLocalRandom.current().nextInt(0, serviceSize);
                if (depServiceId == i) {
                    continue;
                }
                consistencyRedisServiceDiscovery.addTopology(namespace, "service-" + depServiceId).block();
            }
            
        }
    }
}
