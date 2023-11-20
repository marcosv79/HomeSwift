package pt.ipca.hs

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PlaceOrderActivity : AppCompatActivity() {

    private lateinit var dateOrder: EditText
    private val calendar = Calendar.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_order)

        dateOrder = findViewById(R.id.et_date_placeOrder)
        dateOrder.setOnClickListener {
            showDatePickerDialog()
        }

        val providerName = intent.getStringExtra("name")
        val providerService = intent.getStringExtra("service")
        val providerCost = intent.getStringExtra("cost")

        val name = findViewById<EditText>(R.id.et_providerName_placeOrder)
        val service = findViewById<EditText>(R.id.et_service_placeOrder)
        val cost = findViewById<EditText>(R.id.et_cost_placeOrder)

        name.setText(providerName)
        service.setText(providerService)
        cost.setText("$providerCost €")

        name.isEnabled = false
        service.isEnabled = false
        cost.isEnabled = false
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                updateEditTextDate(selectedDate)
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
    }
}