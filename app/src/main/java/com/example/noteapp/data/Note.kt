package com.example.noteapp.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "notes")

data class Note(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    val title: String,
    val description: String,
    val createdAt:Long = System.currentTimeMillis()
): Parcelable
