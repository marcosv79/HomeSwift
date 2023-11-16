package pt.ipca.hs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment

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
    private lateinit var listViewServices: ListView
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
        val searchAutoComplete = rootView.findViewById<AutoCompleteTextView>(R.id.search_auto_complete)

        myDatabase = MyDatabase.invoke(requireContext())

        val name = arguments?.getString("name")

        if (name != null) {
            name_tv.text = "Olá, $name"
        }

        // Adiciona um listener para o AutoCompleteTextView
        //setupSearchListener(searchAutoComplete)

        // Fornece sugestões ao AutoCompleteTextView
        provideSearchSuggestions(searchAutoComplete)

        return rootView
    }
/*
    private fun setupSearchListener(searchAutoComplete: AutoCompleteTextView) {
        // Substitua setOnEditorActionListener por setOnItemClickListener para lidar com a seleção de sugestões
        searchAutoComplete.setOnItemClickListener { _, _, position, _ ->
            val selectedQuery = searchAutoComplete.adapter.getItem(position) as String
            performSearch(selectedQuery)
        }
    }
*/
private fun provideSearchSuggestions(searchAutoComplete: AutoCompleteTextView) {
    // Substitua este array com seus resultados reais da pesquisa
    val suggestions = arrayOf("Carpintaria", "Jardinagem", "Outro")

    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, suggestions)
    searchAutoComplete.setAdapter(adapter)
}
/*
    private fun performSearch(query: String) {
        // Lógica de pesquisa aqui
        // Exemplo: exibir uma mensagem com o termo pesquisado
        Toast.makeText(requireContext(), "Pesquisando por: $query", Toast.LENGTH_SHORT).show()

        // Adiciona a chamada para a função que busca na base de dados
        fetchServicesFromDatabase(query)
    }

    private fun fetchServicesFromDatabase(query: String) {
        val userDao = myDatabase.userDao()
        val users = userDao.findByService(query)

        if (users.isNotEmpty()) {
            // Exibir o serviço correspondente ao termo pesquisado
            val service = users.first().service // Supondo que a sua classe User tenha uma propriedade chamada service
            displayServices(service)
        } else {
            // Lidar com o caso em que nenhum serviço foi encontrado
            Toast.makeText(requireContext(), "Nenhum serviço encontrado para: $query", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayServices(service: String) {
        // Aqui você pode implementar a lógica para exibir o serviço da maneira desejada
        // Por exemplo, pode abrir uma nova tela ou mostrar em um diálogo
        // Neste exemplo, apenas exibimos uma mensagem com o serviço encontrado
        val serviceMessage = "Serviço encontrado: $service"
        Toast.makeText(requireContext(), serviceMessage, Toast.LENGTH_SHORT).show()
    }


*/

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