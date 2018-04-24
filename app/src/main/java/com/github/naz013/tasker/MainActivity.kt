package com.github.naz013.tasker

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.github.naz013.tasker.arch.BaseFragment
import com.github.naz013.tasker.arch.NavInterface
import com.github.naz013.tasker.arch.NestedFragment
import com.github.naz013.tasker.home.HomeFragment

class MainActivity : AppCompatActivity(), NavInterface {

    companion object {
        private const val PRESS_AGAIN_TIME = 2000
    }

    private var isBackPressed: Boolean = false
    private var pressedTime: Long = 0
    private val mUiHandler = Handler(Looper.getMainLooper())
    private var fragment: BaseFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.addOnBackStackChangedListener { onStackChanged() }
    }

    override fun onEnterAnimationComplete() {
        super.onEnterAnimationComplete()
        openFragment(HomeFragment.newInstance(), HomeFragment.TAG)
    }

    private fun onStackChanged() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            val f = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
            if (f != null && f is BaseFragment && f.isResumed) f.onBackStackResume()
        }
    }

    override fun onBackPressed() {
        moveBack()
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        clearBackStack()
        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.fragmentContainer, fragment, tag)
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        ft.addToBackStack(tag)
        ft.commit()
    }

    private fun clearBackStack() {
        val fm = supportFragmentManager
        for (i in 0 until fm.backStackEntryCount) {
            fm.popBackStack()
        }
    }

    override fun onFragmentSelect(fragment: BaseFragment) {
        this.fragment = fragment
    }

    override fun openFragment(fragment: BaseFragment, tag: String) {
        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.fragmentContainer, fragment, tag)
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        ft.addToBackStack(tag)
        ft.commit()
    }

    override fun moveBack() {
        if (fragment != null) {
            if (fragment is NestedFragment && fragment?.canGoBack()!!) {
                super.onBackPressed()
                return
            }
        }
        if (isBackPressed) {
            if (System.currentTimeMillis() - pressedTime < PRESS_AGAIN_TIME) {
                finish()
            } else {
                isBackPressed = false
                onBackPressed()
            }
        } else {
            isBackPressed = true
            pressedTime = System.currentTimeMillis()
            Toast.makeText(this, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show()
        }
    }
}
