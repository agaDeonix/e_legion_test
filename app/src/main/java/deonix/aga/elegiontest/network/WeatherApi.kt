package deonix.aga.elegiontest.network

import deonix.aga.elegiontest.BuildConfig
import deonix.aga.elegiontest.network.model.City
import deonix.aga.elegiontest.network.model.FindResult
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

/**
 * Created by deonix on 07.07.16.
 */
interface WeatherApi {
    @GET("weather?appid=" + BuildConfig.WEATHER_APP_ID + "&units=metric&lang=ru")
    fun weatherById(
            @Query("id") id: Long): Observable<City>

    @GET("find?appid=" + BuildConfig.WEATHER_APP_ID + "&type=accurate&units=metric&lang=ru")
    fun find(
            @Query("q") name: String): Observable<FindResult>
}