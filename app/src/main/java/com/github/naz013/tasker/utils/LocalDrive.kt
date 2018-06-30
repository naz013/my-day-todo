package com.github.naz013.tasker.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.util.Log
import com.github.naz013.tasker.data.AppDb
import com.github.naz013.tasker.data.TaskGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.*


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
class LocalDrive(val context: Context) {

    companion object {
        private const val FILE_NAME = "data.json"
    }

    fun saveToDrive() {
        if (!isAllowed()) return
        val dir = getDir() ?: return
        deleteDataJson()
        Log.d("LocalDrive", "saveToDrive: ")
        try {
            val groups = AppDb.getInMemoryDatabase(context).groupDao().getAll()
            val data = Gson().toJson(groups)
            writeFile(File(dir, FILE_NAME), data)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun deleteDataJson() {
        if (!isAllowed()) return
        val dir = getDir() ?: return
        val files = dir.listFiles() ?: return
        for (file in files) {
            if (file.name.contains("data")) {
                file.delete()
            }
        }
    }

    fun restoreFromDrive(): List<TaskGroup> {
        if (!isAllowed()) return listOf()
        val dir = getDir() ?: return listOf()
        val files = dir.listFiles() ?: return listOf()
        Log.d("LocalDrive", "restoreFromDrive: ")
        for (file in files) {
            if (file.name == FILE_NAME) {
                val type = object : TypeToken<List<TaskGroup>>() {}.type
                try {
                    return Gson().fromJson(readFileToJson(file.toString()), type)
                } catch (e: IOException) {

                }
            }
        }
        return listOf()
    }

    private fun isAllowed(): Boolean {
        if (!hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                || !hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            return false
        }
        if (!Prefs.getInstance(context).isLocalBackupEnabled()) return false
        if (!isSdPresent()) return false
        return true
    }

    @Throws(IOException::class)
    private fun readFileToJson(path: String): String {
        val inputStream = FileInputStream(path)
        val r = BufferedReader(InputStreamReader(inputStream))
        val total = StringBuilder()
        var line: String?
        do {
            line = r.readLine()
            if (line != null) {
                total.append(line)
            }
        } while (line != null)
        inputStream.close()
        val res = total.toString()
        return if ((res.startsWith("{") && res.endsWith("}")) || (res.startsWith("[") && res.endsWith("]")))
            res
        else {
            throw IOException("Bad JSON")
        }
    }

    private fun writeFile(file: File, data: String?) {
        if (data == null) return
        val inputStream = ByteArrayInputStream(data.toByteArray())
        val buffer = ByteArray(8192)
        var bytesRead: Int
        val output = ByteArrayOutputStream()
        try {
            do {
                bytesRead = inputStream.read(buffer)
                if (bytesRead != -1) {
                    output.write(buffer, 0, bytesRead)
                }
            } while (bytesRead != -1)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (file.exists()) {
            file.delete()
        }
        val fw = FileWriter(file)
        fw.write(output.toString())
        fw.close()
        output.close()
    }

    private fun getDir(): java.io.File? {
        return if (isSdPresent()) {
            val sdPath = Environment.getExternalStorageDirectory()
            val dir = java.io.File(sdPath.toString() + "/MyDay")
            if (!dir.exists() && dir.mkdirs()) {
                dir
            } else dir
        } else {
            null
        }
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun isSdPresent(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state
    }
}