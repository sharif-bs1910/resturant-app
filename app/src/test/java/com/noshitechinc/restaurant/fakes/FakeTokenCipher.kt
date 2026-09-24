package com.noshitechinc.restaurant.fakes

import com.noshitechinc.restaurant.core.auth.TokenCipher

class FakeTokenCipher : TokenCipher {
    override fun encrypt(plainText: String): String = "enc:" + plainText.reversed()

    override fun decrypt(cipherText: String): String? = cipherText.takeIf { it.startsWith("enc:") }?.removePrefix("enc:")?.reversed()
}
