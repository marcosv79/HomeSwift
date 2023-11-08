package pt.ipca.hs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainProviderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_provider)

        val name_tv = findViewById<TextView>(R.id.welcome_provider_tv)
        val name = intent.getStringExtra("name")

        if(name != null){
            name_tv.text = "Olá $name"
        }
    }
}