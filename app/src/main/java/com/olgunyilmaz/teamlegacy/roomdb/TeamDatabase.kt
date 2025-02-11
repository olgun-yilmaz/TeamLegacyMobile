package com.olgunyilmaz.teamlegacy.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.olgunyilmaz.teamlegacy.model.Team

@Database(entities = arrayOf(Team :: class), version = 1)
abstract class TeamDatabase : RoomDatabase() {
    abstract fun teamDao() : TeamDao
}