import org.jetbrains.grammarkit.tasks.GenerateLexer
import org.jetbrains.grammarkit.tasks.GenerateParser
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Arrays.asList

plugins {
    id("org.jetbrains.grammarkit") version "2019.1"
    id("org.jetbrains.intellij") version "0.4.8"
    kotlin("jvm") version "1.3.31"
    java
}

repositories {
    maven("https://artifactory-pub.bit9.local:443/artifactory/java-all-release-virtual")
}

group = "com.carbonblack"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs = asList("-Xjvm-default=enable")
}

sourceSets{
    main {
        java.srcDir("$projectDir/src/main/gen")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "2019.1.2"
}

val generateParser = tasks.create("generateParser", GenerateParser::class) {
    source = "$projectDir/src/main/grammars/RpmSpecParser.bnf"
    targetRoot = "$projectDir/src/main/gen"
    pathToParser = "/com/carbonblack/intellij/rpmspec/parser/RpmSpecParser.java"
    pathToPsiRoot = "/com/carbonblack/intellij/rpmspec/psi"
    purgeOldFiles = true
}

val generateLexer = tasks.create("generateLexer", GenerateLexer::class) {
    source = "$projectDir/src/main/grammars/RpmSpecLexer.flex"
    targetDir = "$projectDir/src/main/gen/com/carbonblack/intellij/rpmspec"
    targetClass = "_RpmSpecLexer"
    purgeOldFiles = true
}

val generateGrammars = tasks.register("generateGrammars") {
    dependsOn(generateParser, generateLexer)
}

tasks.withType<KotlinCompile> {
    dependsOn(generateGrammars)
}
