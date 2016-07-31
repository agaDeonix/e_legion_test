package deonix.aga.elegiontest.network.model

/**
 * Created by deonix on 08.07.16.
 */
class City(var cod: Int, var id: Long, var name: String, var main: Main?, var wind: Wind?, var weather: List<Weather>?) {

    class Main(var temp: Float?, var pressure: Float?, var humidity: Float?, var temp_min: Float?, var temp_max: Float?) {

    }

    class Weather(var main: String?, var description: String?, var icon: String?) {

    }

    class Wind(var speed: Float?, var deg: Float?) {

    }

}