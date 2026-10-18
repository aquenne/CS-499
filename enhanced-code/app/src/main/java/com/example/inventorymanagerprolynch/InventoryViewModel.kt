package com.example.inventorymanagerprolynch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InventoryViewModel(private val dbHelper: DatabaseHelper) : ViewModel() {

    private val _itemList = MutableStateFlow<List<InventoryItem>>(emptyList())
    val itemList: StateFlow<List<InventoryItem>> = _itemList.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // O(1) HashMap Cache Index for fast constant-time entity lookup
    private val itemHashMap = HashMap<Int, InventoryItem>()

    // State flow for scan error/success feedback messaging
    private val _scanResult = MutableStateFlow<String>("")
    val scanResult: StateFlow<String> = _scanResult.asStateFlow()

    init {
        loadItems()
    }

    /**
     * Loads items from SQLite DB and populates the O(1) HashMap lookup table.
     */
    fun loadItems() {
        viewModelScope.launch {
            val itemsFromDb = dbHelper.getAllItems()
            _itemList.value = itemsFromDb

            // Rebuild HashMap lookup table in O(n) time once during load
            itemHashMap.clear()
            for (item in itemsFromDb) {
                itemHashMap[item.id] = item
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    /**
     * O(1) Constant Time Lookup Algorithm:
     * Resolves an item ID directly from memory without linear O(n) list iteration.
     */
    fun getItemById(id: Int): InventoryItem? {
        return itemHashMap[id]
    }

    /**
     * Scanned Payload Processing Algorithm:
     * Validates raw frame bytes, computes CRC32 checksum, parses JSON, and caches result in O(1) HashMap.
     */
    fun processScannedPayload(rawBytes: ByteArray, expectedChecksum: Long) {
        viewModelScope.launch {
            val decodeResult = DataDecoder.decodeAndValidatePayload(rawBytes, expectedChecksum)
            decodeResult.onSuccess { item ->
                // O(1) insertion into HashMap memory cache
                itemHashMap[item.id] = item

                // Update persistent storage
                if (dbHelper.addItem(item.name, item.quantity, item.price)) {
                    loadItems()
                    _scanResult.value = "Successfully scanned & cached: ${item.name}"
                }
            }.onFailure { error ->
                _scanResult.value = "Scan error: ${error.message}"
            }
        }
    }

    fun addNewItem(name: String, quantity: Int, price: Double) {
        viewModelScope.launch {
            if (dbHelper.addItem(name, quantity, price)) {
                loadItems()
            }
        }
    }

    fun deleteItem(id: Int) {
        viewModelScope.launch {
            if (dbHelper.deleteItem(id) > 0) {
                // Remove from O(1) HashMap cache directly
                itemHashMap.remove(id)
                loadItems()
            }
        }
    }

    fun clearScanResult() {
        _scanResult.value = ""
    }
}