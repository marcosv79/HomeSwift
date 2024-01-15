package pt.ipca.hs

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "idProvider") val idProvider: Int = 0,
    @ColumnInfo(name = "idClient") val idClient: Int = 0,
    @ColumnInfo(name = "service") val service: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "hour") val hour: String,
    @ColumnInfo(name = "typeService") val typeService: String,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "cost") val cost: String,
    @ColumnInfo(name = "evalStar") val evalStar: Int = 0,
    @ColumnInfo(name = "evalComment") val evalComment: String,
)
