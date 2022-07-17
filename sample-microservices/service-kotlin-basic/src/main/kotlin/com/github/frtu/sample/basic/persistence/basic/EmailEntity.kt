package com.github.frtu.sample.basic.persistence.basic

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.*

@Table("email")
data class EmailEntity(
    @Column("receiver")
    var receiver: String? = null,

    @Column("subject")
    var subject: String = "",

    @Column("content")
    var content: String = "",

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
) {
    companion object {
        const val TABLE_NAME = "email"
    }
}

enum class STATUS {
    INIT, SENT, ERROR
}