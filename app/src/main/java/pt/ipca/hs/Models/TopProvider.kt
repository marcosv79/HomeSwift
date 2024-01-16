package pt.ipca.hs.Models

import androidx.room.ColumnInfo

data class TopProvider(
    @ColumnInfo(name = "idProvider") val idProvider: Int,
    @ColumnInfo(name = "serviceCount") val serviceCount: Int
)
