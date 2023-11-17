package pt.ipca.hs

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProviderPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_provider_page)

        val providerId = intent.getIntExtra("id", 0)

        lifecycleScope.launch(Dispatchers.IO) {
            val userDao = MyDatabase.invoke(applicationContext).userDao()

            val selectedProvider = userDao.findById(providerId)

            launch(Dispatchers.Main) {
                updateUI(selectedProvider)
            }
        }

        val messagesButton = findViewById<ImageButton>(R.id.messagesbutamc)
        messagesButton.setOnClickListener {
            // Ao clicar no botão, iniciar outra atividade ou fragmento
            // Exemplo de iniciar outra atividade:
            val intent = Intent(this, Chat_Layout::class.java)
            startActivity(intent)

            // Ou exemplo de iniciar um fragmento:
            // val fragment = NovoFragmento()
            // supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
        }
    }

    private fun updateUI(selectedProvider: User?) {
        val providerImageView = findViewById<ImageView>(R.id.providerImageView)
        val providerNameTextView = findViewById<TextView>(R.id.providerNameTextView)
        val providerAddressTextView = findViewById<TextView>(R.id.providerAddressTextView)

        if (selectedProvider != null) {
            val providerName = selectedProvider.name
            val providerAddress = selectedProvider.address

            // Substitua ic_default_provider_icon pelo recurso real da imagem
            providerImageView.setImageResource(R.drawable.baseline_person_24)
            providerNameTextView.text = providerName
            providerAddressTextView.text = providerAddress
        } else {
            // Se o fornecedor não for encontrado, você pode exibir uma mensagem apropriada
            providerImageView.setImageResource(R.drawable.baseline_person_24)
            providerNameTextView.text = "Fornecedor não encontrado"
            providerAddressTextView.text = ""
        }
    }
}
