package pt.ipca.hs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

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
    }
}