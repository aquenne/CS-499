package com.example.inventorymanagerprolynch

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "inventory_manager.db"
        private const val DATABASE_VERSION = 2 // Incremented to handle users table addition safely

        // Inventory Table Constants
        const val TABLE_INVENTORY = "inventory"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_QUANTITY = "quantity"
        const val COLUMN_PRICE = "price"

        // Users Table Constants (Added for Project Three login & registration functionality)
        const val TABLE_USERS = "users"
        const val COLUMN_USER_ID = "user_id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"
    }

    // Called when the SQLite database is created for the first time
    override fun onCreate(db: SQLiteDatabase?) {
        // SQL query string to create the inventory table with columns for id, name, quantity, and price
        val createInventoryTable = ("CREATE TABLE " + TABLE_INVENTORY + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_QUANTITY + " INTEGER, "
                + COLUMN_PRICE + " REAL)")
        // SQL query string to create the users table with columns for user id, unique username, and password
        val createUsersTable = ("CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USERNAME + " TEXT UNIQUE, "
                + COLUMN_PASSWORD + " TEXT)")

        // Execute the SQL statements to build both tables in the database
        db?.execSQL(createInventoryTable)
        db?.execSQL(createUsersTable)
    }

    // Called when the database needs to be upgraded or version number changes
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Drop existing tables if they already exist to prevent duplicate table creation errors
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_INVENTORY")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        // Recreate the tables from scratch using the onCreate method
        onCreate(db)
    }

    // --- User Authentication & Registration Methods ---

    // Registers a new user into the database table
    fun registerUser(username: String, password: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
        }
        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return result != -1L
    }

    // Checks credentials against stored records during user login attempts
    fun checkUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_ID),
            "$COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(username, password),
            null, null, null
        )
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    // --- Inventory CRUD Operations ---

    // Insert a new inventory item
    fun addItem(name: String, quantity: Int, price: Double): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_QUANTITY, quantity)
            put(COLUMN_PRICE, price)
        }
        val result = db.insert(TABLE_INVENTORY, null, values)
        db.close()
        return result != -1L
    }

    // Read all inventory items
    fun getAllItems(): List<InventoryItem> {
        val itemList = mutableListOf<InventoryItem>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_INVENTORY", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY))
                val price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE))

                itemList.add(InventoryItem(id, name, quantity, price))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return itemList
    }

    // Update an existing inventory item (Added for full CRUD compliance)
    fun updateItem(id: Int, name: String, quantity: Int, price: Double): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_QUANTITY, quantity)
            put(COLUMN_PRICE, price)
        }
        val rowsAffected = db.update(TABLE_INVENTORY, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsAffected > 0
    }

    // Delete an inventory item by ID
    fun deleteItem(id: Int): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_INVENTORY, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return result
    }
}