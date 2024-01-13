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

        @Query("SELECT * FROM orders WHERE idProvider = :providerId AND date = :orderDate AND hour = :hourDate")
        fun findOrderByProviderDateHour(providerId: Int, orderDate: String, hourDate: String): Order?

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

        @Query("UPDATE orders SET evalComment = :evalComment WHERE id = :orderId")
        fun updateEvalComment(orderId: Int, evalComment: String)

        @Query("SELECT * FROM orders WHERE evalComment IS NOT NULL and evalComment != ''")
        fun getReviewsWithComments(): List<Order>

        @Query("SELECT idProvider, COUNT(*) as serviceCount FROM orders GROUP BY idProvider ORDER BY serviceCount DESC LIMIT 3")
        fun getTopProviders(): List<TopProvider>

        @Query("SELECT service, COUNT(*) as serviceCount FROM orders GROUP BY service ORDER BY serviceCount DESC LIMIT 3")
        fun getTopServices(): List<TopService>

        @Query("SELECT idClient, COUNT(*) as serviceCount FROM orders GROUP BY idClient ORDER BY serviceCount DESC LIMIT 3")
        fun getTopClients(): List<TopClient>

    }