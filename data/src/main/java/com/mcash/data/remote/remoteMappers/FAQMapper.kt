package com.mcash.data.remote.remoteMappers

import com.mcash.data.remote.model.FAQ
import com.mcash.domain.model.FAQEntity
import javax.inject.Inject

class FAQMapper @Inject constructor(): BaseRemoteMapper<FAQ, FAQEntity> {
    override fun mapToDomain(entity: FAQ): FAQEntity {
        return FAQEntity(
            id = entity.id,
            title = entity.title,
            content = entity.content
        )
    }

}