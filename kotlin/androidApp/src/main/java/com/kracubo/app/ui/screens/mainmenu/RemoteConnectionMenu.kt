package com.kracubo.app.ui.screens.mainmenu

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kracubo.app.ui.theme.ButtonBorderColor
import com.kracubo.app.ui.theme.ButtonTextColor
import com.kracubo.app.ui.theme.InputBorderColor
import com.kracubo.app.ui.theme.LabelColor
import com.kracubo.app.ui.theme.TextColor
import androidx.core.net.toUri

@Composable
fun RemoteScreen(
    connection: () -> Unit,
    exit: () -> Unit
) {
    val context = LocalContext.current

    var serverIp by remember { mutableStateOf("") }
    var serverPort by remember { mutableStateOf("") }
    var authToken by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        IconButton(
            onClick = { exit() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = ButtonBorderColor
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Remote connection",
                color = TextColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            OutlinedTextField(
                value = serverIp,
                onValueChange = { serverIp = it },
                label = { Text("Server IP", color = LabelColor) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = InputBorderColor,
                    unfocusedBorderColor = InputBorderColor,
                    focusedTextColor = TextColor,
                    unfocusedTextColor = TextColor,
                    cursorColor = TextColor,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth(0.75f)
            )
            OutlinedTextField(
                value = serverPort,
                onValueChange = { serverPort = it },
                label = { Text("Port", color = LabelColor) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = InputBorderColor,
                    unfocusedBorderColor = InputBorderColor,
                    focusedTextColor = TextColor,
                    unfocusedTextColor = TextColor,
                    cursorColor = TextColor,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth(0.75f)
            )
            OutlinedTextField(
                value = authToken,
                onValueChange = { authToken = it },
                label = { Text("Authorization token", color = LabelColor) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = InputBorderColor,
                    unfocusedBorderColor = InputBorderColor,
                    focusedTextColor = TextColor,
                    unfocusedTextColor = TextColor,
                    cursorColor = TextColor,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth(0.75f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { connection() },
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, ButtonBorderColor),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = ButtonTextColor
                )
            ) {
                Text(
                    text = "Establish connection",
                    fontSize = 16.sp
                )
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Contact Us",
                color = ButtonBorderColor,
                fontSize = 14.sp,
                modifier = Modifier.clickable {
                    val intent = Intent(Intent.ACTION_VIEW, "https://t.me/Dasxer2008".toUri())
                    context.startActivity(intent)
                },
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "v1.0.0-Alpha",
                color = LabelColor,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}