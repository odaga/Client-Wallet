package com.mcash.data.remote.remoteMappers

import com.mcash.data.remote.model.RemoteUser
import com.mcash.domain.model.UserEntity
import javax.inject.Inject

class RemoteUserMapper @Inject constructor(): BaseRemoteMapper<RemoteUser, UserEntity> {
    override fun mapToDomain(entity: RemoteUser): UserEntity {
        return UserEntity(
            id = entity.id,
            username = entity.username.orEmpty(),
            name = entity.name.orEmpty(),
            email = entity.email.orEmpty(),
            pin = "",
            phone = entity.phone.orEmpty(),
            token = entity.token,
            ip = entity.ip,
            sip = entity.sip
        )
    }
}