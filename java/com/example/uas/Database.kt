package com.example.uas

import androidx.room.*
import android.content.Context

// Define your entities here
@Entity
data class Cashier(
    @PrimaryKey val username: String,
    var name: String,
    var password: String
)

@Entity
data class Item(
    @PrimaryKey val sku: String,
    var itemName: String,
    var price: Int,
    var stock: Int,
    var pictureLink: String
)

@Entity
data class LineItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val sku: String,
    val itemName: String,
    val itemPrice: Int,
    var quantity: Int,
    val pictureLink: String
) {
    val totalLineItemPrice: Int get() = itemPrice * quantity
}

@Entity(primaryKeys = ["orderId", "lineItemId"])
data class OrderLineItemCrossRef(
    val orderId: Long,
    val lineItemId: Long
)

@Entity
data class Order(
    @PrimaryKey(autoGenerate = true) val orderId: Long = 0L,
    var cashierUserName: String,
    var customerName: String,
    var paymentType: String,
    var totalPrice: Int
)

data class OrderWithLineItems(
    @Embedded val order: Order,
    @Relation(
        parentColumn = "orderId", // This is the column in Order entity
        entityColumn = "id", // This is the column in LineItem entity
        associateBy = Junction(
            value = OrderLineItemCrossRef::class,
            parentColumn = "orderId", // This is the column in OrderLineItemCrossRef
            entityColumn = "lineItemId" // This is the column in OrderLineItemCrossRef
        )
    )
    val lineItems: List<LineItem>
)


@Dao
interface CashierDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCashier(cashier: Cashier)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cashiers: List<Cashier>)

    @Update
    suspend fun updateCashier(cashier: Cashier)

    @Delete
    suspend fun deleteCashier(cashier: Cashier)

    @Query("SELECT * FROM Cashier WHERE username = :username")
    suspend fun getCashierByUsername(username: String): Cashier?

    @Query("SELECT * FROM Cashier")
    suspend fun getAllCashiers(): List<Cashier>
}

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: Item)

    @Update
    suspend fun updateItem(item: Item)

    @Delete
    suspend fun deleteItem(item: Item)

    @Query("SELECT * FROM Item WHERE sku = :sku")
    suspend fun getItemBySKU(sku: String): Item?

    @Query("SELECT * FROM Item")
    suspend fun getAllItems(): List<Item>
}

@Dao
interface LineItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLineItem(lineItem: LineItem)

    @Update
    suspend fun updateLineItem(lineItem: LineItem)

    @Delete
    suspend fun deleteLineItem(lineItem: LineItem)

    @Query("SELECT * FROM LineItem WHERE id = :id")
    suspend fun getLineItemById(id: Int): LineItem?

    @Query("SELECT * FROM LineItem")
    suspend fun getAllLineItems(): List<LineItem>
}

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderLineItemCrossRef(crossRef: OrderLineItemCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLineItem(lineItem: LineItem): Long

    @Update
    suspend fun updateOrder(order: Order)

    @Delete
    suspend fun deleteOrder(order: Order)

    @Query("SELECT * FROM `Order` WHERE orderId = :orderId")
    suspend fun getOrderById(orderId: Long): Order?

    @Query("SELECT * FROM `Order`")
    suspend fun getAllOrders(): List<Order>

    @Transaction
    @Query("SELECT * FROM `Order`")
    suspend fun getAllOrdersWithLineItems(): List<OrderWithLineItems>
}

// Define the Room database
@Database(entities = [Cashier::class, Item::class, LineItem::class, Order::class, OrderLineItemCrossRef::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cashierDao(): CashierDao
    abstract fun itemDao(): ItemDao
    abstract fun lineItemDao(): LineItemDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration() // Enable destructive migration
                    .build()
                instance = newInstance
                newInstance
            }
        }
    }
}
