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

package me.ahoo.cosky.discovery.spring.cloud.discovery;

import me.ahoo.cosky.discovery.ServiceDiscovery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import reactor.core.publisher.Flux;

/**
 * Cosky Reactive Discovery Client.
 *
 * @author ahoo wang
 */
@Slf4j
public class CoskyReactiveDiscoveryClient implements ReactiveDiscoveryClient {
    private final ServiceDiscovery serviceDiscovery;
    
    public CoskyReactiveDiscoveryClient(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }
    
    @Override
    public String description() {
        return "CoSky Reactive Discovery Client";
    }
    
    @Override
    public Flux<ServiceInstance> getInstances(String serviceId) {
        return serviceDiscovery.getInstances(serviceId)
            .map(CoskyServiceInstance::new);
    }
    
    @Override
    public Flux<String> getServices() {
        return serviceDiscovery.getServices();
    }
}
