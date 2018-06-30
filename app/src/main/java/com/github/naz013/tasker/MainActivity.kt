package com.github.naz013.tasker

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.github.naz013.tasker.arch.BaseFragment
import com.github.naz013.tasker.arch.NavInterface
import com.github.naz013.tasker.arch.NestedFragment
import com.github.naz013.tasker.home.HomeFragment
import com.github.naz013.tasker.utils.Prefs
import com.github.naz013.tasker.utils.TimeUtils

class MainActivity : AppCompatActivity(), NavInterface {

    companion object {
        private const val PRESS_AGAIN_TIME = 2000
    }

    private var isBackPressed: Boolean = false
    private var pressedTime: Long = 0
    private var fragment: BaseFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.addOnBackStackChangedListener { onStackChanged() }

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

    override fun onFragmentSelect(fragment: BaseFragment) {
        this.fragment = fragment
    }

    override fun openFragment(fragment: BaseFragment, tag: String) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragmentContainer, fragment, tag)
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

    override fun onDestroy() {
        Prefs.getInstance(this).setLastLaunch(TimeUtils.getGmtStamp())
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("MainActivity", "onActivityResult: ")
        fragment?.onActivityResult(requestCode, resultCode, data)
    }
}
