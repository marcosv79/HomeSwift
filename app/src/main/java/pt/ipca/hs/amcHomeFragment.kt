package pt.ipca.hs

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [amcHomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class amcHomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var serviceSpinner: Spinner
    private lateinit var listView: ListView
    private lateinit var usersAdapter: ArrayAdapter<String>
    private lateinit var myDatabase: MyDatabase

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
        val rootView = inflater.inflate(R.layout.fragment_amc_home, container, false)
        val name_tv = rootView.findViewById<TextView>(R.id.welcome_client_tv)

        val name = arguments?.getString("name")

        if (name != null) {
            name_tv.text = "Olá, $name"
        }

        serviceSpinner = rootView.findViewById(R.id.spinner_service_amc_home)
        listView = rootView.findViewById(R.id.lv_service_amc_home)
        myDatabase = MyDatabase.invoke(requireContext())

        val servicesAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.service,
            android.R.layout.simple_spinner_item
        )
        servicesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        serviceSpinner.adapter = servicesAdapter

        usersAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1
        )
        listView.adapter = usersAdapter

        serviceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedService = parent?.getItemAtPosition(position).toString()
                getSelectedProvider(selectedService)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                getProviders()
            }
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedProviderName = listView.getItemAtPosition(position).toString()

            lifecycleScope.launch(Dispatchers.IO) {
                val userDao = MyDatabase.invoke(requireContext()).userDao()
                val selectedProvider = userDao.findByName(selectedProviderName)

                launch(Dispatchers.Main) {
                    if (selectedProvider != null) {
                        navigateToProviderPage(selectedProvider.id)
                    }
                }
            }
        }
        return rootView
    }

    private fun navigateToProviderPage(providerId: Int) {
        val intent = Intent(requireContext(), ProviderPageActivity::class.java)
        intent.putExtra("id", providerId)
        startActivity(intent)
    }


    private fun getSelectedProvider(selectedService: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val userDao = myDatabase.userDao()

            val providersList = userDao.getUsersByService(selectedService)

            launch(Dispatchers.Main) {
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    providersList.map { it.name ?: "Nome não disponível" }
                )
                listView.adapter = adapter
            }
        }
    }


    private fun getProviders() {
        lifecycleScope.launch(Dispatchers.IO) {
            val userDao = myDatabase.userDao()

            val clientsList = userDao.getUsersProvider()

            launch(Dispatchers.Main) {
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    clientsList.map { it.name ?: "Nome não disponível" }
                )
                listView.adapter = adapter
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
         * @return A new instance of fragment amcHomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            amcHomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}