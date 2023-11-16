package pt.ipca.hs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class amcMensagensFragment : Fragment() {

    private lateinit var listViewContacts: ListView
    private lateinit var myDatabase: MyDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_amp_mensagens, container, false)

        listViewContacts = rootView.findViewById(R.id.listViewContacts)
        myDatabase = MyDatabase.invoke(requireContext())

        getProviders()

        return rootView
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
                listViewContacts.adapter = adapter
            }
        }
    }
}
