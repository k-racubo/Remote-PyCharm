package com.kracubo.app.ui.screens.codeeditor.projectsslist

import android.content.Intent
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kracubo.app.R
import com.kracubo.app.core.extensions.OnDisconnectEffect
import com.kracubo.app.core.viewmodels.codeditor.ProjectsListViewModel
import com.kracubo.app.ui.customscrollbar.ColorType
import com.kracubo.app.ui.customscrollbar.ScrollbarConfig
import com.kracubo.app.ui.customscrollbar.rememberScrollbarState
import androidx.core.net.toUri
import com.kracubo.app.ui.customscrollbar.verticalScrollWithScrollbar

@Composable
fun ProjectsList(toCodeEditor: () -> Unit, toMainMenu: () -> Unit) {
    val context = LocalContext.current

    val viewModel: ProjectsListViewModel = viewModel()

    viewModel.OnDisconnectEffect(toMainMenu)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.mipmap.dark_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(40.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "Remote Pycharm",
                color = Color.LightGray,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "Recent projects",
            color = Color.Gray,
            fontSize = 20.sp
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(475.dp)
                .padding(vertical = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .border(
                        width = 2.dp,
                        color = Color.LightGray,
                        shape = RoundedCornerShape(15.dp)
                    )
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color(0xFF323232))
                    .padding(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScrollWithScrollbar(
                            rememberScrollState(),
                            rememberScrollbarState(),
                            scrollbarConfig = ScrollbarConfig(
                                padding = PaddingValues(end = 7.dp, top = 5.dp, bottom = 5.dp),
                                indicatorColor = ColorType.Solid(MaterialTheme.colorScheme.onSecondaryContainer),
                                indicatorThickness = 16.dp,
                                barThickness = 0.dp,
                                indicatorPadding = PaddingValues(2.dp),
                                barColor = ColorType.Solid(MaterialTheme.colorScheme.onSecondaryContainer)
                            ))
                ) {
                    viewModel.projects.forEach { project ->
                        ProjectItem(project = project, onClick = { project ->
                            viewModel.closeCurrentProject()
                            viewModel.openProject(project.name, project.path)
                            toCodeEditor()
                        })
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedButton(
            onClick = { toMainMenu() },
            modifier = Modifier.fillMaxWidth(0.65f)
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "Disconnect from server")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Contact Us",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW, "https://t.me/k_racubo".toUri())
                        context.startActivity(intent)
                    },
                )
                Text(
                    text = "v1.0.0-Alpha",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                )
            }
        }

    }
}

