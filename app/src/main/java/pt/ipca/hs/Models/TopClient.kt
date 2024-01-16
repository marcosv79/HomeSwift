package pt.ipca.hs.Models

import androidx.room.ColumnInfo

data class TopClient(
    @ColumnInfo(name = "idClient") val idClient: Int,
    @ColumnInfo(name = "serviceCount") val serviceCount: Int
)