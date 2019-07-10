package com.github.naz013.tasker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

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
)