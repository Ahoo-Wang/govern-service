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

package me.ahoo.cosky.rest.controller;

import me.ahoo.cosky.rest.dto.role.ResourceActionDto;
import me.ahoo.cosky.rest.security.rbac.RBACService;
import me.ahoo.cosky.rest.security.rbac.ResourceAction;
import me.ahoo.cosky.rest.security.rbac.annotation.AdminResource;
import me.ahoo.cosky.rest.support.RequestPathPrefix;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * @author ahoo wang
 */
@CrossOrigin("*")
@RestController
@RequestMapping(RequestPathPrefix.ROLES_PREFIX)
/**
 * TODO @AdminResource
 */
@AdminResource
public class RoleController {
    private final RBACService rbacService;

    public RoleController(RBACService rbacService) {
        this.rbacService = rbacService;
    }

    @GetMapping
    public Set<String> getAllRole() {
        return rbacService.getAllRole();
    }

    @PutMapping("/{roleName}")
    public void saveRole(@PathVariable String roleName, @RequestBody Set<ResourceActionDto> resourceActionBind) {
        rbacService.saveRole(roleName, resourceActionBind);
    }

    @DeleteMapping("/{roleName}")
    public void removeRole(@PathVariable String roleName) {
        rbacService.removeRole(roleName);
    }
}
