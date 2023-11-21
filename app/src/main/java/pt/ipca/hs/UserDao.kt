package pt.ipca.hs

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    fun getUserByEmailAndPassword(email: String, password: String): User?

    @Query("SELECT * FROM users")
    fun getAll(): List<User>

    @Query("SELECT * FROM users WHERE id = :id")
    fun findById(id: Int): User

    @Query("SELECT * FROM users WHERE name = :name")
    fun findByName(name: String): User?

    @Query("SELECT * FROM users WHERE email = :email")
    fun findByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE userType = 'Cliente' ")
    fun getUsersClient(): List<User>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: kotlin.Int): User?

    @Query("SELECT * FROM users WHERE userType = 'Fornecedor' ")
    fun getUsersProvider(): List<User>

    @Query("SELECT * FROM users WHERE userType = 'Fornecedor' AND service = :selectedService")
    fun getUsersByService(selectedService: String): List<User>

    @Insert
    fun insertAll(vararg user: User)

    @Delete
    fun delete(user: User)

    @Update
    fun updateUser(vararg user: User)
}