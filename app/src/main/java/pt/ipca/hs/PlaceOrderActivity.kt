package pt.ipca.hs

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PlaceOrderActivity : AppCompatActivity() {

    private lateinit var dateOrder: EditText
    private lateinit var myDatabase: MyDatabase
    private lateinit var typeService: EditText

    private val calendar = Calendar.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_order)

        myDatabase = MyDatabase.invoke(this)

        dateOrder = findViewById(R.id.et_date_placeOrder)
        dateOrder.setOnClickListener {
            showDatePickerDialog()
        }

        val providerName = intent.getStringExtra("name")
        val providerService = intent.getStringExtra("service")
        val providerCost = intent.getStringExtra("cost")
        val providerId = intent.getIntExtra("idProvider", 0)
        val userId = intent.getIntExtra("id", 0)
        typeService = findViewById(R.id.et_typeService_placeOrder)
        val name = findViewById<EditText>(R.id.et_providerName_placeOrder)
        val service = findViewById<EditText>(R.id.et_service_placeOrder)
        val cost = findViewById<EditText>(R.id.et_cost_placeOrder)
        val description = findViewById<EditText>(R.id.et_description_placeOrder)
        name.setText(providerName)
        service.setText(providerService)
        cost.setText("$providerCost €")
        name.isEnabled = false
        service.isEnabled = false
        cost.isEnabled = false

        val btnPlaceOrder = findViewById<Button>(R.id.btn_placeOrder)
        btnPlaceOrder.setOnClickListener {
            placeOrder()
        }
    }

    private fun placeOrder() {
        Thread {
            val orderDao = myDatabase.orderDao()

            val service = findViewById<EditText>(R.id.et_service_placeOrder).text.toString()
            val description = findViewById<EditText>(R.id.et_description_placeOrder).text.toString()
            val date = findViewById<EditText>(R.id.et_date_placeOrder).text.toString()
            val typeService = typeService.text.toString()
            val cost = findViewById<EditText>(R.id.et_cost_placeOrder).text.toString()
            val userId = intent.getIntExtra("id", 0)
            val providerId = intent.getIntExtra("idProvider", 0)

            if (date.isNullOrEmpty()) {
                runOnUiThread {
                    Toast.makeText(this, "Escolha uma data", Toast.LENGTH_SHORT).show()
                }
                return@Thread
            }

            val existingOrder = orderDao.findOrderByProviderAndDate(providerId, date)
            if (existingOrder == null) {
                val order = Order(
                    idClient = userId,
                    idProvider = providerId,
                    service = service,
                    description = description,
                    date = date,
                    typeService = typeService,
                    status = "Ativo",
                    cost = cost,
                    evalStar = 0,
                    evalComment = ""
                )

                orderDao.insertOrder(order)

                runOnUiThread {
                    Toast.makeText(this, "Pedido efetuado com sucesso", Toast.LENGTH_SHORT).show()
                }
            } else {
                runOnUiThread {
                    Toast.makeText(this, "Não é possível fazer este pedido", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)

                if (isValidDay(selectedDate)) {
                    updateEditTextDate(selectedDate)
                } else {
                    Toast.makeText(this, "Selecione um dia válido", Toast.LENGTH_SHORT).show()
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.datePicker.minDate = System.currentTimeMillis() + 86400000
        datePicker.show()
    }

    private fun updateEditTextDate(calendar: Calendar) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateOrder.setText(dateFormat.format(calendar.time))

        val today = Calendar.getInstance()
        val nextMonth = Calendar.getInstance()
        nextMonth.add(Calendar.MONTH, 1)

        if (calendar.after(today) && calendar.before(nextMonth)) {
            typeService.setText("Urgente")
        } else {
            typeService.setText("Normal")
        }
    }

    private fun isValidDay(date: Calendar): Boolean {
        return date.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && !isChristmasDay(date) && !isNewYearDay(date)
    }

    private fun isChristmasDay(date: Calendar): Boolean {
        return date.get(Calendar.MONTH) == Calendar.DECEMBER && date.get(Calendar.DAY_OF_MONTH) == 25
    }

    private fun isNewYearDay(date: Calendar): Boolean {
        return date.get(Calendar.MONTH) == Calendar.JANUARY && date.get(Calendar.DAY_OF_MONTH) == 1
    }
}