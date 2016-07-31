package deonix.aga.elegiontest.ui.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by deonix on 29.06.16.
 */
abstract class BaseFragment: Fragment() {

    abstract fun getViewId(): Int;

    var actionBar: ActionBar? = null
    var rootView: View? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater?.inflate(getViewId(), container, false)
        setHasOptionsMenu(true)
        retainInstance = true
        if (arguments != null) {
            initArguments(arguments)
        }
        rootView = view
        initView(rootView!!)
        initActionBar()
        return view
    }

    open fun initArguments(arguments: Bundle) {

    }

    open fun initActionBar() {
        actionBar = (getActivity() as AppCompatActivity).supportActionBar
        actionBar?.title = getTitle()
        actionBar?.setDisplayHomeAsUpEnabled(false)
        actionBar?.setDisplayShowHomeEnabled(false)

    }

    open fun getTitle(): String? {
        return ""
    }

    open fun initView(view: View) {

    }
}