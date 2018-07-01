package com.github.naz013.tasker

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.github.naz013.tasker.data.AppDb
import com.github.naz013.tasker.data.TaskGroup
import com.github.naz013.tasker.utils.Notifier
import com.github.naz013.tasker.utils.Prefs
import com.github.naz013.tasker.utils.TimeUtils
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        if (!Prefs.getInstance(this).isFirstAdded()) {
            addEmptyGroups()
        } else if (!TimeUtils.isSameDay(Prefs.getInstance(this@SplashScreenActivity).getLastLaunch())) {
            verifyGroups()
        } else {
            openApp()
        }
    }

    private fun verifyGroups() {
        launch(CommonPool) {
            val db = AppDb.getInMemoryDatabase(this@SplashScreenActivity)
            var groups = db.groupDao().getAll()
            val prefs = Prefs.getInstance(this@SplashScreenActivity)
            val clear = prefs.getClearOnDay()
            if (clear == Prefs.ENABLED) {
                groups.forEach {
                    it.tasks.clear()
                }
                db.groupDao().insert(groups)
            } else if (clear == Prefs.CUSTOM) {
                val ids = prefs.getStringList(Prefs.CLEAR_GROUP_IDS)
                if (!ids.isEmpty()) {
                    groups.filter { ids.contains(it.id.toString()) }.forEach {
                        it.tasks.clear()
                    }
                    db.groupDao().insert(groups)
                }
            }

            groups = db.groupDao().getAll()
            val unCheck = prefs.getClearChecks()
            Log.d("SplashScreenActivity", "onCreate: $unCheck")
            if (unCheck == Prefs.ENABLED) {
                groups.forEach {
                    it.tasks.forEach { it.done = false }
                }
                Log.d("SplashScreenActivity", "onCreate: $groups")
                db.groupDao().insert(groups)
            } else if (unCheck == Prefs.CUSTOM) {
                val ids = prefs.getStringList(Prefs.CLEAR_CHECKS_IDS)
                if (!ids.isEmpty()) {
                    groups.filter { ids.contains(it.id.toString()) }.forEach {
                        it.tasks.forEach { it.done = false }
                    }
                    Log.d("SplashScreenActivity", "onCreate: $ids, $groups")
                    db.groupDao().insert(groups)
                }
            }

            groups = db.groupDao().getAll()
            withContext(UI) {
                val notifier = Notifier(this@SplashScreenActivity)
                groups.forEach {
                    if (it.notificationEnabled) notifier.showNotification(it)
                    else notifier.hideNotification(it)
                }
                openApp()
            }
        }
    }

    private fun addEmptyGroups() {
        launch(CommonPool) {
            val db = AppDb.getInMemoryDatabase(this@SplashScreenActivity)
            val groups = db.groupDao().getAll()
            if (groups.isEmpty()) {
                db.groupDao().insert(TaskGroup(0, "#FF4081", 0, "Todo", mutableListOf()))
                db.groupDao().insert(TaskGroup(0, "#69F0AE", 1, "Places to go", mutableListOf()))
                db.groupDao().insert(TaskGroup(0, "#FFAB40", 2, "Talk with", mutableListOf()))
            }
            Prefs.getInstance(this@SplashScreenActivity).setFirstAdded(true)
            delay(250)
            withContext(UI) {
                openApp()
            }
        }
    }

    private fun openApp() {
        startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
        finish()
    }
}
