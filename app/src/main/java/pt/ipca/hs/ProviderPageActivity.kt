package pt.ipca.hs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
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
    }

    private fun updateUI(selectedProvider: User?) {
        val providerTextView = findViewById<TextView>(R.id.providerTextView)

        if (selectedProvider != null) {
            val providerName = selectedProvider.name
            providerTextView.text = "Página do $providerName"
        } else {
            providerTextView.text = "Fornecedor não encontrado"
        }
    }
}
