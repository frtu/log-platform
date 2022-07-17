package com.github.frtu.sample.basic.persistence.basic

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountSubscriptionRepository : CoroutineCrudRepository<AccountSubscriptionEntity, Long> {
}
