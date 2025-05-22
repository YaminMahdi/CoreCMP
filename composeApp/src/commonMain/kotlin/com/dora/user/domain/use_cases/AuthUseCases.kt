package com.dora.user.domain.use_cases

import org.koin.core.annotation.Single

@Single
data class AuthUseCases(
    val signIn: SignInUseCase
)