package com.olgunyilmaz.teamlegacy.model

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Team (

    @ColumnInfo("name")
    var name : String,

    @ColumnInfo("about")
    var about : String,

    @ColumnInfo("year")
    var year : Double,

    @ColumnInfo("image")
    var image : ByteArray


) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id = 0

}