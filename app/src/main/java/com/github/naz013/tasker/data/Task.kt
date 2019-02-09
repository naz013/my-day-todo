package com.github.naz013.tasker.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.github.naz013.tasker.utils.TimeUtils
import com.google.gson.annotations.SerializedName
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
@Entity
data class Task(
        @PrimaryKey(autoGenerate = true)
        @SerializedName("id")
        var id: Int,
        @SerializedName("done")
        var done: Boolean,
        @SerializedName("summary")
        var summary: String,
        @SerializedName("groupId")
        var groupId: Int,
        @SerializedName("important")
        var important: Boolean,
        @SerializedName("comment")
        var comment: String,
        @SerializedName("dt")
        var dt: String
) {
    @Ignore
    constructor() : this((UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE).toInt(),
            false, "", 0, false, "", TimeUtils.getGmtStamp())
}