package com.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import com.data.database.data_store.DataStoreManager
import com.data.repositories.local.configuration.DataStoreRepository
import com.data.repositories.local.configuration.PreferencesRepository
import com.domain.datasources.local.IPreferencesDataSource
import com.domain.datasources.local.SettingsConfigurationSource
import com.domain.model.configuration.AppConfiguration
import com.domain.model.configuration.NftList
import com.domain.model.configuration.UserProfile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ConfigurationModule {

    @Provides
    fun provideSettingsConfigurationRepository(
        userPreferenceDataStore: DataStore<UserProfile>,
        appConfiguration: DataStore<AppConfiguration>,
        nftListDataStore: DataStore<NftList>
    ): SettingsConfigurationSource {
        return DataStoreRepository(
            userPreferenceDataStore,
            appConfiguration,
            nftListDataStore
        )
    }

    @Provides
    fun provideUserPreferences(dataStoreManager: DataStoreManager): DataStore<UserProfile> {
        return dataStoreManager.getUserPreference()
    }

    @Provides
    fun provideConfiguration(dataStoreManager: DataStoreManager): DataStore<AppConfiguration> {
        return dataStoreManager.getDeviceConfiguration()
    }

    @Provides
    fun provideNftList(dataStoreManager: DataStoreManager): DataStore<NftList> {
        return dataStoreManager.getNftListSerializer()
    }

    @Provides
    @Singleton
    fun providePreferencesRepository(@ApplicationContext context: Context): IPreferencesDataSource {
        return PreferencesRepository(context)
    }

    @Provides
    @Singleton
    fun provideDataStoreManager(@ApplicationContext context: Context): DataStoreManager {
        return DataStoreManager(context)
    }
}