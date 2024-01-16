package pt.ipca.hs

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.ipca.hs.Models.Order
import pt.ipca.hs.Models.User
import pt.ipca.hs.Views.OrderAdapterP

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
/**
 * A simple [Fragment] subclass.
 * Use the [ampPedidosFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ampPedidosFragment : Fragment() {
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
        val idProvider = arguments?.getInt("id", 0)
        myDatabase = MyDatabase.invoke(requireContext())
        rootView = inflater.inflate(R.layout.fragment_amp_pedidos, container, false)

        val spinnerFilter = rootView?.findViewById<Spinner>(R.id.spinnerFilterOrderStatus)
        val statusFilterOptions = listOf("Todos", "Ativo", "Concluído")

        spinnerFilter?.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, statusFilterOptions)

        spinnerFilter?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long){
                val selectedStatus = statusFilterOptions[position]
                if (idProvider != null) {
                    loadOrdersForProvider(idProvider,selectedStatus)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        idProvider?.let { providerId ->
            loadOrdersForProvider(providerId, "Todos")
        }

        return rootView
    }

    private fun loadOrdersForProvider(providerId: Int, statusFilter: String) {
        rootView?.let { root ->
            lifecycleScope.launch(Dispatchers.IO) {
                val userDao = myDatabase.userDao()
                val users = userDao.getAll()

                val orderDao = myDatabase.orderDao()
                val orders =
                    if(statusFilter == "Todos"){
                        orderDao.getOrdersByProviderId(providerId)
                    } else {
                        orderDao.getOrdersByProviderAndStatus(providerId, statusFilter)
                    }

                launch(Dispatchers.Main) {
                    updateUIWithOrders(orders, users, providerId)
                }
            }
        }
    }

    private fun updateUIWithOrders(orders: List<Order>, users: List<User>, providerId: Int) {
        val recyclerView = rootView?.findViewById<RecyclerView>(R.id.recyclerViewPedidos)
        val noOrdersMessage = rootView?.findViewById<TextView>(R.id.noOrdersMessage)

        if (orders.isEmpty()) {
            noOrdersMessage?.visibility = View.VISIBLE
            recyclerView?.visibility = View.GONE
        } else {
            noOrdersMessage?.visibility = View.GONE
            recyclerView?.visibility = View.VISIBLE

            // Ordenar a lista de pedidos pelo formato de data
            val sortedOrders = orders.sortedByDescending { order ->
                val parts = order.date.split("/")
                val year = parts[2].toInt()
                val month = parts[1].toInt()
                val day = parts[0].toInt()
                year * 10000 + month * 100 + day
            }

            recyclerView?.layoutManager = LinearLayoutManager(requireContext())
            recyclerView?.adapter = OrderAdapterP(sortedOrders, users,
                { orderId ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        val orderDao = myDatabase.orderDao()
                        orderDao.deleteOrderById(orderId)

                        val updatedOrders = orderDao.getOrdersByProviderId(providerId)
                        val userDao = myDatabase.userDao()
                        val user = userDao.getAll()

                        launch(Dispatchers.Main) {
                            updateUIWithOrders(updatedOrders, user, providerId)
                        }
                    }
                },
                { orderId ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        val orderDao = myDatabase.orderDao()
                        orderDao.updateOrderStatus(orderId, "Concluído")

                        val updatedOrders = orderDao.getOrdersByProviderId(providerId)
                        val userDao = myDatabase.userDao()
                        val user = userDao.getAll()

                        launch(Dispatchers.Main) {
                            updateUIWithOrders(updatedOrders, user, providerId)
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
            val user = userDao.findById(order.idClient)

            launch(Dispatchers.Main) {
                val dialogBuilder = AlertDialog.Builder(requireContext())
                dialogBuilder.setTitle("Pedido ${order.id}")
                dialogBuilder.setMessage(
                            "Cliente: ${user.name}\n" +
                            "Morada: ${user.address} - ${user.location}\n" +
                            "Data: ${order.date}\n" +
                            "Hora: ${order.hour}\n" +
                            "Custo: ${order.cost}\n" +
                            "Tipo de serviço: ${order.typeService}\n" +
                            "Estado: ${order.status}\n" +
                            "Descrição: ${order.description}"
                )
                dialogBuilder.setPositiveButton("Fechar") { dialog, _ ->
                    dialog.dismiss()
                }

                val alertDialog = dialogBuilder.create()
                alertDialog.show()
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ampPedidosFragment.
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