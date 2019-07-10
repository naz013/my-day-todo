package com.github.naz013.tasker.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TasksConverter {
    @Suppress("unused")
    @TypeConverter
    fun toJson(items: List<Task>): String = Gson().toJson(items)

    @Suppress("unused")
    @TypeConverter
    fun toList(json: String): List<Task> {
        val type = object : TypeToken<List<Task>>() {}.type
        return Gson().fromJson(json, type)
    }
}