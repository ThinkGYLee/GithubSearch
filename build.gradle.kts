import com.diffplug.gradle.spotless.SpotlessExtension

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.safeargs) apply false
    alias(libs.plugins.library) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    jacoco
}

val fileFilter = listOf(
    "**/R.class",
    "**/R$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*",
    "**/*Test*.*",
    "android/**/*.*",
    "**/*\$Lambda$*.*",
    "**/*\$ExternalSynthetic$*.*",
    "**/*\$TypeAdapter$*.*",
    "**/*Hilt*.*",
    "**/Dagger*.*",
    "**/*_Factory.*",
    "**/*_MembersInjector.*",
    "**/*_Impl*.*",
    "**/*Binding.*"
)

tasks.register<JacocoReport>("jacocoFullReport") {
    group = "Reporting"
    description = "전체 모듈의 테스트 커버리지 리포트를 통합하여 생성합니다."

    dependsOn(subprojects.mapNotNull { it.tasks.findByName("testDebugUnitTest") })
    dependsOn(subprojects.mapNotNull { it.tasks.findByName("test") })

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    val classDirectoriesList = mutableListOf<FileTree>()
    val sourceDirectoriesList = mutableListOf<FileCollection>()
    val executionDataList = mutableListOf<FileCollection>()

    subprojects {
        val project = this
        val buildDir = project.layout.buildDirectory

        classDirectoriesList.add(
            project.fileTree(buildDir.dir("intermediates/javac/debug/compileDebugJavaWithJavac/classes")) {
                exclude(fileFilter)
            }
        )
        classDirectoriesList.add(
            project.fileTree(buildDir.dir("intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes")) {
                exclude(fileFilter)
            }
        )
        classDirectoriesList.add(
            project.fileTree(buildDir.dir("classes/kotlin/main")) {
                exclude(fileFilter)
            }
        )

        sourceDirectoriesList.add(project.files("${project.projectDir}/src/main/java"))
        sourceDirectoriesList.add(project.files("${project.projectDir}/src/main/kotlin"))

        executionDataList.add(
            project.fileTree(buildDir) {
                include("jacoco/testDebugUnitTest.exec")
                include("jacoco/test.exec")
                include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
            }
        )
    }

    classDirectories.setFrom(files(classDirectoriesList))
    sourceDirectories.setFrom(files(sourceDirectoriesList))
    executionData.setFrom(files(executionDataList))
}

subprojects {
    plugins.apply("com.diffplug.spotless")

    configure<SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            ktlint("1.8.0")
                .setEditorConfigPath("$rootDir/.editorconfig")
            leadingTabsToSpaces(4)
            endWithNewline()
        }
        kotlinGradle {
            target("**/*.gradle.kts")
            ktlint("1.8.0")
                .setEditorConfigPath("$rootDir/.editorconfig")
            leadingTabsToSpaces(4)
            endWithNewline()
        }
    }
}
