package com.example.karaokecompose

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

data class ApiResponse(
    val message: Message
)

data class Message(
    val body: Body
)

data class Body(
    val track_list: List<TrackItem>,
    val lyrics: Lyrics
)

data class LyricsItem(
    val lyrics: Lyrics
)

data class Lyrics(
    val lyrics_id: Int,
    val lyrics_body: String
)

data class TrackItem(
    val track: Track
)

data class Track(
    val track_id: Int,
    val track_name: String,
    val album_id: Int,
    val album_name: String,
    val artist_id: Int,
    val artist_name: String
)

// Define your Retrofit interface
interface GetTop100 {
    @GET("chart.tracks.get")
    fun getData(
        @Query("chart_name") chart_name: String, // top
        @Query("page") page: Int,                // 1
        @Query("page_size") page_size: Int,      // 100
        @Query("has_lyrics") has_lyrics: Int,    // 1
        @Query("apikey") apikey: String          // 240a71d7809755aff6ea17de6ec999db
    ): Call<ApiResponse>
}

interface GetLyrics {
    @GET("track.lyrics.get")
    fun getData(
        @Query("track_id") track_id: Int, // 31409936
        @Query("apikey") apikey: String   // 240a71d7809755aff6ea17de6ec999db
    ): Call<ApiResponse>
}

interface SearchArtist {
    @GET("track.search")
    fun getData(
        @Query("q_artist") q_artist: String,  // rick astley
        @Query("page") page: Int,             // 1
        @Query("page_size") page_size: Int,   // 100
        @Query("has_lyrics") has_lyrics: Int, // 1
        @Query("apikey") apikey: String       // 240a71d7809755aff6ea17de6ec999db
    ): Call <ApiResponse>
}

interface SearchSong {
    @GET("track.search")
    fun getData(
        @Query("q_track") q_track: String,    // never gonna give you up
        @Query("page") page: Int,             // 1
        @Query("page_size") page_size: Int,   // 100
        @Query("has_lyrics") has_lyrics: Int, // 1
        @Query("apikey") apikey: String       // 240a71d7809755aff6ea17de6ec999db
    ): Call <ApiResponse>
}