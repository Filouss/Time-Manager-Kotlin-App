package com.example.semestralka.data.remote

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.time.LocalDate

private const val BASE_URL = "https://date.nager.at/api/v3/"

val json = Json {
    ignoreUnknownKeys = true
}


private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .build()


interface ActivityWebApi {

    @RequiresApi(Build.VERSION_CODES.O)
    @GET("PublicHolidays/{year}/{countryCode}")
    //fetch holidays for current year
    suspend fun getHolidays(
        @Path("year") year: Int = LocalDate.now().year,
        @Path("countryCode") countryCode: String = "CZ"
    ): Response<List<Holiday>>

    companion object {
        @Volatile
        private var INSTANCE: ActivityWebApi? = null

        fun getHolidayApiService(): ActivityWebApi {
            return INSTANCE ?: synchronized(this) {
                val instance = retrofit.create(ActivityWebApi::class.java)
                INSTANCE = instance
                instance
            }
        }
    }
}