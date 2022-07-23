package com.github.frtu.sample.basic.domain

import com.github.frtu.sample.basic.persistence.basic.AccountSubscriptionEntity
import com.github.frtu.sample.basic.persistence.basic.AccountSubscriptionRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class SubscriptionProcessor(
    private val repository: AccountSubscriptionRepository,
) {
    suspend fun queryOne(id: Long) = repository.findById(id)

    suspend fun insert(accountSubscriptionEntity: AccountSubscriptionEntity) =
        repository.save(accountSubscriptionEntity)

    fun queryMany(): Flow<AccountSubscriptionEntity> = repository.findAll()
}