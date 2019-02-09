package com.github.naz013.tasker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.naz013.tasker.utils.Prefs
import com.github.naz013.tasker.utils.TimeUtils

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Prefs.getInstance(this).isDarkMode()) {
            setTheme(R.style.AppTheme_Dark)
        } else {
            setTheme(R.style.AppTheme_Light)
        }
        setContentView(R.layout.activity_main)
    }

    override fun onDestroy() {
        Prefs.getInstance(this).setLastLaunch(TimeUtils.getGmtStamp())
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val navHost = supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment)
        navHost?.let { navFragment ->
            navFragment.childFragmentManager.primaryNavigationFragment?.onActivityResult(requestCode, resultCode, data)
        }
    }
}
