package com.github.naz013.tasker.data

import androidx.room.*
import com.google.gson.annotations.SerializedName

@Entity(indices = [Index("position"), Index("name")])
@TypeConverters(TasksConverter::class)
data class TaskGroup(
        @PrimaryKey(autoGenerate = true)
        @SerializedName("id")
        var id: Int,
        @SerializedName("color")
        var color: String,
        @SerializedName("position")
        var position: Int,
        @SerializedName("name")
        var name: String,
        @SerializedName("tasks")
        var tasks: MutableList<Task>,
        @SerializedName("active")
        var active: Boolean = true,
        @SerializedName("notificationEnabled")
        var notificationEnabled: Boolean = false
) {
    @Ignore
    constructor():this(0, "", 0, "", mutableListOf())
}