package it.simonerenzo.lmhotels.net.services

import io.reactivex.Flowable
import it.simonerenzo.lmhotels.net.models.Hotel
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface HotelsService {

    @GET("/mobile/stubs/hotels")
    fun getHotels() : Flowable<Hotel>

    companion object Factory {
        fun create(): HotelsService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://services.lastminute.com")
                .build()

            return retrofit.create(HotelsService::class.java)
        }
    }

}