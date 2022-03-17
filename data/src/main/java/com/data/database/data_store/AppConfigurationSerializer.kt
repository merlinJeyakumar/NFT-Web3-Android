package com.data.database.data_store

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.domain.model.configuration.AppConfiguration
import com.domain.model.configuration.NftList
import com.domain.model.configuration.nft
import com.google.protobuf.InvalidProtocolBufferException


import java.io.InputStream
import java.io.OutputStream

object AppConfigurationSerializer : Serializer<AppConfiguration> {
    override val defaultValue: AppConfiguration = AppConfiguration.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): AppConfiguration {
        try {
            return AppConfiguration.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: AppConfiguration, output: OutputStream) = t.writeTo(output)
}

object NftSerializer : Serializer<nft> {
    override val defaultValue: nft = nft.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): nft {
        try {
            return nft.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: nft, output: OutputStream) = t.writeTo(output)
}

object NftListSerializer : Serializer<NftList> {
    override val defaultValue:NftList = NftList.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): NftList {
        try {
            return NftList.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: NftList, output: OutputStream) = t.writeTo(output)
}