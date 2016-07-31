package deonix.aga.elegiontest.adapters

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ProgressBar
import android.widget.TextView
import deonix.aga.elegiontest.R
import deonix.aga.elegiontest.db.CityModel
import deonix.aga.elegiontest.other.MyApplication

/**
 * Created by deonix on 10.07.16.
 */
abstract class CitiesAdapter(val context: Context, val cities: List<CityModel>) : BaseAdapter(), View.OnClickListener, View.OnLongClickListener {

    var selectedCity: CityModel? = null

    override fun getCount(): Int {
        return cities.size
    }

    override fun getItem(position: Int): Any? {
        if (position == cities.size)
            return null
        return cities.get(position)
    }

    override fun getItemId(p0: Int): Long {
        if (p0 == cities.size)
            return -1
        return cities.get(p0).cityId!!
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View? {
        var cachedItem: ItemCache?
        var contentView: View ? = view
        when (contentView) {
            null -> {
                contentView = LayoutInflater.from(context).inflate(R.layout.item_city, parent, false)
                cachedItem = ItemCache(
                        rootView = contentView?.findViewById(R.id.card_view)
                        , cityNameTextView = contentView?.findViewById(R.id.cityName) as TextView
                        , tempTextView = contentView?.findViewById(R.id.temp) as TextView
                        , process = contentView?.findViewById(R.id.updateProcess) as ProgressBar
                        , bg = contentView?.findViewById(R.id.bg))
                contentView?.setTag(cachedItem)
            }
            else -> {
                cachedItem = contentView?.getTag() as ItemCache
            }
        }
        val currentPost = getItem(position) as CityModel
        cachedItem?.cityNameTextView?.setText(currentPost.name)
        if (currentPost.equals(selectedCity)){
            cachedItem?.bg?.setBackgroundColor(Color.GRAY)
        } else {
            cachedItem?.bg?.setBackgroundColor(Color.TRANSPARENT)
        }
        if (MyApplication.requestManager.hasRequest("updateCity", currentPost.cityId.toString())){
            cachedItem?.tempTextView?.visibility = View.GONE
            cachedItem?.process?.visibility = View.VISIBLE
        } else {
            cachedItem?.tempTextView?.visibility = View.VISIBLE
            cachedItem?.process?.visibility = View.GONE
            if (currentPost.main != null) {
                var tempText = ""
                if (currentPost.main?.temp!!.toInt() > 0) {
                    tempText = "+"
                }
                if (currentPost.main?.temp!!.toInt() < 0){
                    tempText = "-"
                }
                tempText += "${currentPost.main?.temp.toString()}°C"
                cachedItem?.tempTextView?.setText(tempText)
            } else {
                cachedItem?.tempTextView?.setText(MyApplication.instance.getString(R.string.not_loading))
            }
        }
        if (cachedItem?.rootView != null) {
            cachedItem?.rootView?.setTag(R.id.tempView, cachedItem?.tempTextView)
            cachedItem?.rootView?.tag = position
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cachedItem?.rootView?.transitionName = "card" + position
                cachedItem?.tempTextView?.transitionName = "temp" + position
            }
            cachedItem?.rootView?.setOnClickListener(this)
            cachedItem?.rootView?.setOnLongClickListener(this)
        }
        return contentView
    }

    class ItemCache(val rootView: View?, val cityNameTextView: TextView?, val tempTextView: TextView?, val process: ProgressBar?, val bg: View?)
}