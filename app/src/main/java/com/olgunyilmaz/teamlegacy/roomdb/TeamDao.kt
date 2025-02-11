package com.olgunyilmaz.teamlegacy.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.olgunyilmaz.teamlegacy.model.Team
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface TeamDao {

    @Query("SELECT * FROM Team")
    fun fetchAll() : Flowable<List<Team>>

    @Insert
    fun insert (team : Team) : Completable

    @Delete
    fun delete(team : Team) : Completable

}