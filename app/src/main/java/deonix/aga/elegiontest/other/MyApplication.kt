package deonix.aga.elegiontest.other

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.activeandroid.ActiveAndroid
import java.util.*

/**
 * Created by deonix on 09.07.16.
 */
class MyApplication(): Application() {

    companion object {
        val preferences: PreferencesManager = PreferencesManager()
        val requestManager: RequestsManager = RequestsManager()

        lateinit var instance: MyApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        ActiveAndroid.initialize(this);
    }

    fun isFirstStart(): Boolean {
        return preferences.getSavedBoolValue("isFirstStart", true)
    }

    fun isTablet(): Boolean {
        val xlarge = instance.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK === Configuration.SCREENLAYOUT_SIZE_XLARGE
        val large = instance.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK === Configuration.SCREENLAYOUT_SIZE_LARGE
        return xlarge || large
    }

    class PreferencesManager {
        fun getSavedBoolValue(valueName: String, default: Boolean = false): Boolean {
            return MyApplication.instance.getSharedPreferences("default", Context.MODE_PRIVATE).getBoolean(valueName, default)
        }

        fun saveBoolValue(valueName: String, default: Boolean) {
            val edit = MyApplication.instance.getSharedPreferences("default", Context.MODE_PRIVATE).edit()
            edit.putBoolean(valueName, default)
            edit.commit()
        }
    }

    class RequestsManager {

        interface RequestListener {
            fun handle(requestName: String, params: String)
        }

        var listeners: MutableMap<String, RequestListener> = HashMap<String, RequestListener>()

        var requests = HashMap<String, MutableList<String>>()

        fun addRequest(name: String, params: String){
            var list: MutableList<String>? = requests.get(name)
            if (list != null){
                list.add(params)
            } else {
                list = ArrayList<String>()
                list.add(params)
                requests.put(name, list)
            }
        }

        fun removeRequest(name: String, params: String){
            var list: MutableList<String>? = requests.get(name)
            if (list != null){
                list.remove(params)
            }
            handleListener(name, params)
        }

        fun hasRequest(name:String, params: String): Boolean {
            var list: MutableList<String>? = requests.get(name)
            if (list != null){
                if (list.contains(params)){
                    return true
                }
            }
            return false
        }

        fun addListener(name:String, params: String, listener: RequestListener) {
            val key = name + params
            listeners.put(key, listener)
        }

        fun handleListener(name: String, params: String){
            val key = name + params
            if (listeners.containsKey(key)){
                listeners[key]?.handle(name, params)
            }
        }

    }

    fun setIsFirstStart() {
        preferences.saveBoolValue("isFirstStart", false)
    }

}