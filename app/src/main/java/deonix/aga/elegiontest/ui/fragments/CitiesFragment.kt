package deonix.aga.elegiontest.ui.fragments

import android.app.Activity
import android.content.DialogInterface
import android.os.Build
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ListView
import deonix.aga.elegiontest.R
import deonix.aga.elegiontest.adapters.CitiesAdapter
import deonix.aga.elegiontest.db.CityModel
import deonix.aga.elegiontest.db.CityModel.Companion.queryCityList
import deonix.aga.elegiontest.db.MainModel
import deonix.aga.elegiontest.db.WeatherModel
import deonix.aga.elegiontest.db.WindModel
import deonix.aga.elegiontest.network.WeatherService
import deonix.aga.elegiontest.other.MyApplication
import deonix.aga.elegiontest.ui.MainActivity
import kotlinx.android.synthetic.main.fragment_cities.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

/**
 * Created by deonix on 29.06.16.
 */
class CitiesFragment : BaseFragment() {

    var selectedItem: CityModel? = null

    var cities: List<CityModel>? = null
    var cityList: ListView? = null

    override fun getViewId(): Int {
        return R.layout.fragment_cities
    }

    override fun getTitle(): String? {
        return getString(R.string.title_cities)
    }

    override fun initView(view: View) {
        super.initView(view)
        hideKeyboard(activity)
        cityList = view.findViewById(R.id.cityList) as ListView?
        cityList?.setOnItemLongClickListener { adapterView, view, index, l ->
            deleteItem(index)
            true
        }
        cityList?.setOnItemClickListener { adapterView, view, index, l ->
            selectItem(index)
        }
        updateWeather()

        for (city in cities!!) {
            if (city.main == null) {
                updateCity(city)
            }
        }

        if (view.findViewById(R.id.addCity) != null) {
            val addCityButton = view.findViewById(R.id.addCity) as FloatingActionButton
            addCityButton.setOnClickListener {
                addNewCity()
            }
        }
    }

    private fun deleteItem(index: Int) {
        val dialogClickListener = DialogInterface.OnClickListener { dialogInterface, i ->
            when (i) {
                DialogInterface.BUTTON_POSITIVE -> {
                    var city = (cityList?.adapter?.getItem(index) as CityModel)
                    var main: MainModel? = null
                    if (city.main != null) {
                        main = MainModel(city.main?.temp, city.main?.pressure, city.main?.humidity, city.main?.temp_min, city.main?.temp_max)
                    }
                    var wind: WindModel? = null
                    if (city.wind != null) {
                        wind = WindModel(city.wind?.speed, city.wind?.deg)
                    }
                    var weathers: MutableList<WeatherModel>? = null
                    if (city.getWeatherList() != null) {
                        weathers = ArrayList<WeatherModel>()
                        for (weather in city.getWeatherList()) {
                            weathers.add(WeatherModel(weather.main, weather.description, weather.icon, null))
                        }
                    }
                    var restoreCity = CityModel(city.cityId!!, city.name!!)
                    city.delete()
                    val snackbar = Snackbar.make(main_content, R.string.city_is_deleted, Snackbar.LENGTH_LONG).setAction(R.string.undo, object : View.OnClickListener {
                        override fun onClick(view: View) {
                            if (main != null) {
                                main!!.save()
                                restoreCity.main = main
                            }
                            if (wind != null) {
                                wind!!.save()
                                restoreCity.wind = wind
                            }
                            restoreCity.save()
                            if (weathers != null) {
                                for (weather in weathers!!) {
                                    weather.city = restoreCity
                                    weather.save()
                                }
                            }
                            updateWeather()
                            val snackbar1 = Snackbar.make(view, R.string.city_is_restored, Snackbar.LENGTH_SHORT)
                            snackbar1.show()
                        }
                    })

                    snackbar.show()
                    updateWeather()
                }

                DialogInterface.BUTTON_NEGATIVE -> {
                    dialogInterface.dismiss()
                }
            }
        }
        val builder = AlertDialog.Builder(context)
        builder.setMessage(MyApplication.instance.getString(R.string.delete_question))
                .setPositiveButton(MyApplication.instance.getString(R.string.yes), dialogClickListener)
                .setNegativeButton(MyApplication.instance.getString(R.string.no), dialogClickListener).show()
    }

