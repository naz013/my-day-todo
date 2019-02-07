package com.github.naz013.tasker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

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
@Database(entities = [TaskGroup::class, Task::class],
        version = 2,
        exportSchema = false)
abstract class AppDb : RoomDatabase() {

    abstract fun groupDao(): GroupDao

    companion object {
        private const val DB_NAME = "app_db"
        private var INSTANCE: AppDb? = null

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE TaskGroup ADD COLUMN notificationEnabled INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getInMemoryDatabase(context: Context): AppDb {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext, AppDb::class.java, DB_NAME)
                        .addMigrations(MIGRATION_1_2)
                        .build()
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

    fun cleanDb() {
        groupDao().deleteAll()
    }
}


