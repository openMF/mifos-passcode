package com.mifos.shared

import com.mifos.shared.CommonPublicKey

actual data class CommonKeyPair(val publicKey: String?, val privateKey: String?)
actual interface CommonPublicKey {
    val encoded: String
}
actual class Crypto

data class CommonPublicKeyImpl(override val encoded: String): CommonPublicKey