package deonix.aga.elegiontest.network

import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by deonix on 07.07.16.
 */
class WeatherService {
    val weatherApi: WeatherApi

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/data/2.5/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        weatherApi = retrofit.create(WeatherApi::class.java)
    }
}