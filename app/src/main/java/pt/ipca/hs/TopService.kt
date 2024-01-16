package pt.ipca.hs

import androidx.room.ColumnInfo

data class TopService(
    @ColumnInfo(name = "service") val service: String,
    @ColumnInfo(name = "serviceCount") val serviceCount: Int
)
