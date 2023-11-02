package pt.ipca.hs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val email_et_la = findViewById<EditText>(R.id.email_et_la)
        val password_et_la = findViewById<EditText>(R.id.password_et_la)
        val btn_login_la =  findViewById<Button>(R.id.btn_login_la)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

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
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                val user = auth.currentUser
                if (user != null){
                    firestore.collection("users")
                        .document(user.uid)
                        .get()
                        .addOnSuccessListener { document ->
                            if (document != null){
                                val userType = document.getString("userType")

                                if (userType == "Cliente"){
                                    Toast.makeText(this, "Login realizado com sucesso", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, MainClientActivity::class.java)
                                    startActivity(intent)
                                } else if (userType == "Fornecedor"){
                                    Toast.makeText(this, "Login realizado com sucesso", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, MainProviderActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(this, "Login realizado com sucesso", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, MainAdminActivity::class.java)
                                    startActivity(intent)
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Erro a obter informações do utilizador", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(this, "Credenciais inválidas", Toast.LENGTH_SHORT).show()
            }
        }
    }
}