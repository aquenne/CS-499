package com.example.inventorymanagerprolynch

import org.junit.Assert.*
import org.junit.Test

class DataDecoderTest {

    @Test
    fun testValidPayloadChecksumAndDecoding() {
        val jsonPayload = """{"id":101,"name":"Test QR Item","quantity":15,"price":29.99}"""
        val rawBytes = jsonPayload.toByteArray(Charsets.UTF_8)

        val validChecksum = DataDecoder.computeChecksum(rawBytes)
        val result = DataDecoder.decodeAndValidatePayload(rawBytes, validChecksum)

        assertTrue("Expected payload decoding to succeed", result.isSuccess)
        val item = result.getOrNull()
        assertNotNull(item)
        assertEquals(101, item?.id)
        assertEquals("Test QR Item", item?.name)
        assertEquals(15, item?.quantity)
        assertEquals(29.99, item?.price ?: 0.0, 0.01)
    }

    @Test
    fun testCorruptedPayloadChecksumFailure() {
        val jsonPayload = """{"id":102,"name":"Corrupted Item","quantity":5,"price":9.99}"""
        val rawBytes = jsonPayload.toByteArray(Charsets.UTF_8)

        val invalidChecksum = 123456789L
        val result = DataDecoder.decodeAndValidatePayload(rawBytes, invalidChecksum)

        assertTrue("Expected payload decoding to fail on corrupted checksum", result.isFailure)
        assertEquals("Checksum validation failed: Data corrupted.", result.exceptionOrNull()?.message)
    }

    @Test
    fun testHashMapO1Lookup() {
        val map = HashMap<Int, InventoryItem>()
        val item1 = InventoryItem(1, "Widget A", 10, 5.00)
        val item2 = InventoryItem(2, "Widget B", 20, 15.00)

        map[item1.id] = item1
        map[item2.id] = item2

        val retrievedItem = map[2]
        assertNotNull(retrievedItem)
        assertEquals("Widget B", retrievedItem?.name)
    }
}