package com.proger.cashtracker.di

import com.github.mikephil.charting.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.proger.cashtracker.api.currencyConversion.ApiDataSource
import com.proger.cashtracker.api.currencyConversion.CurrencyApiService
import com.proger.cashtracker.api.currencyConversion.EndPoints
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.Collections
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CurrencyModule {
    //API Base Url
    @Provides
    fun providesBaseUrl() = EndPoints.BASE_URL

    //Gson for converting JSON String to Java Objects
    @Provides
    fun providesGson() : Gson = GsonBuilder().setLenient().create()

    //Retrofit for networking
    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson) : Retrofit = Retrofit.Builder()
        .baseUrl(EndPoints.BASE_URL)
        .client(
            OkHttpClient.Builder().also { client ->
            if (BuildConfig.DEBUG){
                client.connectTimeout(120, TimeUnit.SECONDS)
                client.readTimeout(120, TimeUnit.SECONDS)
                client.protocols(Collections.singletonList(Protocol.HTTP_1_1))
            }
        }.build()
        )
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    //Api Service with retrofit instance
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit) : CurrencyApiService = retrofit.create(CurrencyApiService::class.java)

    //Class helper with apiService Interface
    @Provides
    @Singleton
    fun provideApiDataSource(apiService: CurrencyApiService) = ApiDataSource(apiService)
}