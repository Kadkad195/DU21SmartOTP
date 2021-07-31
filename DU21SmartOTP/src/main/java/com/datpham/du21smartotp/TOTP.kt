package com.datpham.du21smartotp

import java.lang.reflect.UndeclaredThrowableException
import java.math.BigInteger
import java.security.GeneralSecurityException
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


/**
 * This is an example implementation of the OATH
 * TOTP algorithm.
 * Visit www.openauthentication.org for more information.
 *
 * @author Johan Rydell, PortWise, Inc.
 */
class TOTP {
    /**
     * This method uses the JCE to provide the crypto algorithm.
     * HMAC computes a Hashed Message Authentication Code with the
     * crypto hash algorithm as a parameter.
     *
     * @param crypto: the crypto algorithm (HmacSHA1, HmacSHA256,
     * HmacSHA512)
     * @param keyBytes: the bytes to use for the HMAC key
     * @param text: the message or text to be authenticated
     */
    private fun hmac_sha(
        crypto: String, keyBytes: ByteArray,
        text: ByteArray
    ): ByteArray {
        try {
            val hmac: Mac
            hmac = Mac.getInstance(crypto)
            val macKey = SecretKeySpec(keyBytes, "RAW")
            hmac.init(macKey)
            return hmac.doFinal(text)
        } catch (gse: GeneralSecurityException) {
            throw UndeclaredThrowableException(gse)
        }
    }

    /**
     * This method converts a HEX string to Byte[]
     *
     * @param hex: the HEX string
     *
     * @return: a byte array
     */
    private fun hexStr2Bytes(hex: String): ByteArray {
        // Adding one byte to get the right conversion
        // Values starting with "0" can be converted
        val bArray = BigInteger("10$hex", 16).toByteArray()

        // Copy all the REAL bytes, not the "first"
        val ret = ByteArray(bArray.size - 1)
        for (i in ret.indices) ret[i] = bArray[i + 1]
        return ret
    }

    private val DIGITS_POWER // 0 1  2   3    4     5      6       7        8         9          10
            = longArrayOf(
        1,
        10,
        100,
        1000,
        10000,
        100000,
        1000000,
        10000000,
        100000000,
        1000000000,
        10000000000L
    )

    /**
     * This method generates a TOTP value for the given
     * set of parameters.
     *
     * @param key: the shared secret, HEX encoded
     * @param time: a value that reflects a time
     * @param returnDigits: number of digits to return
     *
     * @return: a numeric String in base 10 that includes
     * [truncationDigits] digits
     */
    fun generateTOTP256(
        key: String,
        time: String,
        returnDigits: String
    ): String {
        return generateTOTP(key, time, returnDigits, "HmacSHA256")
    }

    /**
     * This method generates a TOTP value for the given
     * set of parameters.
     *
     * @param key: the shared secret, HEX encoded
     * @param time: a value that reflects a time
     * @param returnDigits: number of digits to return
     *
     * @return: a numeric String in base 10 that includes
     * [truncationDigits] digits
     */
    fun generateTOTP512(
        key: String,
        time: String,
        returnDigits: String?
    ): String {
        return generateTOTP(key, time, returnDigits, "HmacSHA512")
    }
    /**
     * This method generates a TOTP value for the given
     * set of parameters.
     *
     * @param key: the shared secret, HEX encoded
     * @param time: a value that reflects a time
     * @param returnDigits: number of digits to return
     * @param crypto: the crypto function to use
     *
     * @return: a numeric String in base 10 that includes
     * [truncationDigits] digits
     */
    /**
     * This method generates a TOTP value for the given
     * set of parameters.
     *
     * @param key: the shared secret, HEX encoded
     * @param time: a value that reflects a time
     * @param returnDigits: number of digits to return
     *
     * @return: a numeric String in base 10 that includes
     * [truncationDigits] digits
     */
    @JvmOverloads
    fun generateTOTP(
        key: String,
        time: String,
        returnDigits: String?,
        crypto: String = "HmacSHA1"
    ): String {
        var time = time
        val codeDigits = Integer.decode(returnDigits).toInt()
        var result: String? = null

        // Using the counter
        // First 8 bytes are for the movingFactor
        // Compliant with base RFC 4226 (HOTP)
        while (time.length < 16) time = "0$time"

        // Get the HEX in a Byte[]
        val msg = hexStr2Bytes(time)
        val k = hexStr2Bytes(key)
        val hash = hmac_sha(crypto, k, msg)

        // put selected bytes into result int
        val offset = hash[hash.size - 1] and 0xf
        val binary = hash[offset] and 0x7f shl 24 or
                (hash[offset + 1] and 0xff shl 16) or
                (hash[offset + 2] and 0xff shl 8) or
                (hash[offset + 3] and 0xff)
        val otp = binary % DIGITS_POWER[codeDigits]
        result = java.lang.Long.toString(otp)
        while (result!!.length < codeDigits) {
            result = "0$result"
        }
        return result
    }
}