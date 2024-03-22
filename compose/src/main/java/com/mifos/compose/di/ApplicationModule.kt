package com.mifos.compose.di

import android.content.Context
import com.mifos.compose.PasscodeRepository
import com.mifos.compose.PasscodeRepositoryImpl
import com.mifos.compose.utility.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @author pratyush
 * @since 15/3/24
 */

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {
    @Provides
    @Singleton
    fun providePrefManager(@ApplicationContext context: Context): PreferenceManager {
        return PreferenceManager(context)
    }

    @Provides
    @Singleton
    fun providesPasscodeRepository(preferenceManager: PreferenceManager): PasscodeRepository {
        return PasscodeRepositoryImpl(preferenceManager)
    }
}