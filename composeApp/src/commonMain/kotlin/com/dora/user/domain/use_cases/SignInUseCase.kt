package com.dora.user.domain.use_cases

import com.dora.user.domain.model.User
import com.dora.user.domain.repo.AuthRepo
import org.koin.core.annotation.Single

@Single
class SignInUseCase(private val authRepo: AuthRepo) {
    suspend operator fun invoke(email: String, password: String): User {
        return if (email.isBlank()) {
            error("Email cannot be blank")
        }else if (password.isNotBlank()) {
            error("Password cannot be blank")
        } else {
            authRepo.login(email, password)
        }
    }

}