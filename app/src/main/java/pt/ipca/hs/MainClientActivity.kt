package pt.ipca.hs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainClientActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_client)

        val name_tv = findViewById<TextView>(R.id.welcome_client_tv)
        val name = intent.getStringExtra("name")

        if(name != null){
            name_tv.text = "Olá $name"
        }
    }
}