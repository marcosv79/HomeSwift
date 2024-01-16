package pt.ipca.hs

import androidx.room.ColumnInfo

data class TopProvider(
    @ColumnInfo(name = "idProvider") val idProvider: Int,
    @ColumnInfo(name = "serviceCount") val serviceCount: Int
)
