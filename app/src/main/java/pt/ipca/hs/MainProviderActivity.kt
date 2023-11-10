package pt.ipca.hs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.fragment.app.Fragment
import pt.ipca.hs.databinding.ActivityMainClientBinding
import pt.ipca.hs.databinding.ActivityMainProviderBinding

class MainProviderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainProviderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainProviderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(ampHomeFragment())

        binding.bottomNavigationViewProvider.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_amp -> replaceFragment(ampHomeFragment())
                R.id.mensagens_amp -> replaceFragment(ampMensagensFragment())
                R.id.pedidos_amp -> replaceFragment(ampPedidosFragment())
                R.id.perfil_amp -> replaceFragment(ampPerfilFragment())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val name = intent.getStringExtra("name")
        val bundle = Bundle()
        bundle.putString("name", name)
        fragment.arguments = bundle

        fragmentTransaction.replace(R.id.frame_layout_amp, fragment)
        fragmentTransaction.commit()
    }
}