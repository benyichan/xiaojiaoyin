package com.xiaojiaoyin.baby.domain

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

data class LicenseInfo(
    val plan: String,      // "M" 月费 / "Y" 年度 / "L" 永久
    val expireAt: Long     // 0 表示永久
)

/**
 * 离线激活码：
 * payload = "XY" + deviceId(4B) + plan(1B) + expireTs(4B)
 * sig = HMAC-SHA256(payload)[0..3]
 * code = base32(payload + sig)，24 字符，展示为 XXXX-XXXX-XXXX-XXXX
 *
 * 说明：密钥内置在客户端，可被逆向；完全离线下属于"防误操作级"保护，
 * 商用如需强防伪应改为服务器验证。
 */
object License {
    // 正式发布前应更换为随机密钥，并与生成脚本保持一致
    const val SECRET = "xiaojiaoyin-license-v1-2026"
    const val MAGIC = "XY"

    private val ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"

    fun generateCode(deviceIdBytes: ByteArray, plan: String, expireAt: Long): String {
        require(deviceIdBytes.size == 4) { "deviceId 必须为 4 字节" }
        require(plan == "M" || plan == "Y" || plan == "L") { "plan 仅支持 M/Y/L" }
        val payload = MAGIC.toByteArray(Charsets.US_ASCII) +
            deviceIdBytes +
            plan.toByteArray(Charsets.US_ASCII) +
            intToBytes(expireAt.toInt())
        val sig = hmac(payload).copyOf(4)
        val raw = payload + sig
        val encoded = base32Encode(raw)
        // 分组：4 组 6 字符
        return buildString {
            for (i in 0 until encoded.length) {
                if (i > 0 && i % 6 == 0) append('-')
                append(encoded[i])
            }
        }
    }

    fun verifyCode(code: String, deviceIdBytes: ByteArray): LicenseInfo? {
        val normalized = code.uppercase().replace("-", "").replace(" ", "")
        val raw = base32Decode(normalized) ?: return null
        if (raw.size != 15) return null
        val payload = raw.copyOfRange(0, 11)
        val sig = raw.copyOfRange(11, 15)
        val expectedSig = hmac(payload).copyOf(4)
        if (!sig.contentEquals(expectedSig)) return null
        if (payload.size < 11) return null
        if (payload[0] != MAGIC.toByteArray(Charsets.US_ASCII)[0] ||
            payload[1] != MAGIC.toByteArray(Charsets.US_ASCII)[1]
        ) return null
        val codeDevice = payload.copyOfRange(2, 6)
        if (!codeDevice.contentEquals(deviceIdBytes)) return null
        val plan = payload[6].toInt().toChar().toString()
        val expire = bytesToInt(payload.copyOfRange(7, 11))
        if (plan != "L" && expire <= System.currentTimeMillis() / 1000) return null
        return LicenseInfo(
            plan = plan,
            expireAt = if (plan == "L") 0 else expire * 1000L
        )
    }

    fun hmac(data: ByteArray): ByteArray {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(SECRET.toByteArray(Charsets.UTF_8), "HmacSHA256"))
        return mac.doFinal(data)
    }

    private fun intToBytes(value: Int): ByteArray = byteArrayOf(
        (value ushr 24).toByte(),
        (value ushr 16).toByte(),
        (value ushr 8).toByte(),
        value.toByte()
    )

    private fun bytesToInt(bytes: ByteArray): Int =
        ((bytes[0].toInt() and 0xFF) shl 24) or
            ((bytes[1].toInt() and 0xFF) shl 16) or
            ((bytes[2].toInt() and 0xFF) shl 8) or
            (bytes[3].toInt() and 0xFF)

    fun base32Encode(data: ByteArray): String {
        val sb = StringBuilder()
        var buffer = 0
        var bits = 0
        data.forEach { b ->
            buffer = (buffer shl 8) or (b.toInt() and 0xFF)
            bits += 8
            while (bits >= 5) {
                sb.append(ALPHABET[(buffer ushr (bits - 5)) and 0x1F])
                bits -= 5
            }
        }
        if (bits > 0) {
            sb.append(ALPHABET[(buffer shl (5 - bits)) and 0x1F])
        }
        return sb.toString()
    }

    fun base32Decode(text: String): ByteArray? {
        var buffer = 0
        var bits = 0
        val out = mutableListOf<Byte>()
        text.forEach { c ->
            val index = ALPHABET.indexOf(c)
            if (index < 0) return null
            buffer = (buffer shl 5) or index
            bits += 5
            if (bits >= 8) {
                out.add(((buffer ushr (bits - 8)) and 0xFF).toByte())
                bits -= 8
            }
        }
        return out.toByteArray()
    }
}
