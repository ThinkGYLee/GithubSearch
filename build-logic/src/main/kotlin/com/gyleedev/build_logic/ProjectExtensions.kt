package com.gyleedev.build_logic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.konan.properties.hasProperty
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.Properties

val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

val ExtensionContainer.libs: VersionCatalog
    get() = getByType<VersionCatalogsExtension>().named("libs")

val Project.androidExtension: CommonExtension
    get() = extensions.getByType(CommonExtension::class.java)


fun Project.getApiKey(propertyKey: String): String = getProps(propertyKey)

@Suppress("UNCHECKED_CAST")
fun <T> Project.getProps(key: String): T {
    val localProps = gradleLocalProperties(rootDir)
    return when {
        localProps.hasProperty(key) -> {
            localProps[key] as T
        }

        project.hasProperty(key) -> {
            project.property(key) as T
        }

        else -> {
            System.getenv(key) as T
        }
    }
}

fun Project.gradleLocalProperties(projectRootDir: File): Properties {
    val properties = Properties()
    val localProperties = File(projectRootDir, "local.properties")

    if (localProperties.isFile) {
        InputStreamReader(FileInputStream(localProperties), Charsets.UTF_8).use { reader ->
            properties.load(reader)
        }
    }
    return properties
}
