import org.jetbrains.grammarkit.tasks.GenerateLexerTask
import org.jetbrains.grammarkit.tasks.GenerateParserTask
import org.jetbrains.intellij.tasks.RunIdeTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.grammarkit") version "2021.2.1"
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

tasks.withType<RunIdeTask> {
    jvmArgs("-Xmx2048m")
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

    plugins.set(listOf("com.jetbrains.sh"))
}

val generateSpecParser = tasks.create<GenerateParserTask>("generateSpecParser") {
    source.set("$projectDir/src/main/grammars/RpmSpecParser.bnf")
    targetRoot.set("$projectDir/src/main/gen")
    pathToParser.set("/com/carbonblack/intellij/rpmspec/parser/RpmSpecParser.java")
    pathToPsiRoot.set("/com/carbonblack/intellij/rpmspec/psi")
    purgeOldFiles.set(true)
}

val generateSpecLexer = tasks.create<GenerateLexerTask>("generateSpecLexer") {
    source.set("$projectDir/src/main/grammars/RpmSpecLexer.flex")
    skeleton.set(file("$projectDir/src/main/grammars/idea-flex.skeleton"))
    targetDir.set("$projectDir/src/main/gen/com/carbonblack/intellij/rpmspec")
    targetClass.set("_RpmSpecLexer")
    purgeOldFiles.set(true)
}

val generateMacroParser = tasks.create<GenerateParserTask>("generateMacroParser") {
    source.set("$projectDir/src/main/grammars/RpmMacroParser.bnf")
    targetRoot.set("$projectDir/src/main/gen")
    pathToParser.set("/com/carbonblack/intellij/rpmmacro/parser/RpmMacroParser.java")
    pathToPsiRoot.set("/com/carbonblack/intellij/rpmmacro/psi")
    purgeOldFiles.set(true)
}

val generateMacroLexer = tasks.create<GenerateLexerTask>("generateMacroLexer") {
    source.set("$projectDir/src/main/grammars/RpmMacroLexer.flex")
    skeleton.set(file("$projectDir/src/main/grammars/idea-flex.skeleton"))
    targetDir.set("$projectDir/src/main/gen/com/carbonblack/intellij/rpmmacro")
    targetClass.set("_RpmMacroLexer")
    purgeOldFiles.set(true)
}

val generateShellLexer = tasks.create<GenerateLexerTask>("generateShellLexer") {
    source.set("$projectDir/src/main/grammars/RpmSpecShellLexer.flex")
    skeleton.set(file("$projectDir/src/main/grammars/idea-flex.skeleton"))
    targetDir.set("$projectDir/src/main/gen/com/carbonblack/intellij/rpmspec/shell")
    targetClass.set("_RpmSpecShellLexer")
    purgeOldFiles.set(true)
}

val generateGrammars = tasks.register("generateGrammars") {
    dependsOn(generateSpecParser, generateSpecLexer, generateMacroParser, generateMacroLexer, generateShellLexer)
}

tasks.withType<KotlinCompile> {
    dependsOn(generateGrammars)
}
