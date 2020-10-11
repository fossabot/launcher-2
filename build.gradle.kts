import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version Project.kotlinVersion
    openjfx
    shadowjar
    application
}

tasks.withType<Wrapper> {
    gradleVersion = Project.gradleVersion
}

group = "org.spectral.launcher"
version = Project.version

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(Library.tinylogApi)
    implementation(Library.tinylogImpl)
    implementation(Library.jaxb)
    implementation(Library.jaxbRuntime)
    implementation(Library.tornadofx)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = Project.jvmVersion
}

javafx {
    version = "11"
    modules = listOf("javafx.base", "javafx.fxml", "javafx.graphics", "javafx.controls", "javafx.swing")
}

application {
    mainClassName = "org.spectral.launcher.gui.LauncherApp"
}
