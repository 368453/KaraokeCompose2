package com.example.karaoke.db.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LyricDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Lyric)

    @Update
    suspend fun update(item: Lyric)

    @Delete
    suspend fun delete(item: Lyric)

    @Query("DELETE FROM lyrics WHERE id = :id")
    fun removeItem(id: Int): Int

    @Query("SELECT * FROM lyrics WHERE id = :id")
    fun getItem(id: Int): Flow<Lyric>

    @Query("SELECT * FROM lyrics")
    fun getAllItems(): Flow<List<Lyric>>

    @Query("SELECT * FROM lyrics WHERE songArtist = :artist")
    fun getByArtist(artist: String): Flow<List<Lyric>>

    @Query("DELETE FROM lyrics")
    fun wipe(): Int
}
