package com.example.countdown_app_cupcackteam

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.countdown_app_cupcackteam.Event

@Dao
interface DAO {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: Event)

    @Query("SELECT * FROM event_table WHERE id = :eventId LIMIT 1")
    suspend fun getEvent(eventId: Int): Event?

    @Query("SELECT * FROM event_table")
    fun getAllEvents(): LiveData<List<Event>>

    @Query("SELECT * FROM event_table WHERE title = :eventTitle")
    suspend fun getTitle(eventTitle:String): Event?

    @Delete
    suspend fun deletEvent(event: Event)



}