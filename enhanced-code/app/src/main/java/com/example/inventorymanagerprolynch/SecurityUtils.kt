package com.example.inventorymanagerprolynch

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

object SecurityUtils {

    /**
     * Generates a cryptographically secure random salt.
     */
    fun generateSalt(): String {
        val random = SecureRandom()
        val saltBytes = ByteArray(16)
        random.nextBytes(saltBytes)
        return Base64.getEncoder().encodeToString(saltBytes)
    }

    /**
     * Computes a SHA-256 hash for a password combined with a salt.
     */
    fun hashPassword(password: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val combined = password + salt
        val hashBytes = digest.digest(combined.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(hashBytes)
    }

    /**
     * Verifies a password input against a stored hash and salt.
     */
    fun verifyPassword(passwordInput: String, storedHash: String, salt: String): Boolean {
        val computedHash = hashPassword(passwordInput, salt)
        return computedHash == storedHash
    }
}