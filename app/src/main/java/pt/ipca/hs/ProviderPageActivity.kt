package pt.ipca.hs

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProviderPageActivity : AppCompatActivity() {

    private var selectedProvider: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_provider_page)

        val providerId = intent.getIntExtra("id", 0)
        val userId = intent.getIntExtra("userId", 0)
        Log.d("ProviderPageActivity", "Provider recebido: $providerId")
        Log.d("ProviderPageActivity", "User recebido: $userId")
        lifecycleScope.launch(Dispatchers.IO) {
            val userDao = MyDatabase.invoke(applicationContext).userDao()

            selectedProvider = userDao.findById(providerId)

            launch(Dispatchers.Main) {
                updateUI(selectedProvider)
            }
        }

        val messagesButton = findViewById<ImageButton>(R.id.messagesbutamc)
        messagesButton.setOnClickListener {
            val intent = Intent(this, Chat_Layout::class.java)
            intent.putExtra("providerId", providerId)
            intent.putExtra("userId", userId) // Pass the userId to Chat_Layout
            startActivity(intent)
        }

        val btnPlaceOrder = findViewById<Button>(R.id.btnCreateOrder)
        btnPlaceOrder.setOnClickListener{
            val intent = Intent(this, PlaceOrderActivity::class.java)

            intent.putExtra("name", selectedProvider?.name)
            intent.putExtra("service", selectedProvider?.service)
            intent.putExtra("cost", selectedProvider?.cost)
            startActivity(intent)
        }
    }

    private fun updateUI(selectedProvider: User?) {
        val providerImageView = findViewById<ImageView>(R.id.providerImageView)
        val providerNameTextView = findViewById<TextView>(R.id.providerNameTextView)
        val providerLocationTextView = findViewById<TextView>(R.id.providerLocationTextView)
        val providerServiceTextView = findViewById<TextView>(R.id.providerServiceTextView)
        val providerCostTextView = findViewById<TextView>(R.id.providerCostTextView)

        if (selectedProvider != null) {
            val providerName = selectedProvider.name
            val providerAddress = selectedProvider.location
            val providerService = selectedProvider.service
            val providerCost = selectedProvider.cost

            providerImageView.setImageResource(R.drawable.baseline_person_24)
            providerNameTextView.text = providerName
            providerLocationTextView.text = providerAddress
            providerServiceTextView.text = providerService
            providerCostTextView.text = "$providerCost €"
        } else {
            providerImageView.setImageResource(R.drawable.baseline_person_24)
            providerNameTextView.text = "Fornecedor não encontrado"
            providerLocationTextView.text = ""
            providerServiceTextView.text = ""
            providerCostTextView.text = ""
        }
    }
}
