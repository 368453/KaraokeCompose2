package com.example.karaokecompose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

val Roboto = FontFamily(
    Font(R.font.roboto_regular, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.roboto_bold, FontWeight.Bold, FontStyle.Normal)
)

@Composable
fun MyTheme(content: @Composable () -> Unit){
    MaterialTheme(
        colors = lightColors(
            primary = Color.DarkGray,
        ),
        typography = Typography(
            defaultFontFamily = Roboto
        ),
        content = content
    )
}

@Composable
fun TitleBar(text: String, back: Boolean, navController: NavController) {
    MyTheme {
        TopAppBar(
            title = {
                Text(
                    text = text,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.h6
                )
            },
            navigationIcon = if (back) {
                {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            } else {
                null
            },
            elevation = 4.dp,
        )
    }
}

@Composable
fun MainMenu(items: List<String>, navController: NavController){
    MyTheme {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(items) { item ->
                MenuCard(item = item) {
                    val index = items.indexOf(item)
                    navController.navigate("SongMenu/${index}/${item}")
                }
            }
        }
    }
}

@Composable
fun SongMenu(items: List<Entry>, navController: NavController){
    MyTheme {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(items) { item ->
                SongCard(item = item) {
                    val id = item.track_id
                    val artist = item.track_artist
                    val name = item.track_name
                    navController.navigate("SongLyric/${id}/${artist}/${name}")
                }
            }
        }
    }
}

@Composable
fun MenuCard(item: String, onItemClick: () -> Unit){
    MyTheme {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { onItemClick() }, // Handle click events here
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = item,
                    style = MaterialTheme.typography.h6
                )
            }
        }
    }
}

@Composable
fun SongCard(item: Entry, onItemClick: () -> Unit){
    MyTheme {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { onItemClick() }, // Handle click events here
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = item.toString(),
                    style = MaterialTheme.typography.h6
                )
            }
        }
    }
}

@Composable
fun FullscreenTextBox(lyrics: String) {
    MyTheme {
        TextField(
            value = lyrics,
            onValueChange = {},
            modifier = Modifier
                .heightIn(max = 600.dp)
                .padding(16.dp),
            textStyle = TextStyle(
                fontSize = MaterialTheme.typography.h6.fontSize
            ),
            maxLines = Int.MAX_VALUE,
            singleLine = false,
            readOnly = true
        )
    }
}

@Composable
fun DownloadButton(downloaded: Boolean, onClick: () -> Unit) {
    MyTheme {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(if (downloaded) "Delete" else "Download")
        }
    }
}

@Composable
fun QueryBox(type: String, navController: NavController, onTextEntered: (String) -> Unit) {
    MyTheme {
        var textValue by remember { mutableStateOf("") }
        var visible by remember { mutableStateOf(true) }

        if (visible) {
            AlertDialog(
                onDismissRequest = {
                    // Handle dismissal if needed
                },
                title = {
                    Text(
                        text = "Enter ${type}"
                    )
                },
                text = {
                    TextField(
                        value = textValue,
                        onValueChange = {
                            textValue = it
                        },
                        label = {
                            Text("Text input")
                        }
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        visible = false
                        onTextEntered(textValue)
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        visible = false
                        navController.popBackStack()
                    }) {
                        Text(
                            text = "Cancel"
                        )
                    }
                }
            )
        }
    }
}

