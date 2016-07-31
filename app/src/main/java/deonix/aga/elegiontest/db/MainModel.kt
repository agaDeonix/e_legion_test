package deonix.aga.elegiontest.db

import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table

/**
 * Created by deonix on 10.07.16.
 */
@Table(name = "Main")
class MainModel : Model {

    constructor (temp: Float?, pressure: Float?, humidity: Float?, temp_min: Float?, temp_max: Float?) {
        this.temp = temp
        this.pressure = pressure
        this.humidity = humidity
        this.temp_min = temp_min
        this.temp_max = temp_max
    }

    constructor()

    @Column(name = "temp")
    var temp: Float? = null
    @Column(name = "pressure")
    var pressure: Float? = null
    @Column(name = "humidity")
    var humidity: Float? = null
    @Column(name = "temp_min")
    var temp_min: Float? = null
    @Column(name = "temp_max")
    var temp_max: Float? = null
}