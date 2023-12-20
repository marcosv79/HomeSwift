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

class ampMensagensFragment : Fragment() {

    private lateinit var listViewMessages: ListView
    private lateinit var myDatabase: MyDatabase
    private lateinit var messagesList: List<MensagensGroup>
    private var providerid: Int? = 0
    private lateinit var idclients: ArrayList<Int>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_amp_mensagens, container, false)

        idclients = ArrayList<Int>()

        listViewMessages = rootView.findViewById(R.id.listViewContacts)
        myDatabase = MyDatabase.invoke(requireContext())

        providerid = arguments?.getInt("providerId", 0)

        listViewMessages.setOnItemClickListener { _, _, position, _ ->
            if (::messagesList.isInitialized && position < messagesList.size) {
                val clienteid = idclients[position]

                if (clienteid > 0) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        val intent = Intent(requireContext(), Chat_Layout::class.java)
                        intent.putExtra("providerId", clienteid)  // ID do fornecedor
                        intent.putExtra("userId", providerid)       // ID do usuário
                        intent.putExtra("CurrentUserId", providerid)  // ID do usuário atual

                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(
                        context,
                        "cliente invalido, contate o admin",
                        Toast.LENGTH_SHORT
                    ).show()
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
                messagesList = messageDao.getuserMensagens(providerid)
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
        var provider = providerid.toString()


        for(message in messagesList){
            var senders = message.sender.split(",")
            var messages = message.messages.split(",")
            var clientName = ""
            var clientRow = ""

            for(sender in senders)
            {
                if(sender != provider)
                {
                    idclients.add(sender.toInt())
                    clientName = getUserName(sender.toInt())
                    clientRow = clientName + "\n" + messages.last()
                    adapter.add(clientRow)
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
