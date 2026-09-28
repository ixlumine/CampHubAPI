package com.camphub.api.model

import jakarta.persistence.*
import org.hibernate.Hibernate

@MappedSuperclass
abstract class BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as BaseEntity
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: System.identityHashCode(this)
}
