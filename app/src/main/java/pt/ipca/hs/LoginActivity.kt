package pt.ipca.hs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userDao: UserDao
    private lateinit var myDatabase: MyDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val email_et_la = findViewById<EditText>(R.id.email_et_la)
        val password_et_la = findViewById<EditText>(R.id.password_et_la)
        val btn_login_la =  findViewById<Button>(R.id.btn_login_la)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        myDatabase = MyDatabase.invoke(applicationContext)
        userDao = myDatabase.userDao()

        val intent = intent
        val email = intent.getStringExtra("email")

        btn_login_la.setOnClickListener(View.OnClickListener {
            val email_txt = email_et_la.text.toString()
            val password_txt = password_et_la.text.toString()
            loginUser(email_txt, password_txt)
        } )

        if(intent.hasExtra("email")){
            val registeredEmail = intent.getStringExtra("email")
            email_et_la.setText(registeredEmail)
        }
    }

    private fun loginUser(email: String, password: String){
        lifecycleScope.launch(Dispatchers.IO){
            val user = userDao.getUserByEmailAndPassword(email, password)

            runOnUiThread{
                if(user != null){
                    val userType = user.userType
                    val name = user.name

                    when(userType){
                        "Cliente" -> {
                            if (name != null) {
                                startAmcPerfilFragment(email, name)
                            }
                            Toast.makeText(this@LoginActivity, "Login realizado com sucesso", Toast.LENGTH_SHORT).show()
                        }
                        "Fornecedor" -> {
                            if (name != null) {
                                startAmpPerfilFragment(email, name)
                            }
                            Toast.makeText(this@LoginActivity, "Login realizado com sucesso", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(this@LoginActivity, "Login realizado com sucesso", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@LoginActivity, MainAdminActivity::class.java)
                            intent.putExtra("name", name)
                            startActivity(intent)
                        }
                    }
            } else {
                Toast.makeText(this@LoginActivity, "Credenciais inválidas", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

    fun startAmcPerfilFragment(email: String, name: String) {
        val intent = Intent(this, MainClientActivity::class.java)
        intent.putExtra("email", email)
        intent.putExtra("name", name)
        startActivity(intent)
    }

    fun startAmpPerfilFragment(email: String, name: String) {
        val intent = Intent(this, MainProviderActivity::class.java)
        intent.putExtra("email", email)
        intent.putExtra("name", name)
        startActivity(intent)
    }
}