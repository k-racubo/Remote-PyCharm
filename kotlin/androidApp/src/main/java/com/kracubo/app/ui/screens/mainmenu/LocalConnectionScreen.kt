package com.kracubo.app.ui.screens.mainmenu

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kracubo.app.core.viewmodels.mainmenu.LocalServerSearchScreenViewModel
import com.kracubo.app.core.viewmodels.mainmenu.SearchState
import com.kracubo.app.ui.theme.ButtonBorderColor
import com.kracubo.app.ui.theme.ButtonTextColor
import com.kracubo.app.ui.theme.InputBorderColor
import com.kracubo.app.ui.theme.LabelColor
import com.kracubo.app.ui.theme.TextColor

@Composable
fun LocalConnectionScreen(exitToMainScreen: () -> Unit, toCodeEditor: () -> Unit) {
    val context = LocalContext.current

    var searchIP by remember { mutableStateOf("") }
    var searchPort by remember { mutableIntStateOf(0) }

    val viewModel: LocalServerSearchScreenViewModel = viewModel()

    @Composable
    fun splashSearching(searchStateText: String) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = {
                    exitToMainScreen()
                    viewModel.stopSearch()
                },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(30.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text(text = searchStateText)
                Spacer(modifier = Modifier.height(8.dp))
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(60.dp)
                )
            }
        }
    }

    @Composable
    fun manualSearching(isConnecting: Boolean){
        Box(modifier = Modifier
            .fillMaxSize()
            .background(if(isConnecting){Color.Black.copy(alpha = 0.5f)}else{Color.Transparent}),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 60.dp)
                    .blur(if(isConnecting){10.dp} else {0.dp}),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Local Connection",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(16.dp))
                OutlinedTextField(
                    value = searchIP,
                    onValueChange = { searchIP = it },
                    label = { Text("IP", color = LabelColor) },
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
                    enabled = !isConnecting,
                    modifier = Modifier.fillMaxWidth(0.75f),
                )
                Spacer(Modifier.height(20.dp))
                OutlinedTextField(
                    value = searchPort.toString(),
                    onValueChange = { newValue ->
                        val parsedPort = newValue.toIntOrNull() ?: 0
                        searchPort = parsedPort
                    },
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
                    enabled = !isConnecting,
                    modifier = Modifier.fillMaxWidth(0.75f),
                )
                Spacer(Modifier.height(20.dp))
                OutlinedButton(
                    onClick = { viewModel.startFullSearch(searchIP, searchPort) },
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ButtonBorderColor),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = ButtonTextColor
                    ),
                    enabled = !isConnecting
                ) {
                    Text(text = "Establish connection",
                        style = MaterialTheme.typography.titleLarge)
                }
            }
            if(isConnecting){
                Text(text = "Trying force connecting...",
                    modifier = Modifier.padding(bottom = 90.dp))
                CircularProgressIndicator(

                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(60.dp))
            }
            IconButton(
                onClick = {
                    viewModel.stopSearch()
                    exitToMainScreen()
                },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад",
                    modifier = Modifier.size(30.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    when (viewModel.searchState) {
        SearchState.MDNS_SEARCHING -> { splashSearching("mDNS service searching...") }
        SearchState.CACHE_SEARCHING -> { splashSearching("Trying cache data for connect...") }
        SearchState.MANUAL_INPUT -> {
            manualSearching(false)
        }
        SearchState.MANUAL_CONNECTING -> {
            manualSearching(true)
        }
        SearchState.QR_SEARCHING -> {}
        SearchState.FOUND -> {
            toCodeEditor()
        }
        SearchState.ERROR -> {
            exitToMainScreen()
            Toast.makeText(context,"Connection failed", Toast.LENGTH_LONG).show()
        }
    }
}
