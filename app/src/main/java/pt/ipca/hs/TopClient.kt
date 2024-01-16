package pt.ipca.hs

import androidx.room.ColumnInfo

data class TopClient(
    @ColumnInfo(name = "idClient") val idClient: Int,
    @ColumnInfo(name = "serviceCount") val serviceCount: Int
)