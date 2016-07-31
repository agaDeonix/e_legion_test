package deonix.aga.elegiontest.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.transition.TransitionInflater
import android.view.View
import deonix.aga.elegiontest.R
import deonix.aga.elegiontest.db.CityModel
import deonix.aga.elegiontest.other.MyApplication
import deonix.aga.elegiontest.ui.fragments.*

class MainActivity : AppCompatActivity() {

    var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initActionBar()
        if (MyApplication.instance.isFirstStart()) {
            MyApplication.instance.setIsFirstStart()
            initDB()
        }
        var frag = supportFragmentManager.findFragmentByTag("fragment")
        if (frag != null) {
//            showFragment(frag as BaseFragment)
        } else {
            showCities()
        }
    }

    private fun initDB() {
        var city = CityModel(551487, "Kazan")
        city.save()
        city = CityModel(1489425, "Tomsk")
        city.save()
        city = CityModel(498817, "Saint Petersburg")
        city.save()
        city = CityModel(524901, "Moscow")
        city.save()
        city = CityModel(554234, "Kaliningrad")
        city.save()
        city = CityModel(501183, "Rostov")
        city.save()
        city = CityModel(491422, "Sochi")
        city.save()
    }

    private fun initActionBar() {
        toolbar = findViewById(R.id.actionBar) as Toolbar
        toolbar?.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
    }

    fun showCities() {
        val fragment = CitiesFragment()
        showFragment(fragment)
    }

    fun showAddCity() {
        if (MyApplication.instance.isTablet()) {
            var frag = supportFragmentManager.findFragmentByTag("addCity")
            if (frag != null){
                supportFragmentManager.beginTransaction().remove(frag).commit()
            }
            AddCityDialogFragment().show(supportFragmentManager, "addCity")
//            showDetailFragment(AddCityFragment(), true)
        } else {
            showFragment(AddCityFragment(), true)
        }
    }

    fun showCity(city: CityModel, index: Int = 0, sharedElements: List<Pair<View, String>>? = null) {
        val fragment = CityDetailFragment()
        val arguments = Bundle()
        arguments.putLong("city", city.cityId!!)
        arguments.putInt("index", index)
        fragment.arguments = arguments
        if (MyApplication.instance.isTablet()) {
            showDetailFragment(fragment)
        } else {
            showFragment(fragment, true, sharedElements)
        }
    }


    private fun showFragment(fragment: BaseFragment, addBack: Boolean = false, sharedElements: List<Pair<View, String>>? = null) {
        var transaction = supportFragmentManager.beginTransaction()
        transaction = transaction.replace(R.id.contentLayout, fragment, "fragment")
        if (addBack) {
            transaction = transaction.addToBackStack(fragment.toString())
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !MyApplication.instance.isTablet()) {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.contentLayout)
            if (currentFragment != null) {
                currentFragment.setSharedElementReturnTransition(TransitionInflater.from(
                        this).inflateTransition(R.transition.trans));
                currentFragment.setExitTransition(TransitionInflater.from(
                        this).inflateTransition(android.R.transition.fade));

                fragment.setSharedElementEnterTransition(TransitionInflater.from(
                        this).inflateTransition(R.transition.trans));
                fragment.setEnterTransition(TransitionInflater.from(
                        this).inflateTransition(android.R.transition.fade));
            }
        }
        if (sharedElements != null) {
            for (item in sharedElements!!){
                transaction.addSharedElement(item.first, item.second)
            }
        }
        transaction.commit()
    }

    private fun showDetailFragment(fragment: BaseFragment, addBack: Boolean = false) {
        var transaction = supportFragmentManager.beginTransaction()
        transaction = transaction.replace(R.id.detailLayout, fragment, "fragment")
        if (addBack) {
            transaction = transaction.addToBackStack(fragment.toString())
        }
        transaction.commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}
