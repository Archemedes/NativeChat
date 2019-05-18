import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
//import sun.tools.jar.resources.jar

plugins {
    kotlin("jvm") version "1.3.31"
}

group = "me.lotc"
version = "0.3"

repositories {
    /*maven{
        url = uri("https://repo.lordofthecraft.net/artifactory/lotc-releases/")
        credentials{
            username = "${properties["mavenUser"]}"
            password = "${properties["mavenPassword"]}"
        }
    }*/

    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven { url = uri("https://oss.sonatype.org/content/groups/public/") }
    maven { url = uri("http://repo.dmulloy2.net/nexus/repository/public/") }
    mavenCentral()
    mavenLocal()
}

dependencies {
    compileOnly("net.lordofthecraft:OmniscienceAPI:1.0.3")
    compileOnly("net.lordofthecraft.arche:ArcheCore:3.6")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.4.0")
    compileOnly("co.lotc:tythan-bukkit:0.7")
    compileOnly("me.lucko.luckperms:luckperms-api:4.4")
    compileOnly("com.destroystokyo.paper:paper-api:1.13.2-R0.1-SNAPSHOT")
    compile(kotlin("stdlib-jdk8"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs = listOf("-Xjvm-default=enable")
}

tasks.withType<Jar> {
    from( configurations.runtime.get().map { if(it.isDirectory) it else zipTree(it) } )
}