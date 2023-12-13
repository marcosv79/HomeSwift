package pt.ipca.hs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AllReviewsAdapter(private val reviews: List<Order>, private val onDeleteClickListener: (Order) -> Unit) : RecyclerView.Adapter<AllReviewsAdapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_all_reviews, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val order = reviews[position]

        holder.commentTextView.text = order.evalComment
        holder.ratingBar.rating = order.evalStar.toFloat()

        holder.btnDelete.setOnClickListener {
            onDeleteClickListener(order)
        }
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commentTextView: TextView = itemView.findViewById(R.id.commentTextView)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }
}
