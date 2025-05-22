package com.dora.user.data.repo

import com.dora.user.domain.model.User
import com.dora.user.domain.repo.AuthRepo
import io.ktor.client.HttpClient

class AuthRepoImpl(ktorClient: HttpClient) : AuthRepo {
    override suspend fun login(
        email: String,
        password: String
    ): User {
        return User()
    }
}