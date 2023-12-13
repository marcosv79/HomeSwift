package pt.ipca.hs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.Fragment
import pt.ipca.hs.databinding.ActivityMainAdminBinding

class MainAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainAdminBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(amaHomeFragment())

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.home_ama -> replaceFragment(amaHomeFragment())
                R.id.perfil_ama -> replaceFragment(amaPerfilFragment())
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
        fragmentTransaction.replace(R.id.frame_layout_ama,fragment)
        fragmentTransaction.commit()
    }
}