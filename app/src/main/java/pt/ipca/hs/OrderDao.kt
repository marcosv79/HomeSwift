    package pt.ipca.hs

    import androidx.room.Dao
    import androidx.room.Insert
    import androidx.room.Query

    @Dao
    interface OrderDao {
        @Query("SELECT * FROM orders")
        fun getAll(): List<Order>

        @Query("SELECT * FROM orders WHERE idClient = :idClient")
        fun getOrdersByClientId(idClient: Int): List<Order>

        @Query("SELECT * FROM orders WHERE idProvider = :idProvider")
        fun getOrdersByProviderId(idProvider: Int): List<Order>

        @Query("SELECT * FROM orders WHERE id = :id")
        fun findById(id: Int): Order

        @Query("SELECT * FROM orders WHERE idProvider = :providerId AND date = :orderDate")
        fun findOrderByProviderAndDate(providerId: Int, orderDate: String): Order?

        @Insert
        fun insertOrder(vararg order: Order)

        @Query("DELETE FROM orders WHERE id = :id")
        fun deleteOrderById(id: Int)

        @Query("UPDATE orders SET status = :newStatus WHERE id = :orderId")
        fun updateOrderStatus(orderId: Int, newStatus: String)

        @Query("UPDATE orders SET evalStar = :evalStar, evalComment = :evalComment WHERE id = :orderId")
        fun updateOrderEvaluation(orderId: Int, evalStar: Int, evalComment: String)

        @Query("SELECT * FROM orders WHERE idProvider = :providerId AND status = :status")
        fun getOrdersByProviderAndStatus(providerId: Int, status: String): List<Order>

        @Query("SELECT * FROM orders WHERE idClient = :clientId AND status = :status")
        fun getOrdersByClientAndStatus(clientId: Int, status: String): List<Order>

        @Query("SELECT evalStar FROM orders WHERE id = :id AND evalStar > 0")
        fun getOrderEvaluation(id: Int): Int?
    }