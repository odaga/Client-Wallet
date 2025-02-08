package com.mcash.data.remote.remoteMappers

interface BaseRemoteMapper<FROM, TO> {

  fun mapToDomain(entity: FROM): TO
}