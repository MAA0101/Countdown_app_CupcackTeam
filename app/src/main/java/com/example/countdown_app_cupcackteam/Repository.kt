package com.example.countdown_app_cupcackteam

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import kotlinx.coroutines.launch

class Repository(private val eventDao: DAO) {

    fun getAllEvents(): LiveData<List<Event>> = eventDao.getAllEvents()

    suspend fun insert(event: Event) = eventDao.insert(event)

    suspend fun getEvent(eventId: Int): Event? = eventDao.getEvent(eventId)
    suspend fun getTitle(eventTitle:String):Event?=eventDao.getTitle(eventTitle)
    suspend fun deletEvent(event:Event){
        eventDao.deletEvent(event)}
}

class EventViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: Repository
    val allEvents: LiveData<List<Event>>
    private val appContext: Context = application

    init {
        val eventDao = DataBase.getDatabase(application).eventDao()
        repository = Repository(eventDao)
        allEvents = repository.getAllEvents()
    }

    fun insert(event: Event) = viewModelScope.launch {
        repository.insert(event)

    }
    fun delete(event: Event)  {

        WorkManager.getInstance(appContext).cancelUniqueWork(event.title)



        viewModelScope.launch {
            repository.deletEvent(event)
        }
    }
    fun getEvent(eventId: Int) = viewModelScope.launch {
        repository.getEvent(eventId)

    }
    fun getTitle(eventTitle:String) = viewModelScope.launch {
        repository.getTitle(eventTitle)

    }

}
