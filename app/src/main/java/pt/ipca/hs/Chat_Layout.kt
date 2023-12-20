package pt.ipca.hs

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Chat_Layout : AppCompatActivity() {
    private lateinit var editTextMessage: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var messageTextView: LinearLayout
    private lateinit var scrollView: ScrollView
    private lateinit var backButton: ImageButton
    private lateinit var providerNameTextView: TextView
    private lateinit var myDatabase: MyDatabase
    private lateinit var messageDao: MessageDao

    private var userId: Int = 0
    private var providerId: Int = 0
    private var providerName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_layout)

        providerId = intent.getIntExtra("providerId", 0)
        userId = intent.getIntExtra("userId", 0)
        providerName = intent.getStringExtra("providerName")

        myDatabase = MyDatabase(this)
        messageDao = myDatabase.messageDao()

        editTextMessage = findViewById(R.id.editTextMessage)
        sendButton = findViewById(R.id.sendButton)
        messageTextView = findViewById(R.id.chatLayout)
        scrollView = findViewById(R.id.scrollView)
        backButton = findViewById(R.id.backButton)
        providerNameTextView = findViewById(R.id.providerName)

        backButton.setOnClickListener {
            onBackPressed()
        }

        sendButton.setOnClickListener {
            val messageText = editTextMessage.text.toString()

            if (messageText.isNotBlank()) {
                val messageView = formatMessage(
                    Message(
                        senderId = userId.toString(),
                        receiverId = providerId.toString(),
                        message = messageText
                    )
                )

                addMessageView(messageView)

                editTextMessage.text.clear()

                scrollView.post {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                }

                // Salva a mensagem no banco de dados
                lifecycleScope.launch(Dispatchers.IO) {
                    messageDao.insertMessage(
                        Message(
                            senderId = userId.toString(),
                            receiverId = providerId.toString(),
                            message = messageText
                        )
                    )
                }
            } else {
                Toast.makeText(this, "Escreva uma mensagem antes de enviar", Toast.LENGTH_SHORT).show()
            }
        }
        loadAndDisplayMessages(userId, providerId)
        updateActionBarTitle(userId)
    }

    private fun loadAndDisplayMessages(currentUserId: Int, currentProviderId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val messages = if (currentUserId < currentProviderId) {
                messageDao.getMessages(currentUserId.toString(), currentProviderId.toString())
            } else {
                messageDao.getMessages(currentProviderId.toString(), currentUserId.toString())
            }.toList()

            launch(Dispatchers.Main) {
                for (message in messages) {
                    val messageView = formatMessage(message)
                    addMessageView(messageView)
                }

                updateActionBarTitle(userId)

                scrollView.post {
                    scrollView.fullScroll(ScrollView.FOCUS_UP)
                }
            }
        }
    }


    private fun updateActionBarTitle(userId: Int) {
        lifecycleScope.launch {
            val user = getUserName(userId)

            supportActionBar?.let {
                it.title = user?.name ?: "Nome do Utilizador"
            }

            providerNameTextView.text = user?.name ?: "Nome do Fornecedor"
        }
    }

    private fun formatMessage(message: Message): View {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout: View

        if (message.senderId == userId.toString()) {
            layout = inflater.inflate(R.layout.sent_message_layout, null)
            val sentMessageTextView = layout.findViewById<TextView>(R.id.sentMessageTextView)
            sentMessageTextView.text = message.message
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.gravity = Gravity.END
            layout.layoutParams = params
        } else {
            layout = inflater.inflate(R.layout.received_message_layout, null)
            val receivedMessageTextView = layout.findViewById<TextView>(R.id.receivedMessageTextView)
            receivedMessageTextView.text = message.message
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.gravity = Gravity.START
            layout.layoutParams = params
        }

        return layout
    }

    private suspend fun getUserName(userId: Int): User? {
        return withContext(Dispatchers.IO) {
            val userDao = myDatabase.userDao()

            try {
                val user = userDao.getUserById(userId)
                return@withContext user
            } catch (e: Exception) {
                return@withContext null
            }
        }
    }

    private fun addMessageView(view: View) {
        messageTextView.addView(view)

        val spaceView = View(this)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            resources.getDimensionPixelSize(R.dimen.message_space_height)
        )
        messageTextView.addView(spaceView, params)
    }
}