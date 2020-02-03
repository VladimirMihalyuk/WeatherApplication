package com.example.weatherapplication.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Maybe

@Dao
interface DatabaseDAO {
    @Insert
    fun insertToday(today: Today): Completable

    @Query("SELECT * FROM Today")
    fun getToday():Maybe<List<Today>>

    @Query("DELETE FROM Today")
    fun deleteToday(): Completable
}