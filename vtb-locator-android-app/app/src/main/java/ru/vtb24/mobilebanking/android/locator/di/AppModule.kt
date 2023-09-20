package ru.vtb24.mobilebanking.android.locator.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.vtb24.mobilebanking.android.locator.data.HomeRepository
import ru.vtb24.mobilebanking.android.locator.data.LoginRepository
import ru.vtb24.mobilebanking.android.locator.data.UserDataRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHomeRepository(): HomeRepository =
        HomeRepository()

    @Provides
    @Singleton
    fun provideLoginRepository(@ApplicationContext context: Context): LoginRepository =
        LoginRepository(context)

    @Provides
    @Singleton
    fun provideUserDataRepository(): UserDataRepository =
        UserDataRepository()

}
