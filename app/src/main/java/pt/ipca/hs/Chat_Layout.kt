package pt.ipca.hs

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Chat_Layout : AppCompatActivity() {
    private lateinit var editTextMessage: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var messageTextView: TextView
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


        // Recupera o ID do fornecedor e o nome da Intent
        // Recuperar valores do Intent
        providerId = intent.getIntExtra("providerId", 0)
        userId = intent.getIntExtra("userId", 0)
        providerName = intent.getStringExtra("providerName")

        // Agora você pode usar os valores conforme necessário
        Log.d("Chat_Layout", "Provider ID: $providerId, User ID: $userId, Provider Name: $providerName")

        // Initialize the database instance
        myDatabase = MyDatabase(this)
        // Initialize the MessageDao
        messageDao = myDatabase.messageDao()


        val providerName = intent.getStringExtra("providerName") ?: "Nome do Fornecedor"
        // Inicializa as Views
        editTextMessage = findViewById(R.id.editTextMessage)
        sendButton = findViewById(R.id.sendButton)
        messageTextView = findViewById(R.id.messageTextView)
        scrollView = findViewById(R.id.scrollView)
        backButton = findViewById(R.id.backButton)
        providerNameTextView = findViewById(R.id.providerName)

        // Configura o nome do fornecedor na barra superior
        providerNameTextView.text = providerName

        // Configura o clique do botão de voltar
        backButton.setOnClickListener {
            onBackPressed()
        }

        sendButton.setOnClickListener {
            val messageText = editTextMessage.text.toString()

            if (messageText.isNotBlank()) {
                // Adiciona a nova mensagem no início do texto
                messageTextView.text =
                    "Você para o fornecedor $providerId: $messageText\n" + messageTextView.text
                // Limpa o texto após o envio
                editTextMessage.text.clear()

                // Rola automaticamente para a última mensagem
                scrollView.post {
                    scrollView.fullScroll(ScrollView.FOCUS_UP)
                }

                // Save the message to the database
                val message = Message(
                    senderId = userId.toString(),
                    receiverId = providerId.toString(),
                    message = messageText
                )
                lifecycleScope.launch(Dispatchers.IO) {
                    messageDao.insertMessage(message)
                }
            } else {
                showToast("Digite uma mensagem antes de enviar.")
            }
        }
        loadAndDisplayMessages(userId, providerId)
    }

    private fun loadAndDisplayMessages(currentUserId: Int, currentProviderId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            // Consulta no banco de dados para obter as mensagens relevantes
            val messages = if (currentUserId < currentProviderId) {
                messageDao.getMessages(currentUserId.toString(), currentProviderId.toString())
            } else {
                messageDao.getMessages(currentProviderId.toString(), currentUserId.toString())
            }.toList() // Ordena as mensagens por timestamp

            // Atualiza a UI no thread principal
            launch(Dispatchers.Main) {
                // Limpa o texto atual da ScrollView
                messageTextView.text = ""


                // Adiciona as mensagens recuperadas à ScrollView
                for (message in messages) {
                    val formattedMessage =
                        "Usuario ${message.senderId} para o fornecedor ${message.receiverId}: ${message.message}\n"
                    messageTextView.append(formattedMessage)
                }

                // Rola automaticamente para a última mensagem
                scrollView.post {
                    scrollView.fullScroll(ScrollView.FOCUS_UP)
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
