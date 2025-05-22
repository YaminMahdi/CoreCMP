package com.dora.user.domain.repo

import com.dora.user.domain.model.User

interface AuthRepo {
    suspend fun login(email: String, password: String): User
}