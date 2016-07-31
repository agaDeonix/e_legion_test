package deonix.aga.elegiontest.ui.fragments

import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.squareup.picasso.Picasso
import deonix.aga.elegiontest.R
import deonix.aga.elegiontest.db.CityModel
import deonix.aga.elegiontest.db.MainModel
import deonix.aga.elegiontest.db.WeatherModel
import deonix.aga.elegiontest.db.WindModel
import deonix.aga.elegiontest.network.WeatherService
import deonix.aga.elegiontest.other.MyApplication
import deonix.aga.elegiontest.ui.MainActivity
import kotlinx.android.synthetic.main.fragment_city_detail.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

/**
 * Created by deonix on 16.07.16.
 */
class CityDetailFragment : BaseFragment() {

    var city: CityModel? = null
    var index: Int = 0

    override fun getViewId(): Int {
        return R.layout.fragment_city_detail
    }

    override fun getTitle(): String? {
        return city?.name
    }

    override fun initArguments(arguments: Bundle) {
        val cityId = arguments.getLong("city")
        city = CityModel.queryById(cityId)
        index = arguments.getInt("index")
    }

    override fun initActionBar() {
        if (!MyApplication.instance.isTablet()) {
            super.initActionBar()
            actionBar?.setDisplayHomeAsUpEnabled(true)
            actionBar?.setDisplayShowHomeEnabled(true)
            (activity as MainActivity).toolbar?.setNavigationOnClickListener {
                activity.onBackPressed()
            }
        }
    }

    override fun initView(view: View) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (view.findViewById(R.id.cardView) != null) {
                view.findViewById(R.id.cardView).transitionName = "card" + index
            }
            view.findViewById(R.id.tempView).transitionName = "temp" + index
        }
        if (view.findViewById(R.id.addCity) != null) {
            val addCityButton = view.findViewById(R.id.addCity) as FloatingActionButton
            addCityButton.setOnClickListener {
                addNewCity()
            }
        }
    }

    private fun addNewCity() {
        (activity as MainActivity).showAddCity()
    }

    override fun onStart() {
        super.onStart()
        if (MyApplication.requestManager.hasRequest("updateCity", city?.cityId.toString())){
            wait.visibility = View.VISIBLE
            MyApplication.requestManager.addListener("updateCity", city?.cityId.toString(), object: MyApplication.RequestsManager.RequestListener {
                override fun handle(requestName: String, params: String){
                    city = CityModel.queryById(city?.cityId!!)
                    updateView()
                    wait.visibility = View.GONE
                }
            })
        }
        updateView()
    }

    private fun updateView() {
        if (activity == null) {
            return
        }
        if (city?.getWeatherList() != null && city?.getWeatherList()!!.size > 0) {
            mainDescView.text = city?.getWeatherList()!!.get(0).description
            Picasso.with(context).load("http://openweathermap.org/img/w/${city?.getWeatherList()!!.get(0).icon}.png").into(iconView);
        }

        if (city?.main != null) {
            tempView.text = getTempString(city?.main?.temp!!)
            tempMinView.text = getTempString(city?.main?.temp_min!!)
            tempMaxView.text = getTempString(city?.main?.temp_max!!)
            if (city?.main?.pressure != null) {
                pressureView.text = String.format("%.2f %s", city?.main?.pressure!! / 133.3 * 100, MyApplication.instance.getString(R.string.pressure_postfix))
            } else {
                pressureView.setText(R.string.unknow)
            }
            if(city?.main?.humidity != null) {
                humidityView.text = city?.main?.humidity!!.toString() + " %"
            } else {
                humidityView.setText(R.string.unknow)
            }
        }

        if (city?.wind != null) {
            if (city?.wind?.speed != null) {
                windSpeedView.text = String.format("%s %s", city?.wind?.speed.toString(), MyApplication.instance.getString(R.string.wind_speed_postfix))
            } else {
                windSpeedView.setText(R.string.unknow)
            }
            if (city?.wind?.deg != null) {
                windDegView.setText(getWindDirection(city?.wind?.deg!!))
            } else {
                windDegView.setText(R.string.unknow)
            }
        }
    }

    private fun getWindDirection(deg: Float): Int {
        if (deg > 348.75 || deg <= 11.25 ) return R.string.wind_n
        if (deg > 11.25 || deg <= 33.75 ) return R.string.wind_nne
        if (deg > 33.75  || deg <= 56.25 ) return R.string.wind_ne
        if (deg > 56.25 || deg <= 68.75 ) return R.string.wind_ene
        if (deg > 68.75 || deg <= 101.25 ) return R.string.wind_e
        if (deg > 101.25 || deg <= 123.75 ) return R.string.wind_ese
        if (deg > 123.75 || deg <= 146.25 ) return R.string.wind_se
        if (deg > 146.25 || deg <= 168.75) return R.string.wind_sse
        if (deg > 168.75 || deg <= 191.25 ) return R.string.wind_s
        if (deg > 191.25 || deg <= 213.75 ) return R.string.wind_ssw
        if (deg > 213.75 || deg <= 236.25 ) return R.string.wind_sw
        if (deg > 236.25 || deg <= 258.75 ) return R.string.wind_wsw
        if (deg > 258.75 || deg <= 281.25 ) return R.string.wind_w
        if (deg > 281.25 || deg <= 303.75 ) return R.string.wind_wnw
        if (deg > 303.75 || deg <= 326.25 ) return R.string.wind_nw
        if (deg > 326.25 || deg <= 348.75 ) return R.string.wind_nnw
        return R.string.unknow
    }

    fun getTempString(temp: Float): String {
        var tempText = ""
        if (temp > 0) {
            tempText = "+"
        }
        if (temp < 0) {
            tempText = "-"
        }
        tempText += "${temp.toString()}°C"
        return tempText
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (!MyApplication.instance.isTablet()) {
            inflater.inflate(R.menu.cities, menu)
            super.onCreateOptionsMenu(menu, inflater)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (!MyApplication.instance.isTablet()) {
            if (item?.itemId == R.id.refresh) {
                updateCity()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateCity() {
        val service = WeatherService()
        wait.visibility = View.VISIBLE
        MyApplication.requestManager.addRequest("updateCity", city?.cityId.toString())
        service.weatherApi.weatherById(city?.cityId!!)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    cityWeather ->
                    MyApplication.requestManager.removeRequest("updateCity", city?.cityId.toString())
                    if (cityWeather.cod == 200) {
                        if (cityWeather.wind != null) {
                            val wind = WindModel(cityWeather.wind!!.speed, cityWeather.wind!!.deg)
                            wind.save()
                            city?.wind = wind
                        }
                        if (cityWeather.main != null) {
                            val main = MainModel(cityWeather.main!!.temp,
                                    cityWeather.main!!.pressure,
                                    cityWeather.main!!.humidity,
                                    cityWeather.main!!.temp_min,
                                    cityWeather.main!!.temp_max)
                            main.save()
                            city?.main = main
                        }
                        city?.save()
                        if (cityWeather.weather != null) {
                            val weatherList = ArrayList<WeatherModel>()
                            for (weather in cityWeather.weather!!) {
                                val newWeather = WeatherModel(weather.main, weather.description, weather.icon, city)
                                newWeather.save()
                                weatherList.add(newWeather)
                            }
                            city?.weather = weatherList
                        }
                        updateView()
                        wait.visibility = View.GONE
                    }
                }, { error ->
                    MyApplication.requestManager.removeRequest("updateCity", city?.cityId.toString())
                    if (activity != null) {
                        updateView()
                        wait.visibility = View.GONE
                    }
                })
    }
}