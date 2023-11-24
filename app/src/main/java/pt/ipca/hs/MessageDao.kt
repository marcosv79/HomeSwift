package pt.ipca.hs

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
@Dao
interface MessageDao {

    @Query("SELECT * FROM messages WHERE (sender_id = :senderId AND receiver_id = :receiverId) OR (sender_id = :receiverId AND receiver_id = :senderId)")
    fun getMessages(senderId: String, receiverId: String): List<Message>

    @Insert
    fun insert(message: Message)

    @Query("SELECT * FROM messages WHERE receiver_id = :receiverId")
    suspend fun getMessagesForReceiver(receiverId: Int): List<Message>

    @Query("SELECT * FROM messages")
    fun getAllMessages(): List<Message>
    @Query("SELECT * FROM users WHERE name = :username LIMIT 1")
    fun getLoggedInUserByUsername(username: String): User?

    @Query("SELECT * FROM messages WHERE receiver_id = :providerId")
    suspend fun getMessagesForProvider(providerId: Int): List<Message>

    @Query("SELECT * FROM messages WHERE id = :id")
    fun findMessageById(id: Long): Message
    @Query("SELECT * FROM messages WHERE receiver_id = :receiverId")
    suspend fun getMessagesForUser(receiverId: Int): List<Message>

    @Delete
    fun delete(message: Message)

    @Update
    fun updateMessage(vararg message: Message)
    @Insert
    fun insertMessage(message: Message)
}
