package com.github.naz013.tasker.utils

import android.content.Context
import com.crashlytics.android.Crashlytics
import com.github.naz013.tasker.data.AppDb
import com.github.naz013.tasker.data.TaskGroup
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.AbstractInputStreamContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mcxiaoke.koi.ext.isConnected
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.*

class GoogleDrive(val context: Context) {

    companion object {
        private const val APPLICATION_NAME = "MyDay/1.0"
        private const val FILE_NAME = "data.json"
    }

    private var driveService: Drive? = null

    init {
        val user = Prefs.getInstance(context).getGoogleEmail()
        if (user.matches(".*@.*".toRegex())) {
            try {
                val credential = GoogleAccountCredential.usingOAuth2(context, listOf(DriveScopes.DRIVE_APPDATA))
                credential.selectedAccountName = user
                val mJsonFactory = GsonFactory.getDefaultInstance()
                val mTransport = AndroidHttp.newCompatibleTransport()
                driveService = Drive.Builder(mTransport, mJsonFactory, credential).setApplicationName(APPLICATION_NAME).build()
            } catch (e: Exception) {
                Crashlytics.log(e.message)
            }
        } else {
            Prefs.getInstance(context).setGoogleEmail("")
        }
    }

    fun saveToDrive() {
        if (!context.isConnected()) return
        val service = driveService ?: return

        deleteDataJson()

        try {
            val fileMetadata = File()
            fileMetadata.name = FILE_NAME
            fileMetadata.parents = Collections.singletonList("appDataFolder")

            val groups = AppDb.getInMemoryDatabase(context).groupDao().getAll()
            val data = Gson().toJson(groups)
            val array = data.toByteArray(StandardCharsets.UTF_8)
            val stream = ByteArrayInputStream(array)
            val content = object : AbstractInputStreamContent("application/json") {
                override fun getLength(): Long = array.size.toLong()
                override fun retrySupported(): Boolean = true
                override fun getInputStream(): InputStream = stream
            }

            val file = service.files().create(fileMetadata, content).setFields("id").execute()
            println("File ID: " + file.id)
        } catch (e: Exception) {
            Crashlytics.log(e.message)
        }
    }

    private fun deleteDataJson() {
        if (!context.isConnected()) return
        val service = driveService ?: return
        try {
            val files = service.files().list()
                    .setSpaces("appDataFolder")
                    .setFields("nextPageToken, files(id, name)")
                    .setPageSize(10)
                    .execute()
            for (file in files.files) {
                if (file.name.contains("data")) {
                    service.files().delete(file.id).execute()
                }
            }
        } catch (e: Exception) {
            Crashlytics.log(e.message)
        }
    }

    fun restoreFromDrive(): List<TaskGroup> {
        if (!context.isConnected()) return listOf()
        val service = driveService ?: return listOf()

        try {
            val files = service.files().list()
                    .setSpaces("appDataFolder")
                    .setFields("nextPageToken, files(id, name)")
                    .setPageSize(10)
                    .execute()
            for (file in files.files) {
                if (file.name == FILE_NAME) {
                    val outputStream = ByteArrayOutputStream()
                    service.files().get(file.id).executeMediaAndDownloadTo(outputStream)
                    val type = object : TypeToken<List<TaskGroup>>() {}.type
                    return Gson().fromJson(outputStream.toString(), type)
                }
            }
        } catch (e: Exception) {
            Crashlytics.log(e.message)
        }
        return listOf()
    }
}