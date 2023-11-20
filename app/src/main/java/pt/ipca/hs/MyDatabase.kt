package pt.ipca.hs

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [User::class, Service::class], version = 2)
abstract class MyDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun serviceDao(): ServiceDao

    companion object {
        @Volatile
        private var instance: MyDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context,
                MyDatabase::class.java,
                "homeswift.db"
            ).addMigrations(MIGRATION_1_2)
                .build()
    }
}

private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE users_temp (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, email TEXT, password TEXT, userType TEXT, address TEXT, location TEXT, service TEXT, cost TEXT)")
        database.execSQL("INSERT INTO users_temp (id, name, email, password, userType, address, location, service, cost) SELECT id, name, email, password, userType, address, location, service, cost FROM users")
        database.execSQL("DROP TABLE users")
        database.execSQL("ALTER TABLE users_temp RENAME TO users")
    }
}
