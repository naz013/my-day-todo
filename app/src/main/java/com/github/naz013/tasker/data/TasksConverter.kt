package com.github.naz013.tasker.data

import android.arch.persistence.room.TypeConverter
import com.github.naz013.tasker.data.Task
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Copyright 2017 Nazar Suhovich
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
class TasksConverter {
    @TypeConverter
    fun toJson(items: List<Task>): String = Gson().toJson(items)

    @TypeConverter
    fun toList(json: String): List<Task> {
        val type = object : TypeToken<List<Task>>() {}.type
        return Gson().fromJson(json, type)
    }
}