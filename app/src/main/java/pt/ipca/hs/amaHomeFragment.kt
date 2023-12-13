package pt.ipca.hs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class amaHomeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var allReviews: List<Order> = emptyList()  // Adicionada a variável para armazenar as avaliações

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_ama_home, container, false)
        val name_tv = rootView.findViewById<TextView>(R.id.welcome_admin_tv)
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerViewAllReviews)

        val name = arguments?.getString("name")

        if (name != null) {
            name_tv.text = "Olá, $name"
        }

        loadAllReviews()

        val adapter = AllReviewsAdapter(allReviews) { order ->
            onDeleteReview(order)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return rootView
    }

    private fun loadAllReviews() {
        lifecycleScope.launch(Dispatchers.IO) {
            val orderDao = MyDatabase.invoke(requireContext()).orderDao()
            allReviews = orderDao.getReviewsWithComments()

            launch(Dispatchers.Main) {
                updateUIWithAllReviews(allReviews)
            }
        }
    }

    private fun updateUIWithAllReviews(orders: List<Order>) {
        val reviews = orders.filter { it.evalStar > 0 }

        updateUIWithReviews(reviews)
    }

    private fun updateUIWithReviews(reviews: List<Order>) {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerViewAllReviews)
        val noOrdersMessage = view?.findViewById<TextView>(R.id.noOrdersMessage)

        if(reviews.isEmpty()){
            noOrdersMessage?.visibility = View.VISIBLE
            recyclerView?.visibility = View.GONE
        }
        else{
            noOrdersMessage?.visibility = View.GONE
            recyclerView?.visibility = View.VISIBLE

            val adapter = AllReviewsAdapter(reviews) { order ->
                onDeleteReview(order)
            }

            recyclerView?.adapter = adapter
            recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        }


    }

    private fun onDeleteReview(order: Order) {
        lifecycleScope.launch(Dispatchers.IO){
            val orderDao = MyDatabase.invoke(requireContext()).orderDao()

            orderDao.updateEvalComment(order.id, "")

            loadAllReviews()
        }
    }
}
