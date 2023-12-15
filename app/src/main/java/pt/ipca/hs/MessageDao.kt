package pt.ipca.hs

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import pt.ipca.hs.MensagensGroup

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

    @Query("SELECT * FROM messages WHERE receiver_id = :userId OR sender_id = :userId Group by sender_id,receiver_id")
    suspend fun getUserMessages(userId: Int?): List<Message>

    @Query("SELECT ROWID as id, sender_id AS sender, receiver_id AS receiver, message_text AS messages FROM messages WHERE (sender_id = :userId OR receiver_id = :userId) AND ROWID IN (SELECT MAX(ROWID) FROM messages WHERE sender_id = :userId OR receiver_id = :userId GROUP BY CASE WHEN sender_id < receiver_id THEN sender_id ELSE receiver_id END, CASE WHEN sender_id < receiver_id THEN receiver_id ELSE sender_id END);")
    suspend fun getuserMensagens(userId: Int?): List<MensagensGroup>

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
