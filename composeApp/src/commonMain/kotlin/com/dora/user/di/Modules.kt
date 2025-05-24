package com.dora.user.di

import com.dora.user.data.repo.AuthRepoImpl
import com.dora.user.domain.repo.AuthRepo
import com.dora.user.presentation.MainViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.ksp.generated.defaultModule

val viewModelModule = module {
    viewModelOf(::MainViewModel)
}

val dataModule = module {
    singleOf(::AuthRepoImpl) bind AuthRepo::class
}


val appModules = listOf(
    defaultModule,
    viewModelModule,
    dataModule,
    networkModule,
)

