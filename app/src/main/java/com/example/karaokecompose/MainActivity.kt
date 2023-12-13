package com.example.karaokecompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.karaoke.db.database.Lyric
import com.example.karaoke.db.database.LyricDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var db : LyricDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "MainMenu"){
                    composable("MainMenu"){
                        val items = listOf("Downloaded Lyrics", "Top 100 Charts", "Search by Artist", "Search by Song")
                        Column {
                            TitleBar("Karaoke App", false, navController)
                            MainMenu(items, navController)
                        }
                    }
                    composable("SongMenu/{index}/{item}"){ backStackEntry ->
                        val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: -1
                        val item = backStackEntry.arguments?.getString("item").toString()
                        val (songs, setSongs) = remember { mutableStateOf<List<Entry>>(emptyList()) }

                        // downloaded lyrics
                        if (index == 0) {
                            db = Room
                                .databaseBuilder(applicationContext, LyricDatabase::class.java, "lyric_database")
                                .fallbackToDestructiveMigration()
                                .build()
                            val downloadedSongs = mutableListOf<Entry>()
                            LaunchedEffect(id) {
                                db.lyricDao().getAllItems().collect { downloadedSong ->
                                    for (song in downloadedSong) {
                                        downloadedSongs.add(
                                            Entry(
                                                track_id = song.id,
                                                track_name = song.songName,
                                                track_artist = song.songArtist
                                            )
                                        )
                                    }
                                    setSongs(downloadedSongs)
                                }
                            }
                        }

                        // top 100 charts
                        if (index == 1) {
                            Top100List(object : GetRequestCallBackSongs {
                                override fun onSuccess(songs: List<Entry>) {
                                    setSongs(songs)
                                }
                                override fun onFailure(error: String) {
                                    // do something with the error idk
                                }
                            })
                        }

                        // search by artist
                        else if (index == 2) {
                            QueryBox("artist", navController){
                                ArtistList(it, object : GetRequestCallBackSongs {
                                    override fun onSuccess(songs: List<Entry>) {
                                        setSongs(songs)
                                    }
                                    override fun onFailure(error: String) {
                                        // do something with the error idk
                                    }
                                })
                            }
                        }

                        // search by song
                        else if (index == 3) {
                            QueryBox("song", navController){
                                SongList(it, object : GetRequestCallBackSongs {
                                    override fun onSuccess(songs: List<Entry>) {
                                        setSongs(songs)
                                    }
                                    override fun onFailure(error: String) {
                                        // do something with the error idk
                                    }
                                })
                            }
                        }

                        Column {
                            TitleBar(item, true, navController)
                            SongMenu(songs, navController)
                        }
                    }
                    composable("SongLyric/{id}/{artist}/{name}"){ backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: -1
                        val artist = backStackEntry.arguments?.getString("artist").toString()
                        val name = backStackEntry.arguments?.getString("name").toString()
                        val (lyrics, setLyrics) = remember { mutableStateOf<String>("") }
                        val (downloaded, setDownloaded) = remember { mutableStateOf<Boolean>(false) }

                        db = Room
                            .databaseBuilder(applicationContext, LyricDatabase::class.java, "lyric_database")
                            .fallbackToDestructiveMigration()
                            .build()
                        LaunchedEffect(id) {
                            db.lyricDao().getItem(id).collect { dbLyrics ->
                                if (dbLyrics != null){
                                    setLyrics(dbLyrics.songLyric)
                                    setDownloaded(true)
                                }
                                else{
                                    SongLyrics(id, object : GetRequestCallBackLyrics {
                                        override fun onSuccess(lyrics: String) {
                                            setLyrics(lyrics)
                                        }
                                        override fun onFailure(error: String) {
                                            // do something with the error idk
                                        }
                                    })
                                }
                            }
                        }

                        Column {
                            TitleBar(name, true, navController)
                            FullscreenTextBox(lyrics)
                            DownloadButton(downloaded, onClick = {
                                lifecycleScope.launch(Dispatchers.IO) {
                                    if (!downloaded){
                                        db.lyricDao().insert(
                                            Lyric(
                                                id = id,
                                                songName = name,
                                                songArtist = artist,
                                                songLyric = lyrics
                                            )
                                        )
                                    }
                                    else{
                                        db.lyricDao().removeItem(id)
                                    }

                                }
                                setDownloaded(!downloaded)
                            })
                        }
                    }
                }
            }
        }
    }
}
