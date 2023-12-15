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
                        intent.putExtra("providerId", providerId)
                        intent.putExtra("userId", idClient)
                        intent.putExtra("CurrentUserId", idClient)

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

        for (message in messagesList) {
            try {
                // Separando os IDs de destinatário
                val receiverIds = message.receiver.split(",").mapNotNull { it.toIntOrNull() }

                // Verificando se você é o destinatário
                if (receiverIds.contains(idClient)) {
                    // Adicionando apenas mensagens em que você é o destinatário
                    val senderIds = message.sender.split(",").mapNotNull { it.toIntOrNull() }

                    // Removendo o seu ID da lista de remetentes
                    val otherSenderIds = senderIds.filter { it != idClient }

                    if (otherSenderIds.isNotEmpty()) {
                        idProviders.addAll(otherSenderIds)

                        for (userId in otherSenderIds) {
                            try {
                                // Obtendo o nome do usuário
                                val userName = getUserName(userId)
                                adapter.add("$userName\n${message.messages}")
                            } catch (e: Exception) {
                                Log.e("ampMensagensFragment", "Error getting user name: ${e.message}")
                            }
                        }
                    } else {
                        Log.e("ampMensagensFragment", "No valid sender IDs found")
                    }
                }
            } catch (e: NumberFormatException) {
                Log.e("ampMensagensFragment", "Error converting sender and receiver IDs: ${message.sender}, ${message.receiver}")
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
