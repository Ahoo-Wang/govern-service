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

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * @author ahoo wang
 */
public interface ServiceDiscovery {

    CompletableFuture<Set<String>> getServices(String namespace);

    CompletableFuture<Set<String>> getServices();

    CompletableFuture<List<ServiceInstance>> getInstances(String serviceId);

    CompletableFuture<List<ServiceInstance>> getInstances(String namespace, String serviceId);

    CompletableFuture<ServiceInstance> getInstance(String namespace, String serviceId, String instanceId);

    CompletableFuture<Long> getInstanceTtl(String namespace, String serviceId, String instanceId);

}
