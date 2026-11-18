package com.example.contacts_phase1.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase

/** The app's Room database: two tables (contacts and contact_groups) and one DAO. */
@Database(entities = [Contact::class, Group::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ContactsDatabase : RoomDatabase() {

    abstract fun contactDao(): ContactDao

    companion object {
        // @Volatile: every thread sees the newest value of INSTANCE straight away.
        @Volatile
        private var INSTANCE: ContactsDatabase? = null

        /** Returns the single database instance, creating it the first time (singleton). */
        fun getDatabase(context: Context): ContactsDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ContactsDatabase::class.java,
                    "contacts_database",
                )
                    .addCallback(SeedGroups)
                    .build()
                    .also { INSTANCE = it }
            }
    }

    /** Adds the three starting groups when the database file is first created. */
    private object SeedGroups : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            db.execSQL(
                "INSERT INTO contact_groups (name) VALUES ('Family'), ('Work'), ('University')"
            )
        }
    }
}
