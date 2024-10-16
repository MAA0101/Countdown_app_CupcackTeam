package com.example.countdown_app_cupcackteam

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.countdown_app_cupcackteam.ui.theme.Countdown_app_CupcackTeamTheme
import com.example.countdown_app_cupcackteam.ui.theme.joypixle
import com.example.countdown_app_cupcackteam.ui.theme.pixlify
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private val context: Context = this
    private var isDialogShown = mutableStateOf(false)
    private val eventViewModel: EventViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        val handler = handlePermissionRespons()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Countdown_app_CupcackTeamTheme {

                EventCountdownScreen(viewModel = eventViewModel, context = context)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)//so it can make this this popping box allowed if the mobile version has the future
                    handler.launch(android.Manifest.permission.POST_NOTIFICATIONS)


            }
        }
    }

    private fun handlePermissionRespons(): ActivityResultLauncher<String> {
        val launcher =
            registerForActivityResult(
                ActivityResultContracts
                    .RequestPermission()
            ) { isGranted ->
                if (isGranted)
                else {
                    isDialogShown.value = true
                }
            }
        return launcher
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventCountdownScreen(viewModel: EventViewModel, context: Context) {
    val events by viewModel.allEvents.observeAsState(emptyList())
    var test by rememberSaveable {
        mutableStateOf(false)
    }

    AddSubjectDialog(
        isOpen = test,
        onDismissRequest = { test = false },
        onConfirmButtonClick = { test = false }, viewModel = viewModel, context = context
    )
    Surface(
        modifier = Modifier
            .fillMaxSize(),

        color = MaterialTheme.colorScheme.background
    ) {


        val scrollBehavior =
            TopAppBarDefaults.exitUntilCollapsedScrollBehavior()//the main screen that show the top app bar and the list

        Scaffold(modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(end = 29.dp)
                        , contentAlignment = Alignment.Center
                    )
                    {
                        Text(text = "My Plans")
                    }
                },
                navigationIcon = {//  +
                    IconButton(onClick = {
                        test = true
                        Toast.makeText(context, "worked!", Toast.LENGTH_SHORT).show()
                    }
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add event")
                    }
                },

                scrollBehavior = scrollBehavior


            )

        }) {

                values ->
            Column {

                LazyColumn(
                    modifier = Modifier.background(Color(0xC4EFB8C8))
                        .fillMaxSize()
                        .padding(values),
                ) {


                    items(events) { event ->
                        var remainingTime by remember { mutableStateOf(event.eventTime - System.currentTimeMillis()) }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .size(width = 300.dp, height = 130.dp)
                                .padding(top = 8.dp),
                            elevation = CardDefaults.cardElevation(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFBEFD5))
                        )
                        {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                LaunchedEffect(event.eventTime) {
                                    while (remainingTime > 0) {
                                        delay(1000L)
                                        remainingTime = event.eventTime - System.currentTimeMillis()
                                    }
                                }
                                if (remainingTime > 0) {
                                    val hours = TimeUnit.MILLISECONDS.toHours(remainingTime)
                                    val minutes =
                                        TimeUnit.MILLISECONDS.toMinutes(remainingTime) % 60
                                    val seconds =
                                        TimeUnit.MILLISECONDS.toSeconds(remainingTime) % 60

                                    Text(
                                        text = "${event.title}:\n\n           $hours:$minutes:$seconds ",
                                        modifier = Modifier
                                            .padding(top = 24.dp)
                                            .padding(start = 4.dp)
                                            .size(300.dp),
                                        color = Color.Black, fontFamily = joypixle, fontWeight = FontWeight.Thin
                                    )

                                } else {
                                    Text(
                                        text = "${event.title}: \n\n          Time's up!",

                                        modifier = Modifier
                                            .padding(top = 15.dp)
                                            .padding(start = 4.dp)
                                            .size(300.dp),
                                        color = Color.Black,fontFamily = joypixle, fontWeight = FontWeight.Thin
                                    )


                                }
                                IconButton(
                                    onClick = { viewModel.delete(event) },
                                    modifier = Modifier.padding(top = 33.dp),
                                    enabled = true
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(35.dp)
                                            .background(Color.Red, RoundedCornerShape(14.dp))
                                            .padding(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "delete event",
                                            tint = Color.White
                                        )
                                    }
                                }

                            }
                        }
                    }


                }
            }

        }


    }


}