    private fun selectItem(index: Int, sharedElements: List<Pair<View, String>>? = null) {
        selectedItem = cityList?.adapter?.getItem(index) as CityModel
        (activity as MainActivity).showCity(selectedItem!!, index, sharedElements)
        if (MyApplication.instance.isTablet()) {
            (cityList?.adapter as CitiesAdapter).selectedCity = selectedItem
            (cityList?.adapter as CitiesAdapter).notifyDataSetChanged()
        }
    }

    private fun addNewCity() {
        (activity as MainActivity).showAddCity()
    }

    private fun updateWeather() {
        cities = queryCityList()
        cityList?.adapter = getAdapter(cities!!)
        for (city in cities!!) {
            if (MyApplication.requestManager.hasRequest("updateCity", city?.cityId.toString())) {
                MyApplication.requestManager.addListener("updateCity", city?.cityId.toString(), object : MyApplication.RequestsManager.RequestListener {
                    override fun handle(requestName: String, params: String) {
                        cities = queryCityList()
                        var adapter = getAdapter(cities!!)
                        if (MyApplication.instance.isTablet()) {
                            adapter?.selectedCity = selectedItem
                        }
                        cityList?.adapter = adapter
                    }
                })
            }
        }
        if (MyApplication.instance.isTablet()) {
            if (cities != null && cities!!.size > 0) {
                if (!cities!!.contains(selectedItem)) {
                    selectedItem = cities!![0]
                }
                (activity as MainActivity).showCity(selectedItem!!)
                (cityList?.adapter as CitiesAdapter).selectedCity = selectedItem
                (cityList?.adapter as CitiesAdapter).notifyDataSetChanged()
            }
        }
    }

    private fun getAdapter(cities: List<CityModel>): CitiesAdapter? {
        if (!MyApplication.instance.isTablet()) {
            return object : CitiesAdapter(context, cities!!) {
                override fun onClick(p0: View?) {
                    Handler().postDelayed( {
                        val index = p0?.tag as Int
                        var sharedElements:ArrayList<Pair<View, String>>?  = null
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            sharedElements = ArrayList<Pair<View, String>>()
                            var temp = p0?.getTag(R.id.tempView) as View
                            sharedElements.add(Pair(p0!!, p0.transitionName))
                            sharedElements.add(Pair(temp, temp.transitionName))
                        }
                        selectItem(p0?.tag as Int, sharedElements)
                    }, 300)
                }

                override fun onLongClick(p0: View?): Boolean {
                    Handler().postDelayed( {
                        deleteItem(p0?.tag as Int)
                    }, 300)

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.cities, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.refresh) {
            updateAllCities()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateCity(city: CityModel) {
        val service = WeatherService()
        MyApplication.requestManager.addRequest("updateCity", city.cityId.toString())
        updateWeather()
        service.weatherApi.weatherById(city.cityId!!)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    cityWeather ->
                    MyApplication.requestManager.removeRequest("updateCity", city.cityId.toString())
                    if (cityWeather.cod == 200) {
                        if (cityWeather.wind != null) {
                            val wind = WindModel(cityWeather.wind!!.speed, cityWeather.wind!!.deg)
                            wind.save()
                            city.wind = wind
                        }
                        if (cityWeather.main != null) {
                            val main = MainModel(cityWeather.main!!.temp,
                                    cityWeather.main!!.pressure,
                                    cityWeather.main!!.humidity,
                                    cityWeather.main!!.temp_min,
                                    cityWeather.main!!.temp_max)
                            main.save()
                            city.main = main
                        }
                        city.save()
                        if (cityWeather.weather != null) {
                            val weatherList = ArrayList<WeatherModel>()
                            for (weather in cityWeather.weather!!) {
                                val newWeather = WeatherModel(weather.main, weather.description, weather.icon, city)
                                newWeather.save()
                                weatherList.add(newWeather)
                            }
                            city.weather = weatherList
                        }
                        updateWeather()
                    }
                }, { error ->
                    MyApplication.requestManager.removeRequest("updateCity", city.cityId.toString())
                    updateWeather()
                })
    }

    private fun updateAllCities() {
        for (city in this@CitiesFragment.cities!!) {
            updateCity(city)
        }
    }

    fun hideKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

}
