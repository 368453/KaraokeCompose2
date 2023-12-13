package com.example.karaokecompose

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Config {
    var baseUrl: String = "https://api.musixmatch.com/ws/1.1/"
    var apiKey: String = "7cf84cc378407a04c43224ed074fed5d"
}

data class Entry(
    val track_id: Int,
    val track_name: String,
    val track_artist: String
) {
    override fun toString(): String {
        return "$track_artist - $track_name"
    }
}

interface GetRequestCallBackSongs {
    fun onSuccess(songs: List<Entry>)
    fun onFailure(error: String)
}

interface GetRequestCallBackLyrics {
    fun onSuccess(lyrics: String)
    fun onFailure(error: String)
}

fun buildRetrofit(): Retrofit{
    return Retrofit.Builder()
        .baseUrl(Config.baseUrl) // Replace with your API base URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun SendGetRequestSongs(call: Call<ApiResponse>, callback: GetRequestCallBackSongs){
    val songs = mutableListOf<Entry>()
    call.enqueue(object : retrofit2.Callback<ApiResponse> {
        override fun onResponse(
            call: Call<ApiResponse>,
            response: retrofit2.Response<ApiResponse>
        ) {
            if (response.isSuccessful) {
                val data = response.body()
                data?.let {
                    it.message.body.track_list.forEach { track ->
                        songs.add(
                            Entry(
                                track_id = track.track.track_id,
                                track_name = track.track.track_name,
                                track_artist = track.track.artist_name
                            )
                        )
                    }
                    callback.onSuccess(songs)
                } ?: run {
                    callback.onFailure("Nothing retrieved")
                }
            } else {
                callback.onFailure("API error: ${response.message()}")
            }
        }

        override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
            callback.onFailure("API error: ${t.message}")
        }
    })
}

fun SendGetRequestLyrics(call: Call<ApiResponse>, callback: GetRequestCallBackLyrics){
    call.enqueue(object : retrofit2.Callback<ApiResponse> {
        override fun onResponse(
            call: Call<ApiResponse>,
            response: retrofit2.Response<ApiResponse>
        ) {
            if (response.isSuccessful) {
                val data = response.body()
                data?.let {
                    callback.onSuccess(it.message.body.lyrics.lyrics_body)
                } ?: run {
                    callback.onFailure("Nothing retrieved")
                }
            } else {
                callback.onFailure("API error: ${response.message()}")
            }
        }

        override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
            callback.onFailure("API error: ${t.message}")
        }
    })
}

fun Top100List(callback: GetRequestCallBackSongs){
    val retrofit = buildRetrofit()
    var call: Call<ApiResponse>
    val api = retrofit.create(GetTop100::class.java)
    call = api.getData(
        chart_name = "top",
        page = 1,
        page_size = 100,
        has_lyrics = 1,
        apikey = Config.apiKey
    )
    SendGetRequestSongs(call, callback)
}

fun ArtistList(query: String, callback: GetRequestCallBackSongs){
    val retrofit = buildRetrofit()
    var call: Call<ApiResponse>
    val api = retrofit.create(SearchArtist::class.java)
    call = api.getData(
        q_artist = query,
        page = 1,
        page_size = 100,
        has_lyrics = 1,
        apikey = Config.apiKey
    )
    SendGetRequestSongs(call, callback)
}

fun SongList(query: String, callback: GetRequestCallBackSongs){
    val retrofit = buildRetrofit()
    var call: Call<ApiResponse>
    val api = retrofit.create(SearchSong::class.java)
    call = api.getData(
        q_track = query,
        page = 1,
        page_size = 100,
        has_lyrics = 1,
        apikey = Config.apiKey
    )
    SendGetRequestSongs(call, callback)
}

fun SongLyrics(id: Int, callback: GetRequestCallBackLyrics){
    val retrofit = buildRetrofit()
    var call: Call<ApiResponse>
    val api = retrofit.create(GetLyrics::class.java)
    call = api.getData(
        track_id = id,
        apikey = Config.apiKey
    )
    SendGetRequestLyrics(call, callback)
}