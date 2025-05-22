package com.dora.user.di

import com.dora.user.data.repo.AuthRepoImpl
import com.dora.user.domain.repo.AuthRepo
import com.dora.user.presentation.MainViewModel
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.ksp.generated.module

val viewModelModule = module {
    viewModelOf(::MainViewModel)
}

val dataModule = module {
    singleOf(::AuthRepoImpl) bind AuthRepo::class
}


val appModules = listOf(
    viewModelModule,
    dataModule,
    networkModule,
    AnnotatedModule().module
)

@Module
@ComponentScan
class AnnotatedModule

