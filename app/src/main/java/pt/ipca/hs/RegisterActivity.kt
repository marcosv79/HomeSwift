package pt.ipca.hs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val email = findViewById<EditText>(R.id.email_et)
        val password = findViewById<EditText>(R.id.password_et)
        val btnRegisterRa = findViewById<Button>(R.id.btn_register_ra)
        val userTypeSpinner = findViewById<Spinner>(R.id.spinner_user_type)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val userTypeAdapter = ArrayAdapter.createFromResource(this, R.array.user_types, android.R.layout.simple_spinner_item)
        userTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        userTypeSpinner.adapter = userTypeAdapter

        btnRegisterRa.setOnClickListener(View.OnClickListener {
            val email_txt = email.text.toString()
            val password_txt = password.text.toString()
            val selectedUserType = userTypeSpinner.selectedItem.toString()

            if (TextUtils.isEmpty(email_txt) || TextUtils.isEmpty(password_txt)) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            } else if (password_txt.length < 6) {
                Toast.makeText(this, "Palavra-passe curta", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(email_txt, password_txt, selectedUserType)
            }
        })
    }

    private fun registerUser(email: String, password: String, userType: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser

                val userDocument = firestore.collection("users").document(user?.uid.toString())
                val userData = hashMapOf(
                    "email" to email,
                    "userType" to userType
                )
                userDocument.set(userData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Utilizador registado com sucesso", Toast.LENGTH_SHORT).show()
                        startLoginActivity()
                    }
                    .addOnFailureListener{
                        Toast.makeText(this, "Registo não concluído", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Registo não concluído", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun startLoginActivity(){
        val email_et = findViewById<EditText>(R.id.email_et)
        val email = email_et.text.toString()
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("email",email)
        startActivity(intent)
    }
}