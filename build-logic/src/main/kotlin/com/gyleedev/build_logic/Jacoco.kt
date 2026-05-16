package com.gyleedev.build_logic

import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

private val fileFilter = listOf(
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

internal fun Project.configureJacoco(
    androidComponentsExtension: AndroidComponentsExtension<*, *, *>,
) {
    configureJacocoCommon()

    if (tasks.findByName("jacocoTestReport") == null) {
        tasks.register<JacocoReport>("jacocoTestReport") {
            group = "Reporting"
            description = "Generate Jacoco coverage reports for the debug build."

            reports {
                xml.required.set(true)
                html.required.set(true)
            }

            val javaClasses = fileTree(layout.buildDirectory.dir("intermediates/javac/debug/compileDebugJavaWithJavac/classes")) {
                exclude(fileFilter)
            }
            val kotlinClasses = fileTree(layout.buildDirectory.dir("intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes")) {
                exclude(fileFilter)
            }

            classDirectories.setFrom(files(javaClasses, kotlinClasses))

            sourceDirectories.setFrom(
                files(
                    "$projectDir/src/main/java",
                    "$projectDir/src/main/kotlin"
                )
            )

            executionData.setFrom(
                fileTree(layout.buildDirectory) {
                    include("jacoco/testDebugUnitTest.exec")
                    include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
                }
            )
        }
    }
}

internal fun Project.configureJacocoJvm() {
    configureJacocoCommon()

    if (tasks.findByName("jacocoTestReport") == null) {
        tasks.register<JacocoReport>("jacocoTestReport") {
            group = "Reporting"
            description = "Generate Jacoco coverage reports for the JVM build."

            reports {
                xml.required.set(true)
                html.required.set(true)
            }

            val jvmClasses = fileTree(layout.buildDirectory.dir("classes/kotlin/main")) {
                exclude(fileFilter)
            }

            classDirectories.setFrom(files(jvmClasses))

            sourceDirectories.setFrom(
                files(
                    "$projectDir/src/main/java",
                    "$projectDir/src/main/kotlin"
                )
            )

            executionData.setFrom(
                fileTree(layout.buildDirectory) {
                    include("jacoco/test.exec")
                }
            )
        }
    }
}

private fun Project.configureJacocoCommon() {
    configure<JacocoPluginExtension> {
        toolVersion = libs.findVersion("jacoco").get().toString()
    }

    tasks.withType<Test>().configureEach {
        configure<org.gradle.testing.jacoco.plugins.JacocoTaskExtension> {
            isIncludeNoLocationClasses = true
            excludes = listOf("jdk.internal.*")
        }
    }
}
