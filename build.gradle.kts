import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import sun.tools.jar.resources.jar

plugins {
    kotlin("jvm") version "1.3.11"
}

group = "co.lotc"
version = "0.1"

repositories {
    maven{
        url = uri("https://repo.lordofthecraft.net/artifactory/lotc-releases/")
        credentials{
            username = "${properties["mavenUser"]}"
            password = "${properties["mavenPassword"]}"
        }
    }

    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://oss.sonatype.org/content/groups/public/") }
    mavenCentral()
}

dependencies {
    compileOnly("co.lotc:tythan-bukkit:0.3")
    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT")
    compile(kotlin("stdlib-jdk8"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Jar> {
    from(configurations.runtime.map { if (it.isDirectory) it else zipTree(it) })
}