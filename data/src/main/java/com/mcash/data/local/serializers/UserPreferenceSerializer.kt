package com.mcash.data.local.serializers

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.mcash.data.UserProto
import java.io.InputStream
import java.io.OutputStream

object UserPreferenceSerializer : Serializer<UserProto> {
    override val defaultValue: UserProto = UserProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserProto = try {
        UserProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    override suspend fun writeTo(t: UserProto, output: OutputStream) = t.writeTo(output)
}