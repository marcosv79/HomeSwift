package pt.ipca.hs

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ampHomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ampHomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var myDatabase: MyDatabase
    private lateinit var emailEditText: EditText
    private lateinit var spinnerService: Spinner
    private lateinit var costEditText: EditText
    private lateinit var btnGuardar: Button

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
        val rootView = inflater.inflate(R.layout.fragment_amp_home, container, false)
        val name_tv = rootView.findViewById<TextView>(R.id.welcome_provider_tv)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        myDatabase = MyDatabase.invoke(requireContext())
        emailEditText = rootView.findViewById(R.id.et_email_amp_home)
        spinnerService = rootView.findViewById(R.id.spinner_service_amp_home)
        costEditText = rootView.findViewById(R.id.et_cost_amp_home)
        btnGuardar = rootView.findViewById(R.id.btn_save_amp_home)

        emailEditText.isVisible = false

        loadProviderData()

        btnGuardar.setOnClickListener{
            saveNewData()
        }

        val name = arguments?.getString("name")

        if (name != null) {
            name_tv.text = "Olá, $name"
        }

        return rootView
    }

    private fun saveNewData() {
        Thread {
            val userDao = myDatabase.userDao()

            val existingUser = userDao.findByEmail(emailEditText.text.toString())

            if (existingUser != null) {
                val selectedService = spinnerService.selectedItem.toString()
                val cost = costEditText.text.toString()

                if (selectedService.isNotEmpty()) {
                    existingUser.service = selectedService
                } else {
                    activity?.runOnUiThread {
                        Toast.makeText(context, "Preencha o serviço", Toast.LENGTH_SHORT).show()
                    }
                    return@Thread
                }

                if (cost.isNotEmpty()) {
                    existingUser.cost = cost
                } else {
                    activity?.runOnUiThread {
                        Toast.makeText(context, "Preencha o custo", Toast.LENGTH_SHORT).show()
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
        val selectedService = spinnerService.selectedItem.toString()
        val cost = costEditText.text.toString()

        val userCollection = firestore.collection("users")
        val query = userCollection.whereEqualTo("email", email)

        query.get().addOnSuccessListener { documents ->
            for(document in documents){
                document.reference.update(
                    "service", selectedService,
                    "cost", cost
                ).addOnSuccessListener {
                }
            }
        }
    }

    private fun loadProviderData() {
        val userEmail = getEmailFromArguments()

        if (!userEmail.isNullOrEmpty()) {

            AsyncTask.execute{
                val userDao = myDatabase.userDao()
                val user = userDao.findByEmail(userEmail)

                activity?.runOnUiThread{
                    user?.let{
                        val userService = it.service
                        val userCost = it.cost

                        emailEditText.setText(userEmail)

                        if(!userService.isNullOrEmpty()){
                            val adapter = spinnerService.adapter as ArrayAdapter<String>
                            val position = adapter.getPosition(userService)
                            spinnerService.setSelection(position)
                        }

                        if(!userCost.isNullOrEmpty()){
                            costEditText.setText(userCost)
                        }
                    }
                }
            }
        }
    }

    private fun getEmailFromArguments(): String? {
        return arguments?.getString("email")
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ampHomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: Int, param2: Int) =
            ampHomeFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                    putInt(ARG_PARAM2, param2)
                }
            }
    }
}