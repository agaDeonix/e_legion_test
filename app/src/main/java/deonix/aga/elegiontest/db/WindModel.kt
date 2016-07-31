package deonix.aga.elegiontest.db

import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table

/**
 * Created by deonix on 10.07.16.
 */
@Table(name = "Wind")
class WindModel : Model {

    @Column(name = "speed")
    var speed: Float? = null
    @Column(name = "deg")
    var deg: Float? = null

    constructor(speed: Float?, deg: Float?){
        this.speed = speed
        this.deg = deg
    }

    constructor()
}