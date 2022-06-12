import org.gradle.api.tasks.diagnostics.internal.dependencies.AsciiDependencyReportRenderer
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    java
    application
    kotlin("jvm")
    kotlin("plugin.spring") version "1.5.32" apply false
    id("org.springframework.boot") version Versions.spring_boot
    jacoco
    pmd
    id("com.github.sherter.google-java-format") version Versions.plugin_google_format
}

group = "com.github.frtu.sample.logs"

apply(plugin = "java")
apply(plugin = "org.jetbrains.kotlin.jvm")
apply(plugin = "org.jetbrains.kotlin.plugin.spring")
apply(plugin = "jacoco")
apply(plugin = "com.github.sherter.google-java-format")
apply(plugin = "project-report")
apply(plugin = "io.spring.dependency-management")

task("allDependencies", DependencyReportTask::class) {
    evaluationDependsOnChildren()
    this.setRenderer(AsciiDependencyReportRenderer())
}
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
        languageVersion = Versions.language
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}
java {
    sourceCompatibility = JavaVersion.toVersion("11")
    targetCompatibility = JavaVersion.toVersion("11")
    withSourcesJar()
}

jacoco {
    toolVersion = Versions.plugin_jacoco
}
tasks {
    test {
        useJUnitPlatform()
    }
    jacocoTestCoverageVerification {
        violationRules {
            // Configure the ratio based on your standard
            rule { limit { minimum = BigDecimal.valueOf(0.0) } }
        }
    }
    check {
        dependsOn(jacocoTestCoverageVerification)
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(Libs.logger_opentelemetry)

    // Spring Reactive
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springdoc:springdoc-openapi-webflux-ui:${Versions.springdoc}")

    // Spring Boot
//    implementation("org.springframework.boot:spring-boot-starter-web")
//    implementation("org.springdoc:springdoc-openapi-ui:${Versions.springdoc}")

    // Storage
//    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
//    implementation("io.r2dbc:r2dbc-h2")
//    runtimeOnly("com.h2database:h2")
//    implementation("io.r2dbc:r2dbc-postgresql")
//    runtimeOnly("org.postgresql:postgresql")

//    runtimeOnly("org.springframework.boot:spring-boot-starter-jdbc")
//    runtimeOnly("org.flywaydb:flyway-core")

    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // DevTools and Monitoring
    implementation("org.springframework.boot:spring-boot-devtools")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Serialization
    implementation(Libs.jackson_databind)
    implementation(Libs.jackson_module_kotlin)
    implementation(Libs.jackson_datatype_jsr310)

    // Platform - Coroutine
    implementation(Libs.coroutines_reactor)

    // Platform - Observability
    implementation(Libs.opentelemetry_sdk)
    implementation(Libs.opentelemetry_trace_propagators)
    implementation(Libs.opentelemetry_semconv)
    implementation(Libs.opentelemetry_shim)
    implementation(Libs.opentelemetry_exporter)
    implementation(Libs.trace_impl)

    // Platform - Log
    implementation(Libs.logger_core)
    implementation(Libs.log_impl)
    testImplementation(Libs.lib_utils)
    testImplementation(Libs.spring_core)

    // Platform test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")

    // Test
    testImplementation(Libs.junit)
    testImplementation(Libs.mock)
    testImplementation(Libs.assertions)
    testImplementation(kotlin("test"))

    // Platform - BOMs
    implementation(platform(kotlin("bom")))
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
}

the<DependencyManagementExtension>().apply {
    imports {
        mavenBom(SpringBootPlugin.BOM_COORDINATES)
        mavenBom(Libs.bom_jackson)
//        mavenBom(Libs.bom_kotlin_base)
        mavenBom(Libs.bom_kotlin_libs)
        mavenBom(Libs.bom_logger)
        mavenBom(Libs.bom_opentelemetry)
    }
}

application {
    // Define the main class for the application.
    mainClass.set("com.github.frtu.sample.logs.ApplicationKt")
}