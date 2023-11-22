package pt.ipca.hs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class OrderAdapterP(private val orders: List<Order>, private val users: List<User>, private val cancelOrderListener: (Int) -> Unit) : RecyclerView.Adapter<OrderAdapterP.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_orderp, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]

        val clientId = order.idClient
        val client = users.find { it.id == clientId }

        holder.textClientName.text = "${client?.name}"
        holder.textAddress.text = "${client?.address}"
        holder.textDate.text = "${order.date}"
        holder.textCost.text = "${order.cost}"
        holder.textTypeService.text = "${order.typeService}"
        holder.textStatus.text = "${order.status}"

        holder.btnCancelOrder.setOnClickListener {
            cancelOrderListener.invoke(order.id)
            Toast.makeText(holder.itemView.context, "Pedido Cancelado", Toast.LENGTH_SHORT).show()        }
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textAddress: TextView = itemView.findViewById(R.id.textAddress)
        val textDate: TextView = itemView.findViewById(R.id.textDate)
        val textCost: TextView = itemView.findViewById(R.id.textCost)
        val textTypeService: TextView = itemView.findViewById(R.id.textTypeService)
        val textStatus: TextView = itemView.findViewById(R.id.textStatus)
        val textClientName: TextView = itemView.findViewById(R.id.textClientName)
        val btnCancelOrder: Button = itemView.findViewById(R.id.btnCancelOrder)
    }
}
