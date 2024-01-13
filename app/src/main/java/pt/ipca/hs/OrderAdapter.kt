package pt.ipca.hs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class OrderAdapter(private val orders: List<Order>,
                   private val users: List<User>,
                   private val cancelOrderListener: (Int) -> Unit,
                   private val finishOrderListener: (Int) -> Unit,
                   private val viewMoreListener: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]

        val providerId = order.idProvider
        val provider = users.find { it.id == providerId }

        holder.textProviderName.text = "${provider?.name}"
        holder.textService.text = "${order.service}"
        holder.textDate.text = "${order.date} - ${order.hour}"
        holder.textCost.text = "${order.cost}"
        holder.textTypeService.text = "${order.typeService}"
        holder.textStatus.text = "${order.status}"

        holder.btnCancelOrder.visibility = if (order.status == "Concluído") View.GONE else View.VISIBLE

        holder.btnCancelOrder.setOnClickListener {
            cancelOrderListener.invoke(order.id)
            Toast.makeText(holder.itemView.context, "Pedido cancelado", Toast.LENGTH_SHORT).show()        }

        holder.seeMoreTv.setOnClickListener {
            viewMoreListener.invoke(order)
        }
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textService: TextView = itemView.findViewById(R.id.textService)
        val textDate: TextView = itemView.findViewById(R.id.textDate)
        val textCost: TextView = itemView.findViewById(R.id.textCost)
        val textTypeService: TextView = itemView.findViewById(R.id.textTypeService)
        val textStatus: TextView = itemView.findViewById(R.id.textStatus)
        val textProviderName: TextView = itemView.findViewById(R.id.textProviderName)
        val seeMoreTv: ImageView = itemView.findViewById(R.id.textSeeMore)
        val btnCancelOrder: Button = itemView.findViewById(R.id.btnCancelOrder)
    }
}
