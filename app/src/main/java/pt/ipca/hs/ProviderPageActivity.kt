package pt.ipca.hs


import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProviderPageActivity : AppCompatActivity() {

    private var selectedProvider: User? = null
    private lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_provider_page)

        val userId = intent.getIntExtra("idC", 0)
        val providerId = intent.getIntExtra("id", 0)
        val currentUserId = intent.getIntExtra("currentUserId", -1)

        Log.d("ProviderPageActivity", "Provider ID: $providerId, User ID: $userId")

        lifecycleScope.launch(Dispatchers.IO) {
            val userDao = MyDatabase.invoke(applicationContext).userDao()

            selectedProvider = userDao.findById(providerId)


            // Fetch reviews for the specific provider
            loadReviewsForProvider(providerId)

            launch(Dispatchers.Main) {
                updateUI(selectedProvider)
            }
        }

        val messagesButton = findViewById<ImageButton>(R.id.messagesbutamc)
        messagesButton.setOnClickListener {
            val intent = Intent(this, Chat_Layout::class.java)
            intent.putExtra("providerId", providerId)
            intent.putExtra("userId", userId)
            intent.putExtra("CurrentUserId", currentUserId)
            startActivity(intent)
        }

        val btnPlaceOrder = findViewById<Button>(R.id.btnCreateOrder)
        btnPlaceOrder.setOnClickListener {
            val intent = Intent(this, PlaceOrderActivity::class.java)
            intent.putExtra("name", selectedProvider?.name)
            intent.putExtra("service", selectedProvider?.service)
            intent.putExtra("cost", selectedProvider?.cost)
            intent.putExtra("idProvider", selectedProvider?.id ?: 0)
            intent.putExtra("id", userId)
            startActivity(intent)
        }
    }

    private fun updateUI(selectedProvider: User?) {
        val providerNameTextView = findViewById<TextView>(R.id.providerNameTextView)
        val providerLocationTextView = findViewById<TextView>(R.id.providerLocationTextView)
        val providerServiceTextView = findViewById<TextView>(R.id.providerServiceTextView)
        val providerCostTextView = findViewById<TextView>(R.id.providerCostTextView)

        if (selectedProvider != null) {
            val providerName = selectedProvider.name
            val providerAddress = selectedProvider.location
            val providerService = selectedProvider.service
            val providerCost = selectedProvider.cost

            providerNameTextView.text = providerName
            providerLocationTextView.text = providerAddress
            providerServiceTextView.text = providerService
            providerCostTextView.text = "$providerCost €"
        } else {
            providerNameTextView.text = "Fornecedor não encontrado"
            providerLocationTextView.text = ""
            providerServiceTextView.text = ""
            providerCostTextView.text = ""
        }
    }

    private fun loadReviewsForProvider(providerId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val userDao = MyDatabase.invoke(applicationContext).userDao()
            val users = userDao.getAll()

            val orderDao = MyDatabase.invoke(applicationContext).orderDao()
            val orders = orderDao.getOrdersByProviderId(providerId)

            launch(Dispatchers.Main) {
                Log.d(
                    "loadReviewsForProvider",
                    "Number of orders for provider $providerId: ${orders.size}"
                )

                // Print the details of each order for debugging
                for (order in orders) {
                    Log.d("loadReviewsForProvider", "Order details: $order")
                }

                updateUIWithReviews(orders, users, providerId)
            }
        }
    }

    private fun updateUIWithReviews(orders: List<Order>, users: List<User>, providerId: Int) {
        // Process orders to extract evalComment and evalStar
        val reviews = orders.filter { it.idProvider == providerId && it.evalStar > 0 }


        // Update UI with reviews
        updateUIWithReviews(reviews)
    }

    private fun updateUIWithReviews(orders: List<Order>) {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewReviews)

        lifecycleScope.launch(Dispatchers.IO) {
            val userDao = MyDatabase.invoke(applicationContext).userDao()
            val users = userDao.getAll()

            launch(Dispatchers.Main) {
                val adapter = ReviewAdapter(orders, users)
                recyclerView.adapter = adapter

                val layoutManager = LinearLayoutManager(this@ProviderPageActivity, LinearLayoutManager.VERTICAL, false)
                recyclerView.layoutManager = layoutManager

                val spacingInPixels =
                    resources.getDimensionPixelSize(R.dimen.spacing)
                recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        outRect.bottom = spacingInPixels
                    }
                })
            }
        }
    }
}