package com.example.contacts_phase1.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * The app's Room database: two tables (contacts and contact_groups) and one DAO.
 * Version 2 added the "Other" group (see [MIGRATION_1_2]).
 */
@Database(entities = [Contact::class, Group::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ContactsDatabase : RoomDatabase() {

    abstract fun contactDao(): ContactDao

    companion object {
        // @Volatile: every thread sees the newest value of INSTANCE straight away.
        @Volatile
        private var INSTANCE: ContactsDatabase? = null

        /**
         * Runs once on phones that already have version 1, when the app is updated.
         * It only adds the new group, so every saved contact is kept.
         * (Without a migration Room would stop with an error, or wipe the data if told to.)
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "INSERT INTO contact_groups (name) " +
                        "SELECT 'Other' WHERE NOT EXISTS (SELECT 1 FROM contact_groups WHERE name = 'Other')"
                )
            }
        }

        /** Returns the single database instance, creating it the first time (singleton). */
        fun getDatabase(context: Context): ContactsDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ContactsDatabase::class.java,
                    "contacts_database",
                )
                    .addCallback(SeedGroups)
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
    }

    /** Adds the starting groups when the database file is first created (new installs). */
    private object SeedGroups : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            db.execSQL(
                "INSERT INTO contact_groups (name) VALUES ('Family'), ('Work'), ('University'), ('Other')"
            )
        }
    }
}
