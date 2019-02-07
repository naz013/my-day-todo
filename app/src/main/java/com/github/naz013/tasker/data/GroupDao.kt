package com.github.naz013.tasker.data

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

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
@Dao
interface GroupDao {
    @Transaction
    @Query("select * from TaskGroup order by position")
    fun loadAll(): LiveData<List<TaskGroup>>

    @Query("select * from TaskGroup where id = :id")
    fun loadById(id: Int): LiveData<TaskGroup?>

    @Transaction
    @Query("select * from TaskGroup order by position")
    fun getAll(): List<TaskGroup>

    @Query("select * from TaskGroup where id = :id")
    fun getById(id: Int): TaskGroup?

    @Insert(onConflict = REPLACE)
    fun insert(group: TaskGroup)

    @Insert(onConflict = REPLACE)
    fun insert(groups: List<TaskGroup>)

    @Delete
    fun delete(group: TaskGroup)

    @Query("DELETE FROM TaskGroup")
    fun deleteAll()
}