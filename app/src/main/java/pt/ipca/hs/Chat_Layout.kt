package pt.ipca.hs

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Chat_Layout : AppCompatActivity() {
    private lateinit var editTextMessage: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var messageTextView: TextView
    private lateinit var scrollView: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_layout)

        editTextMessage = findViewById(R.id.editTextMessage)
        sendButton = findViewById(R.id.sendButton)
        messageTextView = findViewById(R.id.messageTextView)
        scrollView = findViewById(R.id.scrollView)

        sendButton.setOnClickListener {
            val messageText = editTextMessage.text.toString()

            if (messageText.isNotBlank()) {
                // Adiciona a nova mensagem no início do texto
                messageTextView.text = "Você: $messageText\n" + messageTextView.text

                // Limpa o texto após o envio
                editTextMessage.text.clear()

                // Rola automaticamente para a última mensagem
                scrollView.post {
                    scrollView.fullScroll(ScrollView.FOCUS_UP)
                }
            } else {
                showToast("Digite uma mensagem antes de enviar.")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}