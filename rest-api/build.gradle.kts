plugins {
    application
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

tasks.jar.configure {
    exclude("application.yaml", "bootstrap.yaml")
}

distributions {
    main {
        contents {
            val dashboardDistPath = "${rootDir.absolutePath}/dashboard/dist";
            from(dashboardDistPath).include("**")
        }
    }
}

application {
    mainClass.set("me.ahoo.govern.rest.RestApiServer")

    applicationDefaultJvmArgs = listOf(
        "-Xms512M",
        "-Xmx512M",
        "-server",
        "-XX:+UseG1GC",
        "-Xlog:gc*:file=logs/rest-api-gc.log:time,tags:filecount=10,filesize=100M",
        "-Dspring.cloud.bootstrap.enabled=true",
        "-Dspring.cloud.bootstrap.location=config/bootstrap.yaml"
    )
}


dependencies {
    implementation(platform(project(":dependencies")))
    implementation("io.springfox:springfox-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation(project(":spring-cloud-starter-config"))
    implementation(project(":spring-cloud-starter-discovery"))
    implementation("com.google.guava:guava")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    compileOnly("org.projectlombok:lombok:${rootProject.ext.get("lombokVersion")}")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:${rootProject.ext.get("springBootVersion")}")
    annotationProcessor("org.projectlombok:lombok:${rootProject.ext.get("lombokVersion")}")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
