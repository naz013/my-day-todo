package com.github.naz013.tasker.settings.backup

import android.Manifest
import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.naz013.tasker.R
import com.github.naz013.tasker.arch.NestedFragment
import com.github.naz013.tasker.data.AppDb
import com.github.naz013.tasker.data.TaskGroup
import com.github.naz013.tasker.utils.GoogleDrive
import com.github.naz013.tasker.utils.LocalDrive
import com.github.naz013.tasker.utils.Prefs
import com.google.android.gms.auth.GoogleAuthException
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.UserRecoverableAuthException
import com.google.android.gms.common.AccountPicker
import com.google.api.client.googleapis.extensions.android.accounts.GoogleAccountManager
import com.google.api.services.drive.DriveScopes
import com.mcxiaoke.koi.ext.onClick
import com.mcxiaoke.koi.ext.toast
import kotlinx.android.synthetic.main.fragment_backup_settings.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import java.io.IOException


/**
 * Copyright 2018 Nazar Suhovich
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class BackupSettingsFragment : NestedFragment() {

    companion object {
        const val TAG = "BackupSettingsFragment"
        private const val REQUEST_AUTHORIZATION = 1
        private const val REQUEST_ACCOUNT_PICKER = 3
        private const val PERMISSION_LOCAL = 1425
        private const val PERMISSION_ACCOUNTS = 1426
        private const val RT_CODE = "rt"

        fun newInstance(): BackupSettingsFragment {
            return BackupSettingsFragment()
        }
    }

    private var mAccountName: String? = null
    private var rtIntent: Intent? = null
    private var mProgress: ProgressDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_backup_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab.onClick { navInterface?.moveBack() }
        googleButton.onClick { googleClick() }
        localButton.onClick { localClick() }

        initLocalButton()
        initGoogleButton()
    }

    private fun localClick() {
        if (Prefs.getInstance(context!!).isLocalBackupEnabled()) {
            Prefs.getInstance(context!!).setLocalBackupEnabled(false)
        } else {
            checkLocal()
        }
        initLocalButton()
    }

    private fun checkLocal() {
        if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            enableLocal()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_LOCAL)
            }
        }
    }

    private fun enableLocal() {
        Prefs.getInstance(context!!).setLocalBackupEnabled(true)
        initLocalButton()
        startLocalSync()
    }

    private fun startLocalSync() {
        launch(UI) {
            showProgress()
            withContext(CommonPool) {
                val appDb = AppDb.getInMemoryDatabase(activity!!)
                val drive = LocalDrive(activity!!)
                val oldList = appDb.groupDao().getAll()
                val cloudList = drive.restoreFromDrive()
                Log.d("BackupSettingsFragment", "startGoogleSync: " + oldList.size + ", " + cloudList.size)
                if (!cloudList.isEmpty() && !oldList.isEmpty()) {
                    Log.d("BackupSettingsFragment", "startGoogleSync: merge")
                    withContext(UI) {
                        hideProgress()
                        showMergeDialog(oldList, cloudList, "SD Card")
                    }
                } else if (!cloudList.isEmpty()) {
                    Log.d("BackupSettingsFragment", "startGoogleSync: local")
                    appDb.groupDao().insert(cloudList)
                    withContext(UI) {
                        hideProgress()
                        toast("Found ${cloudList.size} groups")
                    }
                } else {
                    Log.d("BackupSettingsFragment", "startGoogleSync: nothing")
                    withContext(UI) {
                        hideProgress()
                        toast("Nothing restored")
                    }
                }
            }
        }
    }

    private fun initLocalButton() {
        if (Prefs.getInstance(context!!).isLocalBackupEnabled()) {
            localButton.text = getString(R.string.enabled)
            localButton.setBackgroundResource(R.drawable.button_rounded_green)
        } else {
            localButton.text = getString(R.string.disabled)
            localButton.setBackgroundResource(R.drawable.button_rounded_red)
        }
    }

    private fun googleClick() {
        if (isLogged()) logOut()
        else logIn()
    }

    private fun logIn() {
        Log.d("BackupSettingsFragment", "logIn: ")
        if (hasPermission(Manifest.permission.GET_ACCOUNTS)) {
            askAccount()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.GET_ACCOUNTS), PERMISSION_ACCOUNTS)
            }
        }
    }

    private fun askAccount() {
        Log.d("BackupSettingsFragment", "askAccount: ")
        val intent = AccountPicker.newChooseAccountIntent(null, null,
                arrayOf("com.google"), true, null, null, null, null)
        activity!!.startActivityForResult(intent, REQUEST_AUTHORIZATION)
    }

    private fun initGoogleButton() {
        if (isLogged()) {
            googleButton.text = getString(R.string.enabled)
            googleButton.setBackgroundResource(R.drawable.button_rounded_green)
        } else {
            googleButton.text = getString(R.string.disabled)
            googleButton.setBackgroundResource(R.drawable.button_rounded_red)
        }
    }

    private fun isLogged(): Boolean {
        val email = Prefs.getInstance(context!!).getGoogleEmail()
        val res = email.matches(".*@.*".toRegex())
        Log.d("BackupSettingsFragment", "isLogged: $res")
        return res
    }

    private fun logOut() {
        Log.d("BackupSettingsFragment", "logOut: ")
        Prefs.getInstance(activity!!).setGoogleEmail("")
        initGoogleButton()
    }

    private fun getAndUseAuthTokenInAsyncTask(account: Account) {
        launch(UI) {
            showProgress()
            withContext(CommonPool) {
                val token = getAccessToken(account)
                withContext(UI) {
                    hideProgress()
                    if (token != null) {
                        if (token == RT_CODE) {
                            if (rtIntent != null) {
                                activity!!.startActivityForResult(rtIntent, REQUEST_ACCOUNT_PICKER)
                            } else {
                                toast(getString(R.string.failed_to_login_to_drive))
                            }
                        } else {
                            finishLogin()
                        }
                    } else {
                        toast(getString(R.string.failed_to_login_to_drive))
                    }
                }
            }
        }
    }

    private fun hideProgress() {
        try {
            val progress = mProgress
            if (progress != null && progress.isShowing) {
                progress.dismiss()
            }
            mProgress = null
        } catch (ignored: IllegalArgumentException) {
        }
    }

    private fun showProgress() {
        val progress = mProgress
        if (progress != null && progress.isShowing) return
        val mProgress = ProgressDialog(activity, ProgressDialog.STYLE_SPINNER)
        mProgress.setMessage(getString(R.string.please_wait))
        mProgress.setCancelable(false)
        mProgress.isIndeterminate = true
        mProgress.show()
        this.mProgress = mProgress
    }

    private fun getAccessToken(account: Account): String? {
        return try {
            val scope = "oauth2:" + DriveScopes.DRIVE_APPDATA
            GoogleAuthUtil.getToken(activity, account, scope)
        } catch (e: UserRecoverableAuthException) {
            rtIntent = e.intent
            RT_CODE
        } catch (e: ActivityNotFoundException) {
            null
        } catch (e: GoogleAuthException) {
            null
        } catch (e: IOException) {
            null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_AUTHORIZATION && resultCode == RESULT_OK) {
            mAccountName = data!!.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
            val gam = GoogleAccountManager(activity)
            getAndUseAuthTokenInAsyncTask(gam.getAccountByName(mAccountName))
        } else if (requestCode == REQUEST_ACCOUNT_PICKER && resultCode == RESULT_OK) {
            mAccountName = data!!.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
            finishLogin()
            initGoogleButton()
        } else {
            toast(getString(R.string.failed_to_login_to_drive))
        }
    }

    private fun finishLogin() {
        val account = mAccountName
        if (account != null) {
            Prefs.getInstance(activity!!).setGoogleEmail(account)
            initGoogleButton()
            startGoogleSync()
        }
    }

    private fun startGoogleSync() {
        launch(UI) {
            showProgress()
            withContext(CommonPool) {
                val appDb = AppDb.getInMemoryDatabase(activity!!)
                val drive = GoogleDrive(activity!!)
                val oldList = appDb.groupDao().getAll()
                val cloudList = drive.restoreFromDrive()
                Log.d("BackupSettingsFragment", "startGoogleSync: " + oldList.size + ", " + cloudList.size)
                if (!cloudList.isEmpty() && !oldList.isEmpty()) {
                    Log.d("BackupSettingsFragment", "startGoogleSync: merge")
                    withContext(UI) {
                        hideProgress()
                        showMergeDialog(oldList, cloudList, "Google Drive")
                    }
                } else if (!cloudList.isEmpty()) {
                    Log.d("BackupSettingsFragment", "startGoogleSync: local")
                    appDb.groupDao().insert(cloudList)
                    withContext(UI) {
                        hideProgress()
                        toast("Found ${cloudList.size} groups")
                    }
                } else {
                    Log.d("BackupSettingsFragment", "startGoogleSync: nothing")
                    withContext(UI) {
                        hideProgress()
                        toast("Nothing restored")
                    }
                }
            }
        }
    }

    private fun showMergeDialog(oldList: List<TaskGroup>, cloudList: List<TaskGroup>, storage: String) {
        val dialog = AlertDialog.Builder(activity!!)
        dialog.setMessage("Found ${cloudList.size} groups on $storage and ${oldList.size} in application. " +
                "What to do?")
        dialog.setPositiveButton("Save only from $storage") { _, _ ->
            saveList(cloudList)
        }
        dialog.setNegativeButton("Keep current") { _, _ ->
            saveList(oldList)
        }
        dialog.setNeutralButton("Merge") { _, _ ->
            saveList(oldList + cloudList)
        }
        dialog.create().show()
    }

    private fun saveList(list: List<TaskGroup>) {
        val appDb = AppDb.getInMemoryDatabase(context!!)
        async(CommonPool) {
            appDb.groupDao().deleteAll()
            appDb.groupDao().insert(list)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isEmpty()) return
        when (requestCode) {
            PERMISSION_LOCAL -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableLocal()
                }
            }
            PERMISSION_ACCOUNTS -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    askAccount()
                }
            }
        }
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context!!, permission) == PackageManager.PERMISSION_GRANTED
    }
}