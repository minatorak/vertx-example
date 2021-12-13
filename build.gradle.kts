import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.psi.classInitializerVisitor

plugins {
  kotlin ("jvm") version "1.6.0"
  application
  id("com.github.johnrengelman.shadow") version "7.0.0"
  id("org.graalvm.buildtools.native") version "0.9.4"
}

group = "com.example"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
  gradlePluginPortal()
}

val vertxVersion = "4.2.1"
val junitJupiterVersion = "5.8.1"

val mainVerticleName = "com.example.vertx_kotlin.MainVerticle"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
  mainClass.set(launcherClassName)
}

dependencies {
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-config:$vertxVersion")
  implementation("io.vertx:vertx-web-client:$vertxVersion")
  implementation("io.vertx:vertx-web:4.2.1")
  implementation("io.vertx:vertx-lang-kotlin-coroutines:$vertxVersion")
  implementation("io.vertx:vertx-micrometer-metrics:$vertxVersion")
  implementation("io.vertx:vertx-mssql-client:$vertxVersion")
  implementation("io.vertx:vertx-kafka-client:$vertxVersion")
  implementation("io.vertx:vertx-lang-kotlin:$vertxVersion")
  implementation("io.vertx:vertx-redis-client:$vertxVersion")
  implementation(kotlin("stdlib-jdk8", "1.6.0"))
//  testImplementation("io.vertx:vertx-junit5:4.2.1")
//  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "11"

tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  manifest {
    attributes(mapOf("Main-Verticle" to mainVerticleName))
  }
  mergeServiceFiles()
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

tasks.withType<JavaExec> {
  args = listOf("run", mainVerticleName, "--redeploy=$watchForChange", "--launcher-class=$launcherClassName", "--on-redeploy=$doOnChange")
}


nativeBuild {
  javaLauncher.set(javaToolchains.launcherFor {
    languageVersion.set(JavaLanguageVersion.of(11))
    vendor.set(JvmVendorSpec.matching("GraalVM Enterprise"))
  })

  // Main options
  imageName.set("application") // The name of the native image, defaults to the project name
  mainClass.set(launcherClassName) // The main class to use, defaults to the application.mainClass
  debug.set(true) // Determines if debug info should be generated, defaults to false
  verbose.set(true) // Add verbose output, defaults to false
  fallback.set(true) // Sets the fallback mode of native-image, defaults to false
  sharedLibrary.set(false) // Determines if image is a shared library, defaults to false if `java-library` plugin isn't included
}
