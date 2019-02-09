package com.github.naz013.tasker.settings.backup

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.github.naz013.tasker.R
import com.github.naz013.tasker.arch.BaseFragment
import com.github.naz013.tasker.data.AppDb
import com.github.naz013.tasker.data.TaskGroup
import com.github.naz013.tasker.utils.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import com.mcxiaoke.koi.ext.onClick
import com.mcxiaoke.koi.ext.toast
import kotlinx.android.synthetic.main.fragment_backup_settings.*
import timber.log.Timber

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
class BackupSettingsFragment : BaseFragment() {

    companion object {
        private const val REQUEST_CODE_SIGN_IN = 4
        private const val PERMISSION_LOCAL = 1425
        private const val PERMISSION_ACCOUNTS = 1426

    }

    private var mProgress: ProgressDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_backup_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab.onClick { findNavController().navigateUp() }
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
        showProgress()
        launchDefault {
            val appDb = AppDb.getInMemoryDatabase(activity!!)
            val drive = LocalDrive(activity!!)
            val oldList = appDb.groupDao().getAll()
            val cloudList = drive.restoreFromDrive()
            Timber.d("startGoogleSync: ${oldList.size}, ${cloudList.size}")
            if (!cloudList.isEmpty() && !oldList.isEmpty()) {
                Timber.d("startGoogleSync: merge")
                withUIContext {
                    hideProgress()
                    showMergeDialog(oldList, cloudList, "SD Card")
                }
            } else if (!cloudList.isEmpty()) {
                Timber.d("startGoogleSync: local")
                appDb.groupDao().insert(cloudList)
                withUIContext {
                    hideProgress()
                    toast("Found ${cloudList.size} groups")
                }
            } else {
                Timber.d("startGoogleSync: nothing")
                withUIContext {
                    hideProgress()
                    toast("Nothing restored")
                }
            }
        }
    }

    private fun initLocalButton() {
        if (Prefs.getInstance(context!!).isLocalBackupEnabled()) {
            localButton.text = getString(R.string.enabled)
        } else {
            localButton.text = getString(R.string.disabled)
        }
    }

    private fun googleClick() {
        if (isLogged()) logOut()
        else logIn()
    }

    private fun logIn() {
        Timber.d("logIn: ")
        if (hasPermission(Manifest.permission.GET_ACCOUNTS)) {
            askAccount()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.GET_ACCOUNTS), PERMISSION_ACCOUNTS)
            }
        }
    }

    private fun askAccount() {
        Timber.d("askAccount: ")
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Scope(DriveScopes.DRIVE_APPDATA))
                .requestEmail()
                .build()
        val client = GoogleSignIn.getClient(activity!!, signInOptions)
        activity?.startActivityForResult(client.signInIntent, REQUEST_CODE_SIGN_IN)
    }

    private fun initGoogleButton() {
        if (isLogged()) {
            googleButton.text = getString(R.string.enabled)
        } else {
            googleButton.text = getString(R.string.disabled)
        }
    }

    private fun isLogged(): Boolean {
        val email = Prefs.getInstance(context!!).getGoogleEmail()
        val res = email.matches(".*@.*".toRegex())
        Timber.d("isLogged: $res")
        return res
    }

    private fun logOut() {
        Timber.d("logOut: ")
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Scope(DriveScopes.DRIVE_APPDATA))
                .requestEmail()
                .build()
        val client = GoogleSignIn.getClient(activity!!, signInOptions)
        client.signOut().addOnSuccessListener {
            Prefs.getInstance(activity!!).setGoogleEmail("")
            initGoogleButton()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SIGN_IN && resultCode == RESULT_OK) {
            handleSignInResult(data)
        } else {
            toast(getString(R.string.failed_to_login_to_drive))
        }
    }

    private fun finishLogin(email: String?) {
        if (email != null) {
            Prefs.getInstance(activity!!).setGoogleEmail(email)
            initGoogleButton()
            startGoogleSync()
        }
    }

    private fun startGoogleSync() {
        showProgress()
        launchDefault {
            val appDb = AppDb.getInMemoryDatabase(activity!!)
            val drive = GoogleDrive(activity!!)
            val oldList = appDb.groupDao().getAll()
            val cloudList = drive.restoreFromDrive()
            Timber.d("startGoogleSync: ${oldList.size}, ${cloudList.size}")
            if (!cloudList.isEmpty() && !oldList.isEmpty()) {
                Timber.d("startGoogleSync: merge")
                withUIContext {
                    hideProgress()
                    showMergeDialog(oldList, cloudList, "Google Drive")
                }
            } else if (!cloudList.isEmpty()) {
                Timber.d("startGoogleSync: local")
                appDb.groupDao().insert(cloudList)
                withUIContext {
                    hideProgress()
                    toast("Found ${cloudList.size} groups")
                }
            } else {
                Timber.d("startGoogleSync: nothing")
                withUIContext {
                    hideProgress()
                    toast("Nothing restored")
                }
            }
        }
    }

    private fun showMergeDialog(oldList: List<TaskGroup>, cloudList: List<TaskGroup>, storage: String) {
        val dialog = AlertDialog.Builder(activity!!, dialogStyle())
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
        launchDefault {
            appDb.groupDao().deleteAll()
            appDb.groupDao().insert(list)
        }
    }

    private fun handleSignInResult(result: Intent?) {
        Timber.d("handleSignInResult: $result")
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener { googleAccount ->
                    Timber.d("handleSignInResult: ${googleAccount.email}")
                    finishLogin(googleAccount.account?.name ?: "")
                    initGoogleButton()
                }
                .addOnFailureListener {
                    Timber.d("handleSignInResult: ${it.message}")
                    toast(getString(R.string.failed_to_login_to_drive))
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