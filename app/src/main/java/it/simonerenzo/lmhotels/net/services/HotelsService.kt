package it.simonerenzo.lmhotels.net.services

import io.reactivex.Single
import it.simonerenzo.lmhotels.net.models.HotelsResponse
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface HotelsService {

    @GET("hotels")
    fun getHotels() : Single<HotelsResponse>

    /**
     * Companion object to create the HotelsService
     */
    companion object Factory {
        fun create(): HotelsService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://services.lastminute.com/mobile/stubs/")
                .build()

            return retrofit.create(HotelsService::class.java)
        }
    }

}