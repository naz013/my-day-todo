package com.github.naz013.tasker.utils

import android.content.Context
import android.util.Log
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
class GoogleDrive(val context: Context) {

    companion object {
        private const val APPLICATION_NAME = "MyDay/1.0"
        private const val FILE_NAME = "data.json"
    }

    private var driveService: Drive? = null

    init {
        val user = Prefs.getInstance(context).getGoogleEmail()
        Log.d("GoogleDrive", "init: $user")
        if (user.matches(".*@.*".toRegex())) {
            try {
                val credential = GoogleAccountCredential.usingOAuth2(context, Arrays.asList(DriveScopes.DRIVE_APPDATA))
                credential.selectedAccountName = user
                val mJsonFactory = GsonFactory.getDefaultInstance()
                val mTransport = AndroidHttp.newCompatibleTransport()
                driveService = Drive.Builder(mTransport, mJsonFactory, credential).setApplicationName(APPLICATION_NAME).build()
            } catch (e: Exception) {
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
            Log.d("GoogleDrive", "saveToDrive: $data")
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
                Log.d("GoogleDrive", "deleteDataJson: " + file.name)
                if (file.name.contains("data")) {
                    service.files().delete(file.id).execute()
                }
            }
        } catch (e: Exception) {
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
            Log.d("GoogleDrive", "restoreFromDrive: start")
            for (file in files.files) {
                Log.d("GoogleDrive", "restoreFromDrive: " + file.name)
                if (file.name == FILE_NAME) {
                    val outputStream = ByteArrayOutputStream()
                    service.files().get(file.id).executeMediaAndDownloadTo(outputStream)
                    val type = object : TypeToken<List<TaskGroup>>() {}.type
                    return Gson().fromJson(outputStream.toString(), type)
                }
            }
        } catch (e: Exception) {
        }
        return listOf()
    }
}