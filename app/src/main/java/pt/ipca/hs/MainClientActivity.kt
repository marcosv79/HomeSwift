package pt.ipca.hs

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import pt.ipca.hs.databinding.ActivityMainClientBinding

class MainClientActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainClientBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainClientBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(amcHomeFragment())

        binding.bottomNavigationViewClient.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_amc -> replaceFragment(amcHomeFragment())
                R.id.mensagens_amc -> replaceFragment(amcMensagensFragment())
                R.id.pedidos_amc -> replaceFragment(amcPedidosFragment())
                R.id.perfil_amc -> replaceFragment(amcPerfilFragment())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val name = intent.getStringExtra("name")
        val email = intent.getStringExtra("email")
        val bundle = Bundle()
        bundle.putString("name", name)
        bundle.putString("email", email)

        fragment.arguments = bundle

        fragmentTransaction.replace(R.id.frame_layout_amc, fragment)
        fragmentTransaction.commit()
    }
}

