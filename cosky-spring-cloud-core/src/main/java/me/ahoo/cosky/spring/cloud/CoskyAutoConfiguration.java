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

package me.ahoo.cosky.spring.cloud;

import me.ahoo.cosky.core.NamespaceService;
import me.ahoo.cosky.core.NamespacedContext;
import me.ahoo.cosky.core.redis.RedisNamespaceService;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;

/**
 * Cosky Auto Configuration.
 *
 * @author ahoo wang
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnCoskyEnabled
@EnableConfigurationProperties(CoskyProperties.class)
public class CoskyAutoConfiguration {
    
    public CoskyAutoConfiguration(CoskyProperties coskyProperties) {
        NamespacedContext.GLOBAL.setCurrentContextNamespace(coskyProperties.getNamespace());
    }
    
    @Bean
    @ConditionalOnMissingBean
    public NamespaceService namespaceService(ReactiveStringRedisTemplate redisTemplate) {
        return new RedisNamespaceService(redisTemplate);
    }
    
    @Bean
    @ConditionalOnMissingBean
    public ReactiveRedisMessageListenerContainer reactiveRedisMessageListenerContainer(ReactiveRedisConnectionFactory connectionFactory) {
        return new ReactiveRedisMessageListenerContainer(connectionFactory);
    }
}
