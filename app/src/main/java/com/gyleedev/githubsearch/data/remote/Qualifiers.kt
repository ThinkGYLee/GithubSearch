package com.gyleedev.githubsearch.data.remote

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TypeAccess

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TypeApi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TypeRevoke
