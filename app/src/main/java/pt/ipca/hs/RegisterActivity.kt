package pt.ipca.hs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val email = findViewById<EditText>(R.id.email_et)
        val password = findViewById<EditText>(R.id.password_et)
        val btnRegisterRa = findViewById<Button>(R.id.btn_register_ra)
        auth = FirebaseAuth.getInstance()

        btnRegisterRa.setOnClickListener(View.OnClickListener {
            val email_txt = email.text.toString()
            val password_txt = password.text.toString()

            if (TextUtils.isEmpty(email_txt) || TextUtils.isEmpty(password_txt)) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            } else if (password_txt.length < 6) {
                Toast.makeText(this, "Palavra-passe curta", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(email_txt, password_txt)
            }
        })
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Utilizador registado com sucesso", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Registo não concluído", Toast.LENGTH_SHORT).show()
            }
        }
    }
}