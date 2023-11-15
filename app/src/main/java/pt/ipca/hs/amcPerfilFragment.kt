package pt.ipca.hs

import android.content.Intent
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

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class amcPerfilFragment : Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_amc_perfil, container, false)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        myDatabase = MyDatabase.invoke(requireContext())
        nameEditText = view.findViewById(R.id.et_name_amc_perfil)
        emailEditText = view.findViewById(R.id.et_email_amc_perfil)
        passwordEditText = view.findViewById(R.id.et_password_amc_perfil)
        moradaEditText = view.findViewById(R.id.et_address_amc_perfil)
        spinnerLocation = view.findViewById(R.id.spinner_location_amc_perfil)
        btnGuardar = view.findViewById(R.id.btn_save_amc_perfil)
        btnLogout = view.findViewById(R.id.btn_logout_client)

        nameEditText.isEnabled = false
        emailEditText.isEnabled = false

        loadUserData()

        btnGuardar.setOnClickListener{
            saveNewData()
        }

        btnLogout.setOnClickListener{
            logout()
        }

        return view
    }

    private fun saveNewData(){
        val userId = auth.currentUser?.uid
        val address = moradaEditText.text.toString()
        val selectedLocation = spinnerLocation.selectedItem.toString()
        val newPassword = passwordEditText.text.toString()

        if(userId != null){
            val userDocument = firestore.collection("users").document(userId)

            userDocument
                .update("address", address, "location", selectedLocation)
                .addOnSuccessListener {
                    Toast.makeText(context, "Informações atualizadas com sucesso", Toast.LENGTH_SHORT).show()

                    Thread{
                        val userDao = myDatabase.userDao()

                        val existingUser = userDao.findByEmail(emailEditText.text.toString())

                        if(existingUser != null){
                            existingUser.address = address
                            existingUser.location = selectedLocation

                            if(newPassword.isNotEmpty()){
                                existingUser.password = newPassword
                            }

                            userDao.updateUser(existingUser)

                            activity?.runOnUiThread{
                            }
                        }
                    }.start()

                    if(newPassword.isNotEmpty()){
                        auth.currentUser?.updatePassword(newPassword)
                            ?.addOnCompleteListener{ task ->
                                if(task.isSuccessful){
                                    Toast.makeText(context, "Palavra-passe atualizada com sucesso", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Erro a atualizar palavra-passe", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "Erro a atualizar informações", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadUserData() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val userDocument = firestore.collection("users").document(userId)

            userDocument.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val userName = documentSnapshot.getString("name")
                        val userEmail = documentSnapshot.getString("email")
                        val userAddress = documentSnapshot.getString("address")
                        val userLocation = documentSnapshot.getString("location")

                        nameEditText.setText(userName)
                        emailEditText.setText(userEmail)

                        if (!userAddress.isNullOrEmpty()) {
                            moradaEditText.setText(userAddress)
                        }

                        if (!userLocation.isNullOrEmpty()) {
                            val position = (spinnerLocation.adapter as ArrayAdapter<String>).getPosition(userLocation)
                            spinnerLocation.setSelection(position)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                }
        }
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(context, "Sessão terminada", Toast.LENGTH_SHORT).show()
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}