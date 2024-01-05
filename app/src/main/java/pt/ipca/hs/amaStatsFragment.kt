package pt.ipca.hs

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [amaStatsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class amaStatsFragment : Fragment() {

    private lateinit var myDatabase: MyDatabase
    private lateinit var orderDao: OrderDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myDatabase = MyDatabase.invoke(requireContext())
        orderDao = myDatabase.orderDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_ama_stats, container, false)

        val topClientsTextView = rootView?.findViewById<TextView>(R.id.topClientsTextView)
        val topProvidersTextView = rootView.findViewById<TextView>(R.id.topProvidersTextView)
        val topServicesTextView = rootView.findViewById<TextView>(R.id.topServicesTextView)

        lifecycleScope.launch(Dispatchers.IO) {
            orderDao = myDatabase.orderDao()

            val topProviders = orderDao.getTopProviders()
            val topProvidersString = StringBuilder()
            for ((index, provider) in topProviders.withIndex()) {
                val userDao = myDatabase.userDao()
                val providerName = userDao.findById(provider.idProvider).name
                topProvidersString.append("${index + 1}. $providerName - ${provider.serviceCount} serviços\n")
            }

            val topServices = orderDao.getTopServices()
            val topServicesString = StringBuilder()
            for ((index, service) in topServices.withIndex()) {
                topServicesString.append("${index + 1}. ${service.service} - ${service.serviceCount} vezes\n")
            }

            val topClients = orderDao.getTopClients()
            val topClientsString = StringBuilder()
            for((index, client) in topClients.withIndex()){
                val userDao = myDatabase.userDao()
                val clientName = userDao.findById(client.idClient).name
                topClientsString.append("${index + 1}. $clientName - ${client.serviceCount} serviços\n")
            }

            withContext(Dispatchers.Main) {
                topProvidersTextView?.text = topProvidersString.toString()
                topServicesTextView?.text = topServicesString.toString()
                topClientsTextView?.text = topClientsString.toString()
            }
        }

        return rootView
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment amaStatsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            amaStatsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}