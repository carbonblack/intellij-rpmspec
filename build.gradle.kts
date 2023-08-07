import org.jetbrains.grammarkit.tasks.GenerateLexerTask
import org.jetbrains.grammarkit.tasks.GenerateParserTask
import org.jetbrains.intellij.tasks.RunIdeTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.diffplug.spotless") version "6.20.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.1"
    id("org.gradle.test-retry") version "1.5.4"
    id("org.jetbrains.grammarkit") version "2022.3.1"
    id("org.jetbrains.intellij") version "1.15.0"
    // See: https://plugins.jetbrains.com/docs/intellij/kotlin.html#kotlin-standard-library
    kotlin("jvm") version "1.8.20"
    java
}

spotless {
    val ktlintVersion = "0.50.0"

    kotlin {
        ktlint(ktlintVersion)
            .editorConfigOverride(mapOf("ktlint_experimentasl" to "enabled"))
    }

    kotlinGradle {
        target("*.gradle.kts")
        ktlint(ktlintVersion)
    }
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom("$projectDir/detekt.yml")
}

group = "com.carbonblack"
version = "2.1.0"

tasks.compileJava {
    options.release.set(17)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-Xlint:deprecation")
    options.compilerArgs.add("-Xlint:unchecked")
}

sourceSets {
    main {
        java.srcDir("$projectDir/src/main/gen")
    }
}

tasks.withType<Test> {
    environment("NO_FS_ROOTS_ACCESS_CHECK", "true")

    // Workaround for IDEA-278926
    // See https://youtrack.jetbrains.com/issue/IDEA-278926#focus=Comments-27-5561012.0-0
    isScanForTestClasses = false
    // Only run tests from classes that end with "Test"
    include("**/*Test.class")

    retry {
        maxRetries.set(2)
    }
}

tasks.withType<RunIdeTask> {
    jvmArgs("-Xmx2048m")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("io.mockk", "mockk", "1.13.5")
    testImplementation("io.strikt", "strikt-core", "0.34.1")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    type.set("IC")
    version.set("2023.2") // IntelliJ version and Kotlin version must match
    updateSinceUntilBuild.set(false)

    plugins.set(listOf("com.jetbrains.sh")) // , "au.com.glassechidna.luanalysis:1.2.2-IDEA203"))
}

val generateSpecParser = tasks.create<GenerateParserTask>("generateSpecParser") {
    sourceFile.set(file("$projectDir/src/main/grammars/RpmSpecParser.bnf"))
    targetRoot.set("$projectDir/src/main/gen")
    pathToParser.set("/com/carbonblack/intellij/rpmspec/parser/RpmSpecParser.java")
    pathToPsiRoot.set("/com/carbonblack/intellij/rpmspec/psi")
    purgeOldFiles.set(true)
}

val generateSpecLexer = tasks.create<GenerateLexerTask>("generateSpecLexer") {
    sourceFile.set(file("$projectDir/src/main/grammars/RpmSpecLexer.flex"))
    skeleton.set(file("$projectDir/src/main/grammars/idea-flex.skeleton"))
    targetDir.set("$projectDir/src/main/gen/com/carbonblack/intellij/rpmspec")
    targetClass.set("_RpmSpecLexer")
    purgeOldFiles.set(true)
}

val generateMacroParser = tasks.create<GenerateParserTask>("generateMacroParser") {
    sourceFile.set(file("$projectDir/src/main/grammars/RpmMacroParser.bnf"))
    targetRoot.set("$projectDir/src/main/gen")
    pathToParser.set("/com/carbonblack/intellij/rpmmacro/parser/RpmMacroParser.java")
    pathToPsiRoot.set("/com/carbonblack/intellij/rpmmacro/psi")
    purgeOldFiles.set(true)
}

val generateMacroLexer = tasks.create<GenerateLexerTask>("generateMacroLexer") {
    sourceFile.set(file("$projectDir/src/main/grammars/RpmMacroLexer.flex"))
    skeleton.set(file("$projectDir/src/main/grammars/idea-flex.skeleton"))
    targetDir.set("$projectDir/src/main/gen/com/carbonblack/intellij/rpmmacro")
    targetClass.set("_RpmMacroLexer")
    purgeOldFiles.set(true)
}

val generateShellLexer = tasks.create<GenerateLexerTask>("generateShellLexer") {
    sourceFile.set(file("$projectDir/src/main/grammars/RpmSpecShellLexer.flex"))
    skeleton.set(file("$projectDir/src/main/grammars/idea-flex.skeleton"))
    targetDir.set("$projectDir/src/main/gen/com/carbonblack/intellij/rpmspec/shell")
    targetClass.set("_RpmSpecShellLexer")
    purgeOldFiles.set(true)
}

val generateGrammars: TaskProvider<Task> = tasks.register("generateGrammars") {
    dependsOn(generateSpecParser, generateSpecLexer, generateMacroParser, generateMacroLexer, generateShellLexer)
}

tasks.withType<KotlinCompile> {
    dependsOn(generateGrammars)
}
