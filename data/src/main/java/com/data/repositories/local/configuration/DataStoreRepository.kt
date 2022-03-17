package com.data.repositories.local.configuration

import androidx.datastore.core.DataStore
import com.domain.datasources.local.SettingsConfigurationSource
import com.domain.model.configuration.AppConfiguration
import com.domain.model.configuration.NftList
import com.domain.model.configuration.UserProfile
import com.domain.model.configuration.nft
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataStoreRepository @Inject constructor(
    private val userPreferenceDataStore: DataStore<UserProfile>,
    private val appConfigurationDataStore: DataStore<AppConfiguration>,
    private val nftConfigrationDataStore: DataStore<NftList>,
) : SettingsConfigurationSource {
    override fun getUserPreference(): Flow<UserProfile> {
        return userPreferenceDataStore.data
    }

    override suspend fun updateUserPreference(
        userProfile: UserProfile,
    ) {
        userPreferenceDataStore.updateData {
            it.toBuilder().mergeFrom(userProfile).build()
        }
    }

    override suspend fun clearUserPreference() {
        userPreferenceDataStore.updateData {
            it.toBuilder().clear().build()
        }
    }

    override fun getAppConfiguration(): Flow<AppConfiguration> {
        return appConfigurationDataStore.data
    }

    override suspend fun setAppConfiguration(appConfiguration: AppConfiguration) {
        appConfigurationDataStore.updateData {
            it.toBuilder().mergeFrom(appConfiguration).build()
        }
    }

    override suspend fun addNft(nft: nft) {
        nftConfigrationDataStore.updateData {
            it.toBuilder().addNfts(nft).build()
        }
    }

    override suspend fun removeNft(nft: nft) {
        /*nftConfigrationDataStore.updateData { //todo:
            it.toBuilder().removeNfts().build()
        }*/
    }

    override suspend fun getAllNft(): Flow<NftList> {
        return nftConfigrationDataStore.data
    }


}