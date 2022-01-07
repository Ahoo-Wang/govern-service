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

package me.ahoo.cosky.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author ahoo wang
 */
public class RedisKeysTest {

    @Test
    void isCluster() {

    }

    @Test
    void ofKey() {

        String key = RedisKeys.ofKey(false, "dev");
        Assertions.assertEquals("dev", key);

        key = RedisKeys.ofKey(false, "{dev}");
        Assertions.assertEquals("{dev}", key);

        String clusterKey = RedisKeys.ofKey(true, "dev");
        Assertions.assertEquals("{dev}", clusterKey);

        clusterKey = RedisKeys.ofKey(true, "{dev}");
        Assertions.assertEquals("{dev}", clusterKey);
    }


    @Test
    void hasWrap() {
        Assertions.assertFalse(RedisKeys.hasWrap("dev"));
        Assertions.assertFalse(RedisKeys.hasWrap("{dev"));
        Assertions.assertFalse(RedisKeys.hasWrap("dev}"));
        Assertions.assertTrue(RedisKeys.hasWrap("{dev}"));
        Assertions.assertTrue(RedisKeys.hasWrap("{{dev}"));
        Assertions.assertTrue(RedisKeys.hasWrap("{dev}}"));
        Assertions.assertTrue(RedisKeys.hasWrap("{{dev}}"));
    }

    @Test
    void wrap() {
        Assertions.assertEquals("{dev}", RedisKeys.wrap("dev"));
    }

    @Test
    void unwrap() {
        Assertions.assertEquals("dev", RedisKeys.unwrap("{dev}"));
        Assertions.assertEquals("dev", RedisKeys.unwrap("cosky-{dev}"));
        Assertions.assertEquals("{dev", RedisKeys.unwrap("cosky-{{dev}"));
        Assertions.assertEquals("{dev", RedisKeys.unwrap("cosky-{{dev}}"));
        Assertions.assertEquals("dev", RedisKeys.unwrap("cosky-{dev}-cosky"));
    }

}
