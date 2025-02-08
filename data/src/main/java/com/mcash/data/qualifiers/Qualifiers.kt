package com.mcash.data.qualifiers

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ClientApiQualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class VersionApiQualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class KycServiceQualifier