import org.jetbrains.grammarkit.tasks.GenerateLexer
import org.jetbrains.grammarkit.tasks.GenerateParser
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.grammarkit") version "2021.1.3"
    id("org.jetbrains.intellij") version "1.1.4"
    // See: https://plugins.jetbrains.com/docs/intellij/kotlin.html#kotlin-standard-library
    kotlin("jvm") version "1.5.10"
    java
}

group = "com.carbonblack"
version = "1.2.0"

tasks.compileJava {
    options.release.set(11)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
    kotlinOptions.apiVersion = "1.4"
    kotlinOptions.freeCompilerArgs = listOf("-Xjvm-default=enable")
}

sourceSets{
    main {
        java.srcDir("$projectDir/src/main/gen")
    }
}

tasks.withType<Test> {
    environment("NO_FS_ROOTS_ACCESS_CHECK", "true")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("io.mockk", "mockk", "1.12.0")
    testImplementation("io.strikt", "strikt-core", "0.32.0")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    type.set("IC")
    version.set("2021.2") // IntelliJ version and Kotlin version must match
    updateSinceUntilBuild.set(false)
}

val generateSpecParser = tasks.create<GenerateParser>("generateSpecParser") {
    source = "$projectDir/src/main/grammars/RpmSpecParser.bnf"
    targetRoot = "$projectDir/src/main/gen"
    pathToParser = "/com/carbonblack/intellij/rpmspec/parser/RpmSpecParser.java"
    pathToPsiRoot = "/com/carbonblack/intellij/rpmspec/psi"
    purgeOldFiles = true
}

val generateSpecLexer = tasks.create<GenerateLexer>("generateSpecLexer") {
    source = "$projectDir/src/main/grammars/RpmSpecLexer.flex"
    targetDir = "$projectDir/src/main/gen/com/carbonblack/intellij/rpmspec"
    targetClass = "_RpmSpecLexer"
    purgeOldFiles = true
}

val generateMacroParser = tasks.create<GenerateParser>("generateMacroParser") {
    source = "$projectDir/src/main/grammars/RpmMacroParser.bnf"
    targetRoot = "$projectDir/src/main/gen"
    pathToParser = "/com/carbonblack/intellij/rpmmacro/parser/RpmMacroParser.java"
    pathToPsiRoot = "/com/carbonblack/intellij/rpmmacro/psi"
    purgeOldFiles = true
}

val generateMacroLexer = tasks.create<GenerateLexer>("generateMacroLexer") {
    source = "$projectDir/src/main/grammars/RpmMacroLexer.flex"
    targetDir = "$projectDir/src/main/gen/com/carbonblack/intellij/rpmmacro"
    targetClass = "_RpmMacroLexer"
    purgeOldFiles = true
}

val generateGrammars = tasks.register("generateGrammars") {
    dependsOn(generateSpecParser, generateSpecLexer, generateMacroParser, generateMacroLexer)
}

tasks.withType<KotlinCompile> {
    dependsOn(generateGrammars)
}
