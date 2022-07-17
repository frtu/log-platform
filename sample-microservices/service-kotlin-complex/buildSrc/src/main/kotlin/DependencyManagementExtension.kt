object Versions {
    const val java = "11"

    // Allow to enable >1.4 syntax
    const val language = "1.4"
    const val kotlin = "1.5.32"
    const val kotlinx = "1.5.2"

    const val jackson = "2.13.2.1" // 2022-03-30
    const val r2dbc = "Arabba-SR13"  // 2022-03-18
    const val spring_boot = "2.7.1"
    const val springdoc = "1.5.4"
    const val frtu_base = "1.2.5-SNAPSHOT"
    const val frtu_libs = "1.2.2"
    const val frtu_logs = "1.1.4"

    const val opentelemetry = "1.16.0" // 2022-07-13
    const val jaeger = "1.8.0"
    const val spring_cloud_sleuth = "3.1.3"
    const val spring_cloud_sleuth_otel = "1.1.0-M6"

    const val plugin_jacoco = "0.8.8" // 2022-04-05
    const val plugin_google_format = "0.9" // 2020-06-09
}

object Libs {
    //---------- COMMONS -----------
    const val jackson_databind = "com.fasterxml.jackson.core:jackson-databind"
    const val jackson_module_kotlin = "com.fasterxml.jackson.module:jackson-module-kotlin"
    const val jackson_datatype_jsr310 = "com.fasterxml.jackson.datatype:jackson-datatype-jsr310"
    const val jackson_yaml = "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml"
    const val lib_utils = "com.github.frtu.libs:lib-utils"
    const val spring_core = "org.springframework:spring-core"

    //---------- COROUTINE -----------
    const val coroutines_core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinx}"
    const val coroutines_reactive = "org.jetbrains.kotlinx:kotlinx-coroutines-reactive:${Versions.kotlinx}"
    const val coroutines_reactor = "org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${Versions.kotlinx}"
    const val coroutines_reactor_extensions = "io.projectreactor.kotlin:reactor-kotlin-extensions"

    //---------- OBSERVABILITY -----------
    const val opentelemetry_sdk = "io.opentelemetry:opentelemetry-sdk"
    const val opentelemetry_trace_propagators = "io.opentelemetry:opentelemetry-extension-trace-propagators"
    const val opentelemetry_semconv = "io.opentelemetry:opentelemetry-semconv:${Versions.opentelemetry}-alpha"
    const val opentelemetry_shim = "io.opentelemetry:opentelemetry-opentracing-shim:${Versions.opentelemetry}-alpha"

    const val opentelemetry_exporter = "io.opentelemetry:opentelemetry-exporter-jaeger"
    const val trace_impl = "io.jaegertracing:jaeger-client:${Versions.jaeger}"

    //---------- LOGS -----------
    // Implementation for slf4j
    const val log_impl = "ch.qos.logback:logback-classic"
    const val logger_core = "com.github.frtu.logs:logger-core"

    //---------- LIBS BOM -----------
    const val bom_jackson = "com.fasterxml.jackson:jackson-bom:${Versions.jackson}"
    const val bom_r2dbc = "io.r2dbc:r2dbc-bom:${Versions.r2dbc}"
    const val bom_kotlin_base = "com.github.frtu.archetype:kotlin-base-pom:${Versions.frtu_base}"
    const val bom_kotlin_libs = "com.github.frtu.libs:lib-kotlin-bom:${Versions.frtu_libs}"

    const val bom_logger = "com.github.frtu.logs:logger-bom:${Versions.frtu_logs}"
    const val bom_opentelemetry = "io.opentelemetry:opentelemetry-bom:${Versions.opentelemetry}"
    const val bom_opentelemetry_alpha = "io.opentelemetry:opentelemetry-bom-alpha:${Versions.opentelemetry}-alpha"
    const val bom_spring_cloud_sleuth = "org.springframework.cloud:spring-cloud-sleuth-dependencies:${Versions.spring_cloud_sleuth}"
    const val bom_spring_cloud_sleuth_otel = "org.springframework.cloud:spring-cloud-sleuth-otel-dependencies:${Versions.spring_cloud_sleuth_otel}"

    //---------- TESTS -----------
    const val junit = "org.junit.jupiter:junit-jupiter"
    // Mock lib mockk or mockito
    const val mock = "io.mockk:mockk"
    const val assertions = "org.assertj:assertj-core"
}
