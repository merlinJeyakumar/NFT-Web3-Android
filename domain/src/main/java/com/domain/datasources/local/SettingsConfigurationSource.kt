package com.domain.datasources.local

import com.domain.model.configuration.*
import kotlinx.coroutines.flow.Flow


interface SettingsConfigurationSource {
    fun getUserPreference(): Flow<UserProfile>
    suspend fun updateUserPreference(
        userProfile: UserProfile,
    )
    suspend fun clearUserPreference()

    fun getAppConfiguration(): Flow<AppConfiguration>
    suspend fun setAppConfiguration(appConfiguration: AppConfiguration)

    suspend fun addNft(nft: nft)
    suspend fun removeNft(nft: nft)
    suspend fun getAllNft():Flow<NftList>
}
