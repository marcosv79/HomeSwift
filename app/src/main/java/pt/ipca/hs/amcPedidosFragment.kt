package pt.ipca.hs

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
/**
 * A simple [Fragment] subclass.
 * Use the [amcPedidosFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class amcPedidosFragment : Fragment() {
    private lateinit var myDatabase: MyDatabase
    private lateinit var rootView: View

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        val idClient = arguments?.getInt("idC", 0)
        myDatabase = MyDatabase.invoke(requireContext())
        rootView = inflater.inflate(R.layout.fragment_amc_pedidos, container, false)

        val spinnerFilter = rootView?.findViewById<Spinner>(R.id.spinnerFilterOrderStatus)
        val statusFilterOptions = listOf("Todos", "Ativo", "Concluído")

        spinnerFilter?.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, statusFilterOptions)

        spinnerFilter?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long){
                val selectedStatus = statusFilterOptions[position]
                if (idClient != null) {
                    loadOrdersForClient(idClient,selectedStatus)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }


        idClient?.let { clientId ->
            loadOrdersForClient(clientId, "Todos")
        }

        return rootView
    }

    private fun loadOrdersForClient(clientId: Int, statusFilter: String) {
        rootView?.let { root ->
            lifecycleScope.launch(Dispatchers.IO) {
                val userDao = myDatabase.userDao()
                val users = userDao.getAll()

                val orderDao = myDatabase.orderDao()
                val orders =
                    if(statusFilter == "Todos"){
                        orderDao.getOrdersByClientId(clientId)
                    } else {
                        orderDao.getOrdersByClientAndStatus(clientId, statusFilter)
                    }

                launch(Dispatchers.Main) {
                    updateUIWithOrders(orders, users, clientId)
                }
            }
        }
    }

    private fun updateUIWithOrders(orders: List<Order>, users: List<User>, clientId: Int) {
        val recyclerView = rootView?.findViewById<RecyclerView>(R.id.recyclerViewPedidos)
        val noOrdersMessage = rootView?.findViewById<TextView>(R.id.noOrdersMessage)

        if(orders.isEmpty()){
            noOrdersMessage?.visibility = View.VISIBLE
            recyclerView?.visibility = View.GONE
        } else {
            noOrdersMessage?.visibility = View.GONE
            recyclerView?.visibility = View.VISIBLE
            recyclerView?.layoutManager = LinearLayoutManager(requireContext())
            recyclerView?.adapter = OrderAdapter(orders, users,
                { orderId ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        val orderDao = myDatabase.orderDao()
                        orderDao.deleteOrderById(orderId)

                        val updatedOrders = orderDao.getOrdersByClientId(clientId)
                        val userDao = myDatabase.userDao()
                        val user = userDao.getAll()

                        launch(Dispatchers.Main) {
                            updateUIWithOrders(updatedOrders, user, clientId)
                        }
                    }
                },
                { orderId ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        val orderDao = myDatabase.orderDao()
                        orderDao.updateOrderStatus(orderId, "Concluído")

                        val updatedOrders = orderDao.getOrdersByClientId(clientId)
                        val userDao = myDatabase.userDao()
                        val user = userDao.getAll()

                        launch(Dispatchers.Main) {
                            updateUIWithOrders(updatedOrders, user, clientId)
                        }
                    }
                },
                { order ->
                    showOrderDetailsDialog(order.id)
                }
            )
        }
    }

    private fun showOrderDetailsDialog(orderId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val orderDao = myDatabase.orderDao()
            val order = orderDao.findById(orderId)
            val userDao = myDatabase.userDao()
            val user = userDao.findById(order.idProvider)

            val existingRating = orderDao.getOrderEvaluation(orderId)

            launch(Dispatchers.Main) {
                val dialogBuilder = AlertDialog.Builder(requireContext())
                dialogBuilder.setTitle("Pedido ${order.id}")
                dialogBuilder.setMessage(
                    "Fornecedor: ${user.name}\n" +
                            "Morada: ${user.address} - ${user.location}\n" +
                            "Data: ${order.date}\n" +
                            "Custo: ${order.cost}\n" +
                            "Tipo de serviço: ${order.typeService}\n" +
                            "Estado: ${order.status}\n" +
                            "Descrição: ${order.description}"
                )

                if (order.status == "Concluído" && existingRating == null) {
                    dialogBuilder.setNeutralButton("Deixar avaliação") { _, _ ->
                        showRatingDialog(orderId)
                    }
                }

                dialogBuilder.setPositiveButton("Fechar") { dialog, _ ->
                    dialog.dismiss()
                }

                val alertDialog = dialogBuilder.create()
                alertDialog.show()
            }
        }
    }

    private fun showRatingDialog(orderId: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Deixar avaliação")

        val view = layoutInflater.inflate(R.layout.rating_dialog_layout, null)
        builder.setView(view)

        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)
        val editTextComment = view.findViewById<EditText>(R.id.editTextComment)

        builder.setPositiveButton("Submeter") { dialog, _ ->
            val rating = ratingBar.rating
            val comment = editTextComment.text.toString()
            saveRatingToDatabase(orderId, rating, comment)

            Toast.makeText(requireContext(), "Avaliação submetida com sucesso", Toast.LENGTH_SHORT).show()

            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun saveRatingToDatabase(orderId: Int, rating: Float, comment: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val orderDao = myDatabase.orderDao()

            orderDao.updateOrderEvaluation(orderId, rating.toInt(), comment)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment amcPedidosFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            amcPedidosFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}