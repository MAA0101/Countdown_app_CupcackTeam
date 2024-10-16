package com.example.countdown_app_cupcackteam

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event_table")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val eventTime: Long // Store the event time as a timestamp
)

