package pt.ipca.hs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

class amcMensagensFragment : Fragment() {

    private lateinit var listViewContacts: ListView
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_amp_mensagens, container, false)

        listViewContacts = rootView.findViewById(R.id.listViewContacts)
        firestore = FirebaseFirestore.getInstance()

        // Buscar apenas os fornecedores do Firebase Firestore
        fetchProvidersFromFirestore()

        return rootView
    }

    private fun fetchProvidersFromFirestore() {
        // Substitua "users" pelo nome da sua coleção no Firestore
        firestore.collection("users")
            .whereEqualTo("userType", "Fornecedor")
            .get()
            .addOnSuccessListener { result ->
                val providersList = mutableListOf<String>()

                for (document in result) {
                    // Supondo que você tenha um campo "name" no documento
                    val providerName = document.getString("name")
                    providersList.add(providerName ?: "Nome não disponível")
                }

                // Configurar o adaptador para a ListView
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, providersList)
                listViewContacts.adapter = adapter
            }
            .addOnFailureListener { exception ->
                // Lidar com falha na obtenção de dados do Firestore
                exception.printStackTrace()
            }
    }
}
