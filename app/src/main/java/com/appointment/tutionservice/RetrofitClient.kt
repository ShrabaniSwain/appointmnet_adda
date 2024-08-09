package com.appointment.tutionservice

import com.appointment.tutionservice.Constant.BASE_URL
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    val gson = GsonBuilder().setLenient().create()

    private const val REQUEST_TIMEOUT_TIME_IN_SECONDS = 60L
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val api: TutionServiceApi by lazy {
        retrofit.create(TutionServiceApi::class.java)
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .readTimeout(REQUEST_TIMEOUT_TIME_IN_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(REQUEST_TIMEOUT_TIME_IN_SECONDS, TimeUnit.SECONDS)
            .connectTimeout(REQUEST_TIMEOUT_TIME_IN_SECONDS, TimeUnit.SECONDS)
            .build()
    }

}