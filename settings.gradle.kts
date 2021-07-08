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

/*
 * This file was generated by the Gradle 'init' task.
 *
 * The settings file is used to specify which projects to include in your build.
 *
 * Detailed information about configuring a multi-project build in Gradle can be found
 * in the user manual at https://docs.gradle.org/7.0/userguide/multi_project_builds.html
 */
rootProject.name = "cosky"

include(":cosky-core")
include(":cosky-config")
include(":cosky-discovery")
include(":cosky-bom")
include(":cosky-dependencies")
include(":cosky-spring-cloud-core")
include(":spring-cloud-starter-cosky-config")
include(":spring-cloud-starter-cosky-discovery")
include(":spring-cloud-starter-cosky-discovery-ribbon")
include(":cosky-rest-api")
include(":cosky-mirror")

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("io.codearte.gradle.nexus:gradle-nexus-staging-plugin:0.30.0")
    }
}

include(":cosky-service-provider")
project(":cosky-service-provider").projectDir = file("examples/cosky-service-provider")

include(":cosky-service-provider-api")
project(":cosky-service-provider-api").projectDir = file("examples/cosky-service-provider-api")

include(":cosky-service-consumer")
project(":cosky-service-consumer").projectDir = file("examples/cosky-service-consumer")
