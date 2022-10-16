plugins {
    application
    kotlin("jvm")
}

application {
    mainClass.set("com.github.pgreze.aidea.app.AppKt")
}

dependencies {
    implementation(project(":idea"))

    api(KotlinX.coroutines.core)
    implementation("com.github.ajalt.clikt:clikt:_")
    implementation("com.github.pgreze:kotlin-process:_")
}
