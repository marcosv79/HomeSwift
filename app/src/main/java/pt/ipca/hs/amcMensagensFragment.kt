package pt.ipca.hs

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class amcMensagensFragment : Fragment() {

    private lateinit var listViewMessages: ListView
    private lateinit var myDatabase: MyDatabase
    private lateinit var messagesList: List<MensagensGroup>
    private var idClient: Int? = 0
    private lateinit var idProviders: ArrayList<Int>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_amc_mensagens, container, false)

        idClient = arguments?.getInt("idC", 0)
        idProviders = ArrayList<Int>()

        listViewMessages = rootView.findViewById(R.id.listViewContacts)
        myDatabase = MyDatabase.invoke(requireContext())

        listViewMessages.setOnItemClickListener { _, _, position, _ ->
            if (::messagesList.isInitialized && position < messagesList.size) {
                val providerId = idProviders[position]
                Log.d("amcMensagens", "ID Prov: $providerId")
                if(providerId >0){
                    lifecycleScope.launch(Dispatchers.Main) {
                        val intent = Intent(requireContext(), Chat_Layout::class.java)
                        intent.putExtra("providerId", idClient)      // ID do fornecedor
                        intent.putExtra("userId", providerId)        // ID do usuário
                        intent.putExtra("CurrentUserId", providerId)  // ID do usuário atual

                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(context, "Provider invalido, contate o admin", Toast.LENGTH_SHORT).show()
                }
            }
        }
        getMessages()

        return rootView
    }

    private fun getMessages() {
        lifecycleScope.launch(Dispatchers.IO) {
            val messageDao = myDatabase.messageDao()

            try {
                messagesList = messageDao.getuserMensagens(idClient)
                launch(Dispatchers.Main) {
                    val adapter = createMessageAdapter(messagesList)
                    listViewMessages.adapter = adapter
                }
            } catch (e: Exception) {
                Log.e("Error", "Error getting messages: ${e.message}")
            }
        }
    }

    private suspend fun createMessageAdapter(messagesList: List<MensagensGroup>): ArrayAdapter<String> {
        val adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_list_item_1
        )

        var client = idClient.toString()


        for(message in messagesList){
            var receivers = message.receiver.split(",")
            var messages = message.messages.split(",")
            var providerName = ""
            var providerRow = ""

            for(receiver in receivers)
            {
                if(receiver != client)
                {
                    idProviders.add(receiver.toInt())
                    providerName = getUserName(receiver.toInt())
                    providerRow = providerName + "\n" + messages.last()
                    adapter.add(providerRow)
                    break

                }

            }

        }

        return adapter
    }

    private suspend fun getUserName(userId: Int): String {
        return withContext(Dispatchers.IO) {
            val userDao = myDatabase.userDao()

            try {
                val user = userDao.getUserById(userId)

                if (user != null) {
                    Log.d("amcMensagensFragment", "User found - Name: ${user.name}")
                    return@withContext user.name ?: "Nome não disponível"
                } else {
                    Log.d("amcMensagensFragment", "User not found for ID: $userId")
                    return@withContext "Nome não disponível"
                }
            } catch (e: Exception) {
                Log.e("amcMensagensFragment", "Error fetching user: ${e.message}")
                return@withContext "Erro ao obter o nome do usuário"
            }
        }
    }
}
