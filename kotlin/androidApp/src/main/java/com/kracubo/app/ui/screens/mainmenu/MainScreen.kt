package com.kracubo.app.ui.screens.mainmenu

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import com.kracubo.app.R
import com.kracubo.app.ui.customscrollbar.ColorType
import com.kracubo.app.ui.customscrollbar.ScrollbarConfig
import com.kracubo.app.ui.customscrollbar.rememberScrollbarState
import androidx.core.net.toUri
import com.kracubo.app.ui.customscrollbar.verticalScrollWithScrollbar

@Composable
fun MainScreen(onLocalScreen: () -> Unit,
               onRemoteScreen: () -> Unit) {
    val context = LocalContext.current
    val preferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val isFirstRun = preferences.getBoolean("is_first_run", true)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Top)
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.mipmap.dark_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(40.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Remote PyCharm",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        if(isFirstRun){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .padding(vertical = 24.dp)
            ) {
                // Custom scrollbar
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 30.dp, end = 30.dp, top = 10.dp, bottom = 10.dp)
                        .border(
                            width = 2.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(15.dp)
                        ).clip(RoundedCornerShape(15.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF323232))
                            .verticalScrollWithScrollbar(
                                rememberScrollState(),
                                rememberScrollbarState(),
                                scrollbarConfig = ScrollbarConfig(
                                    padding = PaddingValues(end = 12.dp, top = 5.dp, bottom = 5.dp),
                                    indicatorColor = ColorType.Solid(MaterialTheme.colorScheme.onSecondaryContainer),
                                    indicatorThickness = 16.dp,
                                    barThickness = 0.dp,
                                    indicatorPadding = PaddingValues(2.dp),
                                    barColor = ColorType.Solid(MaterialTheme.colorScheme.onSecondaryContainer)
                                )
                            ).padding(horizontal = 16.dp)
                    ) {
                        Text("MIT License")
                        Text("Copyright (c) 2025 k-racubo")
                        Text("Permission is hereby granted, free of charge, to any person obtaining a copy")
                        Text("of this software and associated documentation files (the ), to deal")
                        Text("in the Software without restriction, including without limitation the rights")
                        Text("to use, copy, modify, merge, publish, distribute, sublicense, and/or sell")
                        Text("copies of the Software, and to permit persons to whom the Software is")
                        Text("furnished to do so, subject to the following conditions:")

                        Text("The above copyright notice and this permission notice shall be included in all")
                        Text("copies or substantial portions of the Software.")

                        Text("THE SOFTWARE IS PROVIDED , WITHOUT WARRANTY OF ANY KIND, EXPRESS OR")
                        Text("IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,")
                        Text("FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE")
                        Text("AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER")
                        Text("LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,")
                        Text("OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE")
                        Text("SOFTWARE.")
                    }
                }
                LaunchedEffect(Unit) {
                    preferences.edit { putBoolean("is_first_run", false) }
                }
            }
        }else{ Spacer(Modifier.height(100.dp))}
        Text(
            text = "Choose a connection type",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
        )
        OutlinedButton(
            onClick = { onLocalScreen() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "Local connection")
        }
        OutlinedButton(
            onClick = { onRemoteScreen() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "Remote connection")
        }
        Spacer(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Contact Us",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                modifier = Modifier.clickable {
                    val intent = Intent(Intent.ACTION_VIEW, "https://t.me/Imchelovek09".toUri())
                    context.startActivity(intent)
                }
            )
            Text(
                text = "v1.0.0-Alpha",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
            )
        }
    }
}