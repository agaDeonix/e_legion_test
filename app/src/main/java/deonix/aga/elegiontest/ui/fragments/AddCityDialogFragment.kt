package deonix.aga.elegiontest.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ListView
import com.jakewharton.rxbinding.widget.RxTextView
import deonix.aga.elegiontest.R
import deonix.aga.elegiontest.adapters.CitiesAdapter
import deonix.aga.elegiontest.db.CityModel
import deonix.aga.elegiontest.db.MainModel
import deonix.aga.elegiontest.db.WeatherModel
import deonix.aga.elegiontest.db.WindModel
import deonix.aga.elegiontest.network.WeatherService
import deonix.aga.elegiontest.network.model.City
import deonix.aga.elegiontest.other.MyApplication
import deonix.aga.elegiontest.ui.MainActivity
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by deonix on 29.07.16.
 */
class AddCityDialogFragment: DialogFragment() {

    var cities: MutableList<CityModel> = ArrayList<CityModel>()
    var cityList: ListView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater?.inflate(R.layout.fragment_add_city, container)

        dialog.window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.setTitle(R.string.find_city)
        view?.findViewById(R.id.cancelButton)?.setOnClickListener {
            dialog.dismiss()
        }
        cityList = view?.findViewById(R.id.cityList) as ListView?
        cityList?.setOnItemClickListener { adapterView, view, i, l ->
            selectItem(i)
        }
        RxTextView.textChangeEvents(view?.findViewById(R.id.nameText) as EditText)
                .filter { e ->
                    e.text().length >= 3 }
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribe {
                    WeatherService().weatherApi.find(it.text().toString())
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                result ->
                                updateList(result.list)
                            },
                                    { error ->
                                        updateList(null)
                                    })
                }
        return view!!
    }

    private fun updateList(list: List<City>?) {
        cities.clear()
        if (list != null) {
            for (cityWeather in list) {
                var city = CityModel()
                city.cityId = cityWeather.id
                city.name = cityWeather.name
                if (cityWeather.wind != null) {
                    val wind = WindModel(cityWeather.wind!!.speed, cityWeather.wind!!.deg)
                    city.wind = wind
                }
                if (cityWeather.main != null) {
                    val main = MainModel(cityWeather.main!!.temp,
                            cityWeather.main!!.pressure,
                            cityWeather.main!!.humidity,
                            cityWeather.main!!.temp_min,
                            cityWeather.main!!.temp_max)
                    city.main = main
                }
                if (cityWeather.weather != null) {
                    val weatherList = ArrayList<WeatherModel>()
                    for (weather in cityWeather.weather!!) {
                        val newWeather = WeatherModel(weather.main, weather.description, weather.icon, city)
                        weatherList.add(newWeather)
                    }
                    city.weather = weatherList
                }
                cities.add(city)
            }
        }
        if (context != null) {
            cityList?.adapter = getAdapter(cities!!)
        }
    }

    private fun getAdapter(cities: List<CityModel>): CitiesAdapter? {
        if (!MyApplication.instance.isTablet()) {
            return object : CitiesAdapter(context, cities!!) {
                override fun onClick(p0: View?) {
                    Handler().postDelayed( {
                        selectItem(p0?.tag as Int)
                    }, 300)
                }

                override fun onLongClick(p0: View?): Boolean {

                    return true
                }

                override fun isEnabled(position: Int): Boolean {
                    return false
                }

            }
        } else {
            return object : CitiesAdapter(context, cities!!) {
                override fun onClick(p0: View?) {

                }

                override fun onLongClick(p0: View?): Boolean {
                    return true
                }

            }
        }
    }

    private fun selectItem(index: Int) {
        var model = cityList?.adapter?.getItem(index) as CityModel
        model.save()
        (activity as MainActivity).showCities()
        dialog.dismiss()
    }


}