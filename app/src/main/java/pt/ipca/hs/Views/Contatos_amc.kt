package pt.ipca.hs.Views

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.ipca.hs.MyDatabase
import pt.ipca.hs.R

class contatos_amc : AppCompatActivity() {

    private lateinit var listViewContacts: ListView
    private lateinit var myDatabase: MyDatabase
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contatos_amc)

        listViewContacts = findViewById(R.id.listViewNovaPagina)
        myDatabase = MyDatabase.invoke(this)

        getProviders()
        val backButton: ImageButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun getProviders() {
        lifecycleScope.launch(Dispatchers.IO) {
            val userDao = myDatabase.userDao()

            val clientsList = userDao.getUsersProvider()

            launch(Dispatchers.Main) {
                val adapter = ArrayAdapter(
                    this@contatos_amc,
                    android.R.layout.simple_list_item_1,
                    clientsList.map { it.name ?: "Nome não disponível" }
                )
                listViewContacts.adapter = adapter
            }
        }
    }
}