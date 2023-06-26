package com.itssuryansh.taaveez

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Poem-table")
data class NotesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val topic: String = "",
    val poem: String = "",
    val date: String = "",
    val createdDate: String = "",
)
