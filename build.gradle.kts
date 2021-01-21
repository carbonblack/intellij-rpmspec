import org.jetbrains.grammarkit.tasks.GenerateLexer
import org.jetbrains.grammarkit.tasks.GenerateParser
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.grammarkit") version "2020.3.2"
    id("org.jetbrains.intellij") version "0.6.5"
    kotlin("jvm") version "1.4.21"
    java
}

group = "com.carbonblack"
version = "1.2.0"

tasks.compileJava {
    @Suppress("UnstableApiUsage")
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
    jcenter()
}

dependencies {
    testImplementation("io.mockk", "mockk", "1.10.4")
    testImplementation("io.strikt", "strikt-core", "0.28.1")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    type = "IC"
    version = "2020.3"
    updateSinceUntilBuild = false
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
