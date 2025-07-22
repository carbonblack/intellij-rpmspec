
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.grammarkit.tasks.GenerateLexerTask
import org.jetbrains.grammarkit.tasks.GenerateParserTask
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.com.diffplug.spotless)
    alias(libs.plugins.com.github.ben.manes.versions)
    alias(libs.plugins.io.gitlab.arturbosch.detekt)
    alias(libs.plugins.nl.littlerobots.version.catalog.update)
    alias(libs.plugins.org.gradle.test.retry)
    alias(libs.plugins.org.jetbrains.grammarkit)
    alias(libs.plugins.org.jetbrains.intellij.platform)
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
version = "2.3.0"

// See http://www.jetbrains.org/intellij/sdk/docs/basics/getting_started/build_number_ranges.html for Java target
val javaVersion = JavaVersion.VERSION_21
tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(javaVersion.toString()))
    }
}

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
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

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity(libs.versions.com.jetbrains.ideaIC)
        bundledPlugin("com.jetbrains.sh")

        testFramework(TestFrameworkType.Platform)
    }

    testImplementation(libs.io.mockk.mockk)
    testImplementation(libs.io.strikt.strikt.core)
    testImplementation(libs.junit.junit)
}

intellijPlatform {
    pluginVerification {
        freeArgs = listOf("-mute", "TemplateWordInPluginId")
        ides {
            ide(IntelliJPlatformType.IntellijIdeaCommunity, libs.versions.com.jetbrains.ideaIC.get())
            recommended()
        }
    }
}

val generateSpecParser = tasks.register<GenerateParserTask>("generateSpecParser") {
    sourceFile.set(file("$projectDir/src/main/grammars/RpmSpecParser.bnf"))
    targetRootOutputDir.set(file("$projectDir/src/main/gen"))
    pathToParser.set("/com/carbonblack/intellij/rpmspec/parser/RpmSpecParser.java")
    pathToPsiRoot.set("/com/carbonblack/intellij/rpmspec/psi")
    purgeOldFiles.set(true)
}

val generateSpecLexer = tasks.register<GenerateLexerTask>("generateSpecLexer") {
    sourceFile.set(file("$projectDir/src/main/grammars/RpmSpecLexer.flex"))
    skeleton.set(file("$projectDir/src/main/grammars/idea-flex.skeleton"))
    targetOutputDir.set(file("$projectDir/src/main/gen/com/carbonblack/intellij/rpmspec"))
    purgeOldFiles.set(false)
}

val generateMacroParser = tasks.register<GenerateParserTask>("generateMacroParser") {
    sourceFile.set(file("$projectDir/src/main/grammars/RpmMacroParser.bnf"))
    targetRootOutputDir.set(file("$projectDir/src/main/gen"))
    pathToParser.set("/com/carbonblack/intellij/rpmmacro/parser/RpmMacroParser.java")
    pathToPsiRoot.set("/com/carbonblack/intellij/rpmmacro/psi")
    purgeOldFiles.set(true)
}

val generateMacroLexer = tasks.register<GenerateLexerTask>("generateMacroLexer") {
    sourceFile.set(file("$projectDir/src/main/grammars/RpmMacroLexer.flex"))
    skeleton.set(file("$projectDir/src/main/grammars/idea-flex.skeleton"))
    targetOutputDir.set(file("$projectDir/src/main/gen/com/carbonblack/intellij/rpmmacro"))
    purgeOldFiles.set(true)
}

val generateShellLexer = tasks.register<GenerateLexerTask>("generateShellLexer") {
    sourceFile.set(file("$projectDir/src/main/grammars/RpmSpecShellLexer.flex"))
    skeleton.set(file("$projectDir/src/main/grammars/idea-flex.skeleton"))
    targetOutputDir.set(file("$projectDir/src/main/gen/com/carbonblack/intellij/rpmspec/shell"))
    purgeOldFiles.set(true)
}

val generateGrammars = tasks.register("generateGrammars") {
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
