package pt.ipca.hs

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val email_et_la = findViewById<EditText>(R.id.email_et_la)
        val password_et_la = findViewById<EditText>(R.id.password_et_la)
        val btn_login_la =  findViewById<Button>(R.id.btn_login_la)

        //auth = FirebaseAuth.getInstance()
        //firestore = FirebaseFirestore.getInstance()
        myDatabase = MyDatabase.invoke(applicationContext)
        userDao = myDatabase.userDao()

        val createAccountTV = findViewById<TextView>(R.id.create_account_tv)
        createAccountTV.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val intent = intent
        val email = intent.getStringExtra("email")

        val savedEmail = sharedPreferences.getString("email", "")
        val savedPassword = sharedPreferences.getString("password", "")

        if (!TextUtils.isEmpty(savedEmail) && !TextUtils.isEmpty(savedPassword)) {
            email_et_la.setText(savedEmail)
            password_et_la.setText(savedPassword)
            if (savedEmail != null && savedPassword != null) {
                loginUser(savedEmail, savedPassword)
            }
        }

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
                    val editor = sharedPreferences.edit()
                    editor.putString("email", email)
                    editor.putString("password", password)
                    editor.apply()
                    val userType = user.userType
                    val name = user.name
                    val id = user.id

                    when(userType){
                        "Cliente" -> {
                            if (name != null) {
                                startMainClientActivity(id, email, name)
                            }
                            Toast.makeText(this@LoginActivity, "Login realizado com sucesso", Toast.LENGTH_SHORT).show()
                        }
                        "Fornecedor" -> {
                            if (name != null) {
                                startMainProviderActivity(id, email, name)
                            }
                            Toast.makeText(this@LoginActivity, "Login realizado com sucesso", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            if (name != null) {
                                startMainAdminActivity(email, name)
                            }
                            Toast.makeText(this@LoginActivity, "Login realizado com sucesso", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Credenciais inválidas", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun startMainAdminActivity(email: String, name: String){
        val intent = Intent(this, MainAdminActivity::class.java)
        intent.putExtra("name", name)
        intent.putExtra("email", email)
        startActivity(intent)
    }

    fun startMainClientActivity(id: Int, email: String, name: String) {
        val intent = Intent(this, MainClientActivity::class.java)
        intent.putExtra("idC", id)
        intent.putExtra("email", email)
        intent.putExtra("name", name)
        startActivity(intent)
    }

    fun startMainProviderActivity(id: Int, email: String, name: String) {
        val intent = Intent(this, MainProviderActivity::class.java)
        intent.putExtra("id", id)
        intent.putExtra("email", email)
        intent.putExtra("name", name)
        startActivity(intent)
    }
}