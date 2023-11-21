package pt.ipca.hs

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ampMensagensFragment : Fragment() {

    private lateinit var listViewMessages: ListView
    private lateinit var myDatabase: MyDatabase
    private lateinit var messagesList: List<Message>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_amp_mensagens, container, false)

        listViewMessages = rootView.findViewById(R.id.listViewContacts)
        myDatabase = MyDatabase.invoke(requireContext())

        // Configurar o OnItemClickListener para a ListView
        listViewMessages.setOnItemClickListener { _, _, position, _ ->
            if (::messagesList.isInitialized && position < messagesList.size) {
                val selectedMessage = messagesList[position]

                lifecycleScope.launch(Dispatchers.Main) {
                    val providerName = getUserName(selectedMessage.senderId.toInt())
                    val intent = Intent(requireContext(), Chat_Layout::class.java)
                    intent.putExtra("providerId", selectedMessage.senderId.toInt())
                    intent.putExtra("userId", selectedMessage.receiverId.toInt())
                    intent.putExtra("providerName", providerName)
                    startActivity(intent)
                }
            }
        }

        getMessages()

        return rootView
    }

    private fun getMessages() {
        lifecycleScope.launch(Dispatchers.IO) {
            val messageDao = myDatabase.messageDao()

            messagesList = messageDao.getAllMessages() // Inicialize a variável aqui

            launch(Dispatchers.Main) {
                val adapter = createMessageAdapter(messagesList)
                listViewMessages.adapter = adapter
            }
        }
    }

    private suspend fun createMessageAdapter(messagesList: List<Message>): ArrayAdapter<String> {
        val adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_list_item_1
        )

        // Agrupa mensagens por usuário
        val messagesByUser =
            messagesList.groupBy { it.senderId } // Alterado para usar o receiverId

        // Adiciona apenas a última mensagem de cada usuário ao adaptador
        for ((userId, userMessages) in messagesByUser) {
            val userName = userMessages.firstOrNull()?.let { getUserName(it.senderId.toInt()) }
                ?: "Nome não disponível"
            val lastMessage = userMessages.lastOrNull()?.message ?: "Sem mensagens"
            adapter.add("$userName\n$lastMessage")
        }

        return adapter
    }

    private suspend fun getUserName(userId: Int): String {
        return withContext(Dispatchers.IO) {
            val userDao = myDatabase.userDao()

            try {
                // Supondo que seu método de busca seja getUserById
                val user = userDao.getUserById(userId)

                if (user != null) {
                    Log.d("ampMensagensFragment", "User found - Name: ${user.name}")
                    return@withContext user.name ?: "Nome não disponível"
                } else {
                    Log.d("ampMensagensFragment", "User not found for ID: $userId")
                    return@withContext "Nome não disponível"
                }
            } catch (e: Exception) {
                Log.e("ampMensagensFragment", "Error fetching user: ${e.message}")
                return@withContext "Erro ao obter o nome do usuário"
            }
        }
    }
}