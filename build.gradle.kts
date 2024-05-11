plugins {
    kotlin("multiplatform") version "1.9.23"
    id("io.kotest.multiplatform") version "5.8.1"
    kotlin("plugin.serialization") version "1.9.23"
    application
}

group = "ru.altmanea"
version = "1.0-SNAPSHOT"

val kotlinWrappers = "org.jetbrains.kotlin-wrappers"
val kotlinWrappersVersion = "1.0.0-pre.490"
val serializationVersion = "1.6.3"
val ktorVersion = "2.3.9"
val kmongoVersion = "4.11.0"
val kotestVersion = "5.8.1"
val logbackVersion = "1.5.3"
val arrowVersion = "1.2.3"
val kotlinHtmlVersion = "0.11.0"

repositories {
    jcenter()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

kotlin {
    jvm {
        jvmToolchain(17)
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-core:$ktorVersion")
                implementation("io.ktor:ktor-server-netty:$ktorVersion")
                implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation("io.ktor:ktor-server-html-builder-jvm:$ktorVersion")
                implementation("io.ktor:ktor-server-auth:$ktorVersion")
                implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")
                implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
                implementation("ch.qos.logback:logback-classic:$logbackVersion")
                implementation("org.litote.kmongo:kmongo-serialization:$kmongoVersion")
            }
        }
        val jvmTest by getting {
            dependencies {
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(
                    project.dependencies.enforcedPlatform(
                        "$kotlinWrappers:kotlin-wrappers-bom:$kotlinWrappersVersion"
                    )
                )
                implementation("$kotlinWrappers:kotlin-emotion")
                implementation("$kotlinWrappers:kotlin-react")
                implementation("$kotlinWrappers:kotlin-react-dom")
                implementation("$kotlinWrappers:kotlin-react-router-dom")
                implementation("$kotlinWrappers:kotlin-react-redux")
                implementation("$kotlinWrappers:kotlin-tanstack-react-query")
                implementation("$kotlinWrappers:kotlin-tanstack-react-query-devtools")
                implementation(npm("cross-fetch", "3.1.5"))
            }
        }
        val jsTest by getting
    }
}

application {
    mainClass.set("ru.altmanea.webapp.ServerKt")
}

tasks.named<Copy>("jvmProcessResources") {
    val jsBrowserDistribution = tasks.named("jsBrowserDistribution")
    from(jsBrowserDistribution)
}

tasks.named<JavaExec>("run") {
    dependsOn(tasks.named<Jar>("jvmJar"))
    classpath(tasks.named<Jar>("jvmJar"))
}