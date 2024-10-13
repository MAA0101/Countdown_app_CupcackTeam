package com.example.eventcountdown.ui.theme.dashboard.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockSelection

@Composable
fun MyCard(mytext:String)
{
    Card(modifier = Modifier
        .fillMaxWidth()
        .size(width = 300.dp, height = 150.dp)
        .padding(all = 8.dp),
        elevation = CardDefaults.cardElevation(12.dp))
    {
        Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,) {
            Text(text = mytext)

        }

    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun  DashboardScreen () {

    var isAddEventNameDialogOpen by rememberSaveable {
        mutableStateOf(false) }
    var eventName by remember { mutableStateOf("") }


    AddSubjectDialog(
        isOpen =isAddEventNameDialogOpen ,
        EventName =eventName ,
        onEventNameChange ={eventName=it} ,
        onDismissRequest = { isAddEventNameDialogOpen=false},
        onConfirmButtonClick = {
            isAddEventNameDialogOpen=false
        }
    )


    val calenderState= rememberSheetState()
    CalendarDialog(state = calenderState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true,
            style = CalendarStyle.MONTH,
        ),
        selection = CalendarSelection.Date{
                date->
            Log.d("selectedDate","$date")
        })

    val clockState= rememberSheetState()
    ClockDialog(state = clockState,
        selection = ClockSelection.HoursMinutes{ hours, minutes ->
            Log.d("SelectedDate","$hours:$minutes")
        } )
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {


        val scrollBehavior= TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        Scaffold(modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
            TopAppBar(title = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                )
                {
                    Text(text = "My Plans")
                }
            },
                navigationIcon = {
                    IconButton(onClick = {
                        calenderState.show()
                        clockState.show()
                        isAddEventNameDialogOpen=true
                    }
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add event")
                    }
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Drop down calender",

                            )
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search event"
                        )
                    }
                },
                scrollBehavior = scrollBehavior


            )

        }) {


                values->
            Column {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(values),
                ) {
                    items(count = 200)
                    { numb ->
                        MyCard(
                            mytext = "$numb",
                        )
                    }
                }
            }


        }


    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DashboardScreen()
}