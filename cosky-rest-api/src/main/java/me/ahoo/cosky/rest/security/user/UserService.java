/*
 * Copyright [2021-2021] [ahoo wang <ahoowang@qq.com> (https://github.com/Ahoo-Wang)].
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

package me.ahoo.cosky.rest.security.user;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;
import lombok.extern.slf4j.Slf4j;
import me.ahoo.cosky.core.Namespaced;
import me.ahoo.cosky.core.redis.RedisConnectionFactory;
import me.ahoo.cosky.rest.dto.user.LoginResponse;
import me.ahoo.cosky.rest.security.CoSkySecurityException;
import me.ahoo.cosky.rest.security.JwtProvider;
import net.bytebuddy.utility.RandomString;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author ahoo wang
 */
@Slf4j
@Service
public class UserService {

    public static final String USER_IDX = Namespaced.SYSTEM + ":user_idx";
    public static final String USER_ROLE_BIND = Namespaced.SYSTEM + ":user_role_bind:%s";
    public static final String USER_LOGIN_LOCK = Namespaced.SYSTEM + ":login_lock:%s";
    private final JwtProvider jwtProvider;
    private final RedisClusterCommands<String, String> redisCommands;

    public UserService(JwtProvider jwtProvider, RedisConnectionFactory redisConnectionFactory) {
        this.jwtProvider = jwtProvider;
        this.redisCommands = redisConnectionFactory.getShareSyncCommands();
    }

    private static String getUserRoleBindKey(String username) {
        return Strings.lenientFormat(USER_ROLE_BIND, username);
    }

    public boolean initRoot(boolean enforce) {

        if (enforce) {
            removeUser(User.SUPER_USER);
        }

        final String coskyPwd = RandomString.make(10);
        if (addUser(User.SUPER_USER, coskyPwd)) {
            printSuperUserPwd(coskyPwd);
            return true;
        }
        return false;
    }

    private void printSuperUserPwd(String coskyPwd) {
        System.out.println(Strings.lenientFormat("---------------- ****** CoSky -  init super user:[%s] password:[%s] ****** ----------------", User.SUPER_USER, coskyPwd));
    }

    public List<User> query() {
        return redisCommands.hkeys(USER_IDX).stream().map(username -> {
            Set<String> roleBind = getRoleBind(username);
            User user = new User();
            user.setUsername(username);
            user.setRoleBind(roleBind);
            return user;
        }).collect(Collectors.toList());
    }

    public boolean existsUser(String username) {
        return redisCommands.hexists(USER_IDX, username);
    }

    public boolean addUser(String username, String pwd) {
        return redisCommands.hsetnx(USER_IDX, username, encodePwd(pwd));
    }

    public boolean removeUser(String username) {
        String userRoleBindKey = getUserRoleBindKey(username);
        redisCommands.del(userRoleBindKey);
        return redisCommands.hdel(USER_IDX, username) > 0;
    }

    public void bindRole(String username, Set<String> roleBind) {
        String userRoleBindKey = getUserRoleBindKey(username);
        redisCommands.del(userRoleBindKey);
        for (String role : roleBind) {
            redisCommands.sadd(userRoleBindKey, role);
        }
    }

    public Set<String> getRoleBind(String username) {
        String userRoleBindKey = getUserRoleBindKey(username);
        return redisCommands.smembers(userRoleBindKey);
    }

    public void changePwd(String username, String oldPwd, String newPwd) {
        oldPwd = encodePwd(oldPwd);
        newPwd = encodePwd(newPwd);
        String prePwd = redisCommands.hget(USER_IDX, username);

        Preconditions.checkNotNull(prePwd, Strings.lenientFormat("username:[%s] not exists!", username));

        if (!prePwd.equals(oldPwd)) {
            throw new CoSkySecurityException(Strings.lenientFormat("username:[%s] - old password is incorrect!", username));
        }

        redisCommands.hset(USER_IDX, username, newPwd);
    }

    public static final int MAX_LOGIN_ERROR_TIMES = 10;
    public static final long LOGIN_LOCK_EXPIRE = Duration.ofMinutes(15).toMillis();
    public static final long MAX_LOGIN_LOCK_EXPIRE = Duration.ofDays(3).toMillis();

    public LoginResponse login(String username, String pwd) throws CoSkySecurityException {
        String loginLockKey = Strings.lenientFormat(USER_LOGIN_LOCK, username);

        long tryCount = redisCommands.incr(loginLockKey);
        long expansion = (int) Math.max(tryCount / MAX_LOGIN_ERROR_TIMES, 1);
        final long loginLockExpire = Math.min(LOGIN_LOCK_EXPIRE * expansion, MAX_LOGIN_LOCK_EXPIRE);

        redisCommands.pexpire(loginLockKey, loginLockExpire);
        if (tryCount > MAX_LOGIN_ERROR_TIMES) {
            throw new CoSkySecurityException(Strings.lenientFormat("User:[%s] sign in freezes for [%s] minutes,Too many:[%s] sign in errors!", username, Duration.ofMillis(loginLockExpire).toMinutes(), tryCount));
        }

        String realPwd = redisCommands.hget(USER_IDX, username);

        if (Strings.isNullOrEmpty(realPwd)) {
            throw new CoSkySecurityException(Strings.lenientFormat("username:[%s] not exists!", username));
        }

        String encodedPwd = encodePwd(pwd);
        if (!realPwd.equals(encodedPwd)) {
            throw new CoSkySecurityException(Strings.lenientFormat("username:[%s] - password is incorrect.!", username));
        }
        Set<String> roleBind = getRoleBind(username);
        User user = new User();
        user.setUsername(username);
        user.setRoleBind(roleBind);
        return jwtProvider.generateToken(user);
    }

    private String encodePwd(String pwd) {
        return Hashing.sha256().hashString(pwd, Charsets.UTF_8).toString();
    }

    public void unlock(String username) {
        String loginLockKey = Strings.lenientFormat(USER_LOGIN_LOCK, username);
        redisCommands.del(loginLockKey);
    }

    public void logout() {

    }
}
