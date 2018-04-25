package com.github.naz013.tasker.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
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
        var id: Int,
        var done: Boolean,
        var summary: String,
        var groupId: Int,
        var important: Boolean,
        var comment: String,
        var dt: String
) {
    @Ignore
    constructor() : this((UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE).toInt(), false, "", 0, false, "", "")
}