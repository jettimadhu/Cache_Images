package com.display.photos.di

import com.display.photos.BuildConfig
import com.display.photos.data.api.ApiClient
import com.display.photos.data.error.mapper.ErrorMapper
import com.display.photos.data.repository.PhotosRepository
import com.display.photos.ui.MainViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private val networkModule = module {
    single {
        provideHttpClient(get())
    }

    single {
        provideRetrofit(get())
    }

    single {
        provideApiClient(get())
    }

    single {
        provideLoggingInterceptor()
    }
}

private val repositoryModule = module {
    single { ErrorMapper(androidContext()) }
    single { PhotosRepository(get(), get()) }
}

private val viewModelModule = module {
    viewModel { MainViewModel(get()) }
}

private fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .build()
}

private fun provideHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
    return OkHttpClient.Builder()
        .addNetworkInterceptor(loggingInterceptor)
        .build()
}

private fun provideLoggingInterceptor(): HttpLoggingInterceptor {
    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
    return loggingInterceptor
}

private fun provideApiClient(retrofit: Retrofit): ApiClient {
    return retrofit.create(ApiClient::class.java)
}

val allModules = listOf(networkModule, repositoryModule, viewModelModule)

