package com.github.naz013.tasker.data

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

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