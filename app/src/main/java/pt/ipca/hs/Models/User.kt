package pt.ipca.hs.Models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "password") var password: String,
    @ColumnInfo(name = "userType") val userType: String,
    @ColumnInfo(name = "address") var address: String = "",
    @ColumnInfo(name = "location") var location: String = "",
    @ColumnInfo(name = "service") var service: String = "",
    @ColumnInfo(name = "cost") var cost: String = ""
    )
