package com.example.countdown_app_cupcackteam

import android.app.TimePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.Calendar
import java.util.concurrent.TimeUnit






    @Composable
    fun AddSubjectDialog(
        isOpen: Boolean,
        title: String = "Create new event",
        onDismissRequest: () -> Unit,
        onConfirmButtonClick: () -> Unit,
        viewModel: EventViewModel, context: Context
    ) {var eventTitle by remember { mutableStateOf(TextFieldValue("")) }
        var selectedDateTime by remember { mutableStateOf(Calendar.getInstance()) }
        var isDatePicked by remember { mutableStateOf(false) }
        if (isOpen) {

            AlertDialog(
                modifier = Modifier.height(350.dp),
                onDismissRequest = onDismissRequest,
                title = { Text(text = title) },
                text = {
                    Column {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {}
                        val charLimit = 20
                        OutlinedTextField(value = eventTitle,
                            onValueChange = { if (it.text.length <= charLimit) {
                                eventTitle = it }},
                            label = { Text(text = "Enter event Name") },
                            singleLine = true,

                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row( modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                            ){
                        if (isDatePicked) {
                        Text(
                            text = "${selectedDateTime.time}",
                            modifier = Modifier.padding(top = 15.dp)
                        )}
                            Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = {
                            openDateTimePicker(selectedDateTime, context) { dateTime ->
                                selectedDateTime = dateTime
                                isDatePicked = true
                            }
                        }, modifier = Modifier.padding(start = 1.dp)

                        )

                        {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Add event"
                            )
                        }
                        }



                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismissRequest) {
                        eventTitle = TextFieldValue("")
                        isDatePicked = false
                        Text(text = "Cancel")
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (eventTitle.text.isNotEmpty() && isDatePicked) {

                            val event = Event(
                                title = eventTitle.text,
                                eventTime = selectedDateTime.timeInMillis

                            )
                            viewModel.insert(event) // Save the event in the database


                            scheduleCountdown(event, context) // Schedule countdown with WorkManager

                            Toast.makeText(context, "Event created!", Toast.LENGTH_SHORT).show()
                            // Reset input

                            eventTitle = TextFieldValue("")
                            isDatePicked = false
                            onConfirmButtonClick()
                        } else {


                            Toast.makeText(
                                context,
                                "Please enter a title and pick a date/time!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }



                    }) {
                        Text(text = "Save")

                    }
                }
            )
        }


    }

//------------------------------------------------------------------------------------------------------------------------------------------------------------------



fun openDateTimePicker(
    calendar: Calendar, context: Context,
    onDateTimePicked: (Calendar) -> Unit
) {


    // Open Date Picker Dialog
    android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            // Once date is selected, open Time Picker Dialog
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)
                    onDateTimePicked(calendar)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),

                true
            ).show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}


fun scheduleCountdown(event: Event, context: Context) {

    val countdownWorkRequest2 = OneTimeWorkRequestBuilder<CountdownWorker>()
        .setInitialDelay(
            (event.eventTime - 120000) - System.currentTimeMillis(),
            TimeUnit.MILLISECONDS
        )
        .setInputData(
            workDataOf(
                "event_id" to event.id,
                "event_time" to event.eventTime,
                "event_text" to event.title,
                "event_title" to "There is an event getting closer"
            )
        )
        .build()

    val countdownWorkRequest = OneTimeWorkRequestBuilder<CountdownWorker>()
        .setInitialDelay(event.eventTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
        .setInputData(
            workDataOf(
                "event_id" to event.id,
                "event_time" to event.eventTime,
                "event_text" to event.title,
                "event_title" to "There is an event today!"
            )
        )
        .build()


    // WorkManager.getInstance(context).enqueueUniqueWork(event.title.toString(), ExistingWorkPolicy.REPLACE, countdownWorkRequest2)
    WorkManager.getInstance(context).beginUniqueWork(
        event.title.toString(),
        ExistingWorkPolicy.REPLACE,
        countdownWorkRequest2
    ).then(countdownWorkRequest).enqueue()

    //stoped here : problem can't cancel the notification plus there is something called memory leak
    //need to change from context to appcontext


}