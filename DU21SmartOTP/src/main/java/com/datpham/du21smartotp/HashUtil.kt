package com.datpham.du21smartotp

import android.util.Log
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class HashUtil {

    companion object {

        private val STEAMCHARS = charArrayOf(
            '2', '3', '4', '5', '6', '7', '8', '9', 'B', 'C',
            'D', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q',
            'R', 'T', 'V', 'W', 'X', 'Y'
        )

        fun sha256String(source: String): String {
            var hash: ByteArray? = null
            var hashCode: String? = null
            try {
                val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
                hash = digest.digest(source.toByteArray())
            } catch (e: NoSuchAlgorithmException) {
                Log.wtf("KAD", "Can't calculate SHA-256")
            }
            if (hash != null) {
                val hashBuilder = StringBuilder()
                for (i in hash.indices) {
                    val hex = Integer.toHexString(hash[i].toInt())
                    if (hex.length == 1) {
                        hashBuilder.append("0")
                        hashBuilder.append(hex[hex.length - 1])
                    } else {
                        hashBuilder.append(hex.substring(hex.length - 2))
                    }
                }
                hashCode = hashBuilder.toString()
            }
            return hashCode ?: ""
        }
    }

}

infix fun Byte.shl(that: Int): Int = this.toInt().shl(that)
infix fun Int.shl(that: Byte): Int =
    this.shl(that.toInt()) // Not necessary in this case because no there's (Int shl Byte)

infix fun Byte.shl(that: Byte): Int =
    this.toInt().shl(that.toInt()) // Not necessary in this case because no there's (Byte shl Byte)

infix fun Byte.and(that: Int): Int = this.toInt().and(that)
infix fun Int.and(that: Byte): Int =
    this.and(that.toInt()) // Not necessary in this case because no there's (Int and Byte)

infix fun Byte.and(that: Byte): Int =
    this.toInt().and(that.toInt())