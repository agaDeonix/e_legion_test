package deonix.aga.elegiontest.db

import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table

/**
 * Created by deonix on 10.07.16.
 */
@Table(name = "Weather")
class WeatherModel : Model {
    @Column(name = "main")
    var main: String? = null
    @Column(name = "description")
    var description: String? = null
    @Column(name = "icon")
    var icon: String? = null

    @Column(name = "city", onDelete = Column.ForeignKeyAction.CASCADE)
    var city: CityModel? = null

    constructor(main: String?, description: String?, icon: String?, city: CityModel?){
        this.main = main
        this.description = description
        this.icon = icon
        this.city = city
    }

    constructor()
}