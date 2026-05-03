package com.gyleedev.build_logic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.kotlin.dsl.getByType

val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

val ExtensionContainer.libs: VersionCatalog
    get() = getByType<VersionCatalogsExtension>().named("libs")

val Project.androidExtension: CommonExtension
    get() = extensions.getByType(CommonExtension::class.java)
