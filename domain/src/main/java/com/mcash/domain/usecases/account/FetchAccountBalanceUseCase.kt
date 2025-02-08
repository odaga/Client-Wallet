package com.mcash.domain.usecases.account

import com.mcash.domain.repository.ClientRepository
import javax.inject.Inject

class FetchAccountBalanceUseCase @Inject constructor(
    private val client: ClientRepository
) {

    suspend operator fun invoke() {
        client.getClientBalance()
    }
}