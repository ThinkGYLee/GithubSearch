package com.gyleedev.build_logic

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

/**
 * Version Catalog(libs.versions.toml)에 정의된 의존성들을 
 * 빌드 스크립트에서 타입 안전하게 참조하기 위한 확장 속성입니다.
 */
val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")
