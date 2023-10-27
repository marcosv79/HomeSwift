package pt.ipca.hs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val btn_logout = findViewById<Button>(R.id.btn_logout)

        btn_logout.setOnClickListener(View.OnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "Sessão terminada", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        })

        val add_name = findViewById<EditText>(R.id.add_name_et)
        val btn_add_name = findViewById<Button>(R.id.btn_add_name)

        btn_add_name.setOnClickListener(View.OnClickListener {
            val txt_name = add_name.text.toString()
            if(txt_name.isEmpty()){
                Toast.makeText(this, "Nenhum nome inserido", Toast.LENGTH_SHORT).show()
            } else {
                FirebaseDatabase.getInstance().reference.child("HomeSwift").push().child("Name").setValue(txt_name)
            }
        })

    }
}