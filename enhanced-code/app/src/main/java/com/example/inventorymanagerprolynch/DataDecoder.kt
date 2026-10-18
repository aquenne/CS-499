package com.example.inventorymanagerprolynch

import org.json.JSONObject
import java.util.zip.CRC32

object DataDecoder {

    /**
     * Algorithmic Data Decoding Pipeline:
     * 1. Validates byte array data integrity using CRC32 checksum.
     * 2. Decodes raw byte payload into UTF-8 text string.
     * 3. Parses structured JSON string into an InventoryItem.
     */
    fun decodeAndValidatePayload(rawBytes: ByteArray, expectedChecksum: Long): Result<InventoryItem> {
        return try {
            // Checksum Verification Algorithm
            val crc = CRC32()
            crc.update(rawBytes)
            val calculatedChecksum = crc.value

            if (calculatedChecksum != expectedChecksum) {
                return Result.failure(IllegalArgumentException("Checksum validation failed: Data corrupted."))
            }

            // String Byte Stream Decoding
            val jsonString = String(rawBytes, Charsets.UTF_8)

            val id = extractInt(jsonString, "id")
            val name = extractString(jsonString, "name")
            val quantity = extractInt(jsonString, "quantity")
            val price = extractDouble(jsonString, "price")

            val item = InventoryItem(id = id, name = name, quantity = quantity, price = price)
            Result.success(item)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Helper algorithm to compute a CRC32 checksum for a given byte payload.
     */
    fun computeChecksum(bytes: ByteArray): Long {
        val crc = CRC32()
        crc.update(bytes)
        return crc.value
    }

    private fun extractString(json: String, key: String): String {
        val pattern = "\"$key\"\\s*:\\s*\"([^\"]*)\"".toRegex()
        return pattern.find(json)?.groupValues?.get(1)
            ?: throw IllegalArgumentException("Missing string key: $key")
    }

    private fun extractInt(json: String, key: String): Int {
        val pattern = "\"$key\"\\s*:\\s*([0-9]+)".toRegex()
        return pattern.find(json)?.groupValues?.get(1)?.toInt()
            ?: throw IllegalArgumentException("Missing int key: $key")
    }

    private fun extractDouble(json: String, key: String): Double {
        val pattern = "\"$key\"\\s*:\\s*([0-9.]+)".toRegex()
        return pattern.find(json)?.groupValues?.get(1)?.toDouble()
            ?: throw IllegalArgumentException("Missing double key: $key")
    }
}