plugins {
    `application`
}

java {
    toolchain {
        languageVersion.convention(libs.versions.java.map(JavaLanguageVersion::of))
    }

    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.fastutil)
    implementation(libs.joml)
}

application {
    mainClass = "io.github.acodili_jg.molecules.client.main.Main"
}

tasks {
    withType<JavaCompile> {
        options.compilerArgs.add("-Xdiags:verbose")
        options.setDeprecation(true)
    }
}
