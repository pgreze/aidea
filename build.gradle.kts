plugins {
    kotlin("jvm")
}

group = "com.github.pgreze.aidea"
version = project.findProperty("aidea_version")?.toString() ?: "WIP"

subprojects {
    plugins.withType(org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper::class) {
        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
            kotlinOptions.jvmTarget = "11"
        }

        tasks.withType<Test> {
            useJUnitPlatform()
            testLogging { events("passed", "skipped", "failed") }
        }

        dependencies {
            compileOnly(platform("org.jetbrains.kotlin:kotlin-bom:_"))
            implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

            testImplementation(platform(Testing.junit.bom))
            testImplementation("org.junit.jupiter:junit-jupiter-api")
            testImplementation("org.junit.jupiter:junit-jupiter-params")
            testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
            testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
            testImplementation("io.kotest:kotest-assertions-core:_")
        }
    }
}
