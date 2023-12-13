package pt.ipca.hs

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [amaPerfilFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class amaPerfilFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var myDatabase: MyDatabase

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var moradaEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var btnGuardar: Button
    private lateinit var spinnerLocation: Spinner
    private lateinit var btnLogout: Button

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_ama_perfil, container, false)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        myDatabase = MyDatabase.invoke(requireContext())
        nameEditText = view.findViewById(R.id.et_name_ama_perfil)
        emailEditText = view.findViewById(R.id.et_email_ama_perfil)
        passwordEditText = view.findViewById(R.id.et_password_ama_perfil)
        moradaEditText = view.findViewById(R.id.et_address_ama_perfil)
        spinnerLocation = view.findViewById(R.id.spinner_location_ama_perfil)
        btnGuardar = view.findViewById(R.id.btn_save_ama_perfil)
        btnLogout = view.findViewById(R.id.btn_logout_admin)

        nameEditText.isEnabled = false
        emailEditText.isEnabled = false

        loadAdminData()

        btnGuardar.setOnClickListener {
            saveNewData()
        }

        btnLogout.setOnClickListener {
            logout()
        }

        return view
    }

    private fun saveNewData(){
        Thread{
            val userDao = myDatabase.userDao()

            val existingUser = userDao.findByEmail(emailEditText.text.toString())

            if(existingUser != null){
                val address = moradaEditText.text.toString()
                val selectedLocation = spinnerLocation.selectedItem.toString()
                val newPassword = passwordEditText.text.toString()

                if(address.isNotEmpty()){
                    existingUser.address = address
                } else{
                    activity?.runOnUiThread{
                        Toast.makeText(context, "Preencha a morada", Toast.LENGTH_SHORT).show()
                    }
                    return@Thread
                }

                if(selectedLocation.isNotEmpty()){
                    existingUser.location = selectedLocation
                } else{
                    activity?.runOnUiThread{
                        Toast.makeText(context, "Preencha a localidade", Toast.LENGTH_SHORT).show()
                    }
                    return@Thread
                }

                if(newPassword.isNotEmpty() && newPassword.length >= 6){
                    existingUser.password = newPassword
                } else if (newPassword.isNotEmpty()) {
                    activity?.runOnUiThread {
                        Toast.makeText(context, "Palavra-passe curta", Toast.LENGTH_SHORT).show()
                    }
                    return@Thread
                }

                userDao.updateUser(existingUser)

                activity?.runOnUiThread {
                    Toast.makeText(context, "Informações atualizadas com sucesso", Toast.LENGTH_SHORT).show()
                    updateFirestore(existingUser.email)
                }
            }
        }.start()
    }

    private fun updateFirestore(email: String){
        val address = moradaEditText.text.toString()
        val selectedLocation = spinnerLocation.selectedItem.toString()
        val newPassword = passwordEditText.text.toString()

        val userCollection = firestore.collection("users")
        val query = userCollection.whereEqualTo("email", email)

        query.get().addOnSuccessListener { documents ->
            for(document in documents){
                document.reference.update(
                    "address", address,
                    "location", selectedLocation
                ).addOnSuccessListener {
                    if(newPassword.isNotEmpty()){
                        auth.currentUser?.updatePassword(newPassword)
                            ?.addOnCompleteListener{task ->
                                if(task.isSuccessful){
                                    Toast.makeText(context, "Palavra-passe atualizada com sucesso", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Erro a atualizar palavra-passe", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }
            }
        }
    }

    private fun loadAdminData(){
        val userEmail = getEmailFromArguments()
//aklsmdakl
        if(!userEmail.isNullOrEmpty()){
            AsyncTask.execute{
                val userDao = myDatabase.userDao()
                val user = userDao.findByEmail(userEmail)

                activity?.runOnUiThread {
                    user?.let{
                        val userName = it.name
                        val userAddress = it.address
                        val userLocation = it.location

                        nameEditText.setText(userName)
                        emailEditText.setText(userEmail)

                        if(!userAddress.isNullOrEmpty()){
                            moradaEditText.setText(userAddress)
                        }

                        if(!userLocation.isNullOrEmpty()){
                            val adapter = spinnerLocation.adapter as ArrayAdapter<String>
                            val position = adapter.getPosition(userLocation)
                            spinnerLocation.setSelection(position)
                        }
                    }
                }
            }
        }
    }

    private fun getEmailFromArguments(): String?{
        return  arguments?.getString("email")
    }

    private fun logout(){
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(context, "Sessão terminada", Toast.LENGTH_SHORT).show()
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}