package com.mifos.shared

interface ICipherUtil {
    @Throws(Exception::class)
    fun generateKeyPair(): CommonKeyPair

    fun getPublicKey(): CommonPublicKey?

    @Throws(Exception::class)
    fun getCrypto(): Crypto

    @Throws(Exception::class)
    suspend fun removePublicKey()
}

expect class CommonKeyPair
expect interface CommonPublicKey
expect class Crypto