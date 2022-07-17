package com.github.frtu.sample.basic.persistence.basic

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.*

@Table("account_subscription")
data class AccountSubscriptionEntity(
    @Column("customer_name")
    var customerName: String = "",

    @Column("email_address")
    var emailAddress: String = "",

    @Column("phone")
    var phone: String = "",

    @Column("status")
    var status: STATUS = STATUS.INIT,

    @Id
    @Column("id")
    var id: Long? = null,

    @CreatedDate
    @Column("creation_time")
    val creationTime: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @Column("update_time")
    var updateTime: LocalDateTime = creationTime
)

enum class STATUS {
    INIT, ACTIVE, DEACTIVATED, CLOSED
}