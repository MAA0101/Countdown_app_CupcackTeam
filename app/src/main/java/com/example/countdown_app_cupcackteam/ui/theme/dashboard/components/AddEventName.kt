package com.example.eventcountdown.ui.theme.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import javax.security.auth.Subject


@Composable
fun AddSubjectDialog(
    isOpen:Boolean,
    title:String="Event Name",
    EventName:String,
    onEventNameChange:(String)->Unit,
    onDismissRequest:()->Unit,
    onConfirmButtonClick:()->Unit
) {
    if(isOpen)
    {
        AlertDialog(
            onDismissRequest=onDismissRequest,
            title={ Text(text = title) },
            text={
                Column {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {


                    }
                    OutlinedTextField(
                        value = EventName,
                        onValueChange = onEventNameChange,
                        label = { Text(text = "Event Name") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                }
            },
            dismissButton={
                TextButton(onClick = onDismissRequest) {
                    Text(text = "Cancel")
                }
            },
            confirmButton={
                TextButton(onClick = onConfirmButtonClick) {
                    Text(text = "Save")
                }
            }
        )
    }


}