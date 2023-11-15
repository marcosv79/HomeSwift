package pt.ipca.hs

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ServiceDao {
    @Query("SELECT * FROM services")
    fun getAll(): List<Service>

    @Query("SELECT * FROM services WHERE id = :id")
    fun findById(id: Int): Service

    @Insert
    fun insertAll(vararg service: Service)

    @Delete
    fun delete(service: Service)

    @Update
    fun updateUser(vararg service: Service)
}