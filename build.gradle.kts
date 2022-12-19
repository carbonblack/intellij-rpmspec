import org.jetbrains.grammarkit.tasks.GenerateLexerTask
import org.jetbrains.grammarkit.tasks.GenerateParserTask
import org.jetbrains.intellij.tasks.RunIdeTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.diffplug.spotless") version "6.12.0"
    id("io.gitlab.arturbosch.detekt") version "1.22.0"
    id("org.gradle.test-retry") version "1.5.0"
    id("org.jetbrains.grammarkit") version "2022.3"
    id("org.jetbrains.intellij") version "1.11.0"
    // See: https://plugins.jetbrains.com/docs/intellij/kotlin.html#kotlin-standard-library
    kotlin("jvm") version "1.7.0"
    java
}

spotless {
    val ktlintVersion = "0.46.1"

    val ktlintOverrides = mapOf(
        "disabled_rules" to "filename,no-wildcard-imports,string-template",
        "ij_kotlin_allow_trailing_comma" to "true",
        "ij_kotlin_allow_trailing_comma_on_call_site" to "true",
    )

    kotlin {
        ktlint(ktlintVersion)
            .setUseExperimental(true)
            .editorConfigOverride(ktlintOverrides)
    }

    kotlinGradle {
        target("*.gradle.kts")
        ktlint(ktlintVersion)
            .setUseExperimental(true)
            .editorConfigOverride(ktlintOverrides)
    }
}

detekt {
    buildUponDefaultConfig = true
    config = files("$projectDir/detekt.yml")
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
    testImplementation("io.mockk", "mockk", "1.13.3")
    testImplementation("io.strikt", "strikt-core", "0.34.1")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    type.set("IC")
    version.set("2022.3") // IntelliJ version and Kotlin version must match
    updateSinceUntilBuild.set(false)

    plugins.set(listOf("com.jetbrains.sh")) // , "au.com.glassechidna.luanalysis:1.2.2-IDEA203"))
}

val generateSpecParser = tasks.create<GenerateParserTask>("generateSpecParser") {
    source.set("$projectDir/src/main/grammars/RpmSpecParser.bnf")
    targetRoot.set("$projectDir/src/main/gen")
    pathToParser.set("/com/carbonblack/intellij/rpmspec/parser/RpmSpecParser.java")
    pathToPsiRoot.set("/com/carbonblack/intellij/rpmspec/psi")
    purgeOldFiles.set(true)

    // These need to be set due to https://github.com/JetBrains/gradle-grammar-kit-plugin/issues/108
    sourceFile.convention(
        source.map {
            project.layout.projectDirectory.file(it)
        },
    )
    targetRootOutputDir.convention(
        targetRoot.map {
            project.layout.projectDirectory.dir(it)
        },
    )
    parserFile.convention(
        pathToParser.map {
            project.layout.projectDirectory.file("${targetRoot.get()}/$it")
        },
    )
    psiDir.convention(
        pathToPsiRoot.map {
            project.layout.projectDirectory.dir("${targetRoot.get()}/$it")
        },
    )
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

    // These need to be set due to https://github.com/JetBrains/gradle-grammar-kit-plugin/issues/108
    sourceFile.convention(
        source.map {
            project.layout.projectDirectory.file(it)
        },
    )
    targetRootOutputDir.convention(
        targetRoot.map {
            project.layout.projectDirectory.dir(it)
        },
    )
    parserFile.convention(
        pathToParser.map {
            project.layout.projectDirectory.file("${targetRoot.get()}/$it")
        },
    )
    psiDir.convention(
        pathToPsiRoot.map {
            project.layout.projectDirectory.dir("${targetRoot.get()}/$it")
        },
    )
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
