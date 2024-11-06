plugins {
    id("buildlogic.kotlin-application-conventions")
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "org.example"
version = "0.0.1-SNAPSHOT"
val springBootGroup = "org.springframework.boot"
val springCloudGroup = "org.springframework.cloud"

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("${springBootGroup}:spring-boot-starter-oauth2-client")

    implementation("${springCloudGroup}:spring-cloud-starter-gateway")

    testImplementation("${springBootGroup}:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

}

