package com.patrykandpatryk.liftapp.bodyrecord.di

import androidx.lifecycle.SavedStateHandle
import com.patrykandpatryk.liftapp.core.navigation.Routes.ARG_ID
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface BodyRecordModule {

    companion object {

        @BodyId
        @Provides
        fun provideBodyId(savedStateHandle: SavedStateHandle): Long =
            requireNotNull(savedStateHandle[ARG_ID])
    }
}
