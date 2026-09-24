package com.noshitechinc.restaurant.core.auth

interface TokenCipher {
    fun encrypt(plainText: String): String
    fun decrypt(cipherText: String): String?
}
