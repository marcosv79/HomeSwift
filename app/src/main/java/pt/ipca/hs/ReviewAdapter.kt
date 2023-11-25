package pt.ipca.hs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReviewAdapter(private val orders: List<Order>) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val order = orders[position]

        holder.commentTextView.text = order.evalComment
        holder.ratingBar.rating = order.evalStar.toFloat()
        holder.ratingBar.setOnTouchListener { _, _ -> true }
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //val textClientName: TextView = itemView.findViewById(R.id.textService)
        val commentTextView: TextView = itemView.findViewById(R.id.commentTextView)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
    }
}
