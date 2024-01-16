package pt.ipca.hs.Views

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import pt.ipca.hs.Controllers.LoginActivity
import pt.ipca.hs.Models.Order
import pt.ipca.hs.MyDatabase
import pt.ipca.hs.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PlaceOrderActivity : AppCompatActivity() {

    private lateinit var dateOrder: EditText
    private lateinit var myDatabase: MyDatabase
    private lateinit var typeService: EditText
    private lateinit var cost: EditText
    private lateinit var spinnerHour : Spinner
    private var baseCost: Double = 0.0
    private var costUrgent: Double = 0.0

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
        val name = findViewById<EditText>(R.id.et_providerName_placeOrder)
        name.isEnabled = false

        val providerService = intent.getStringExtra("service")
        val service = findViewById<EditText>(R.id.et_service_placeOrder)
        service.setText(providerService)
        service.isEnabled = false

        val providerCost = intent.getStringExtra("cost")
        cost = findViewById(R.id.et_cost_placeOrder)
        cost.setText("$providerCost €")
        cost.isEnabled = false
        baseCost = cost.text.toString().replace(" €", "").toDouble()
        costUrgent = baseCost * 2

        typeService = findViewById(R.id.et_typeService_placeOrder)

        spinnerHour = findViewById(R.id.spinner_hour_placeOrder)
        val hours = arrayOf("9h00", "11h00", "14h00", "16h00")


        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, hours)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerHour.adapter = adapter

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
            val hour = spinnerHour.selectedItem.toString()

            if (date.isNullOrEmpty()) {
                runOnUiThread {
                    Toast.makeText(this, "Escolha uma data", Toast.LENGTH_SHORT).show()
                }
                return@Thread
            }

            val existingOrder = orderDao.findOrderByProviderDateHour(providerId, date, hour)
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
                    evalComment = "",
                    hour = hour
                )

                orderDao.insertOrder(order)

                runOnUiThread {
                    Toast.makeText(this, "Pedido efetuado com sucesso", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
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
        val twoWeeksLater = Calendar.getInstance()
        twoWeeksLater.add(Calendar.DAY_OF_MONTH, 14)

        if (calendar.after(today) && calendar.before(twoWeeksLater)) {
            typeService.setText("Urgente")
            cost.setText(String.format("%.0f €", costUrgent))
        } else {
            typeService.setText("Normal")
            cost.setText(String.format("%.0f €", baseCost))
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
