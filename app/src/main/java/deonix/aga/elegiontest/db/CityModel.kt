package deonix.aga.elegiontest.db

import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table
import com.activeandroid.query.Delete
import com.activeandroid.query.Select

/**
 * Created by deonix on 30.06.16.
 */
@Table(name = "Cities")
class CityModel : Model {
    @Column(name = "cityId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    var cityId: Long? = null

    @Column(name = "name")
    var name: String? = null

    @Column(name = "main", onDelete = Column.ForeignKeyAction.CASCADE)
    var main: MainModel? = null

    @Column(name = "wind", onDelete = Column.ForeignKeyAction.CASCADE)
    var wind: WindModel? = null

    var weather: List<WeatherModel>? = null

    constructor(id: Long, name: String, main: MainModel? = null, wind: WindModel? = null, weather: List<WeatherModel>? = null){
        this.cityId = id
        this.name = name
        this.main = main
        this.wind = wind
        this.weather = weather
    }

    fun getWeatherList(): List<WeatherModel>{
        if (weather == null){
            weather = getMany(WeatherModel::class.java, "city")
        }
        return weather!!
    }

    override fun equals(other: Any?): Boolean{
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        if (!super.equals(other)) return false

        other as CityModel

        if (cityId != other.cityId) return false

        return true
    }

    override fun hashCode(): Int{
        var result = super.hashCode()
        result = 31 * result + (cityId?.hashCode() ?: 0)
        return result
    }

    constructor()

    companion object {
        fun queryCityList(): List<CityModel> {
            val modelList:List<CityModel> = Select().from(CityModel::class.java).execute()
            return modelList
        }

        fun queryById(id: Long): CityModel {
            val model: CityModel = Select().from(CityModel::class.java).where("cityId = ?", id).executeSingle()
            return model;
        }

        fun deleteById(id: Long): CityModel {
            val model: CityModel = Delete().from(CityModel::class.java).where("cityId = ?", id).executeSingle()
            return model
        }
    }



}