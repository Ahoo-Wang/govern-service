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

package me.ahoo.cosky.core.test;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import reactor.core.publisher.Mono;

import java.util.Collections;

/**
 * ClearRedisScripts .
 *
 * @author ahoo wang
 */
public final class ClearRedisScripts {
    
    public static final Resource RESOURCE_CLEAN = new ClassPathResource("warn_clear_test_data.lua");
    public static final RedisScript<Void> SCRIPT_CLEAN = RedisScript.of(RESOURCE_CLEAN, Void.class);
    
    public static Mono<Void> clear(ReactiveStringRedisTemplate redisTemplate, String prefix) {
        return redisTemplate.execute(
                SCRIPT_CLEAN,
                Collections.singletonList(prefix)
            )
            .next();
    }
}
