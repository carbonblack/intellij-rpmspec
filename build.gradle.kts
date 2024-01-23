import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.grammarkit.tasks.GenerateLexerTask
import org.jetbrains.grammarkit.tasks.GenerateParserTask
import org.jetbrains.intellij.tasks.RunIdeTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.com.diffplug.spotless)
    alias(libs.plugins.com.github.ben.manes.versions)
    alias(libs.plugins.io.gitlab.arturbosch.detekt)
    alias(libs.plugins.nl.littlerobots.version.catalog.update)
    alias(libs.plugins.org.gradle.test.retry)
    alias(libs.plugins.org.jetbrains.grammarkit)
    alias(libs.plugins.org.jetbrains.intellij)
    alias(libs.plugins.org.jetbrains.kotlin.jvm)
    java
}

spotless {
    kotlin {
        ktlint(libs.versions.com.pinterest.ktlint.get())
    }

    kotlinGradle {
        target("*.gradle.kts")
        ktlint(libs.versions.com.pinterest.ktlint.get())
    }
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom("$projectDir/detekt.yml")
}

group = "com.carbonblack"
version = "2.2.1"

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
    testImplementation(libs.io.mockk.mockk)
    testImplementation(libs.io.strikt.strikt.core)
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    type.set("IC")
    version.set(libs.versions.com.jetbrains.ideaIC)
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

tasks.withType<DependencyUpdatesTask> {
    fun isNonStable(version: String): Boolean {
        val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
        val regex = "^[0-9,.v-]+(-r)?$".toRegex()
        val isStable = stableKeyword || regex.matches(version)
        return isStable.not()
    }

    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
}
