package com.xiaojiaoyin.baby.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class LicenseTest {

    private val deviceId = byteArrayOf(0x12, 0x34, 0x56, 0x78)

    @Test
    fun `永久激活码生成验证往返`() {
        val code = License.generateCode(deviceId, "L", 0)
        val info = License.verifyCode(code, deviceId)
        assertNotNull(info)
        assertEquals("L", info!!.plan)
        assertEquals(0, info.expireAt)
    }

    @Test
    fun `月度激活码带到期时间`() {
        val expire = System.currentTimeMillis() / 1000 + 30 * 24 * 3600
        val code = License.generateCode(deviceId, "M", expire)
        val info = License.verifyCode(code, deviceId)
        assertNotNull(info)
        assertEquals("M", info!!.plan)
        assertEquals(expire * 1000, info.expireAt)
    }

    @Test
    fun `年度激活码带到期时间`() {
        val expire = System.currentTimeMillis() / 1000 + 365 * 24 * 3600
        val code = License.generateCode(deviceId, "Y", expire)
        val info = License.verifyCode(code, deviceId)
        assertNotNull(info)
        assertEquals("Y", info!!.plan)
        assertEquals(expire * 1000, info.expireAt)
    }

    @Test
    fun `其他设备无法激活`() {
        val code = License.generateCode(deviceId, "L", 0)
        val otherDevice = byteArrayOf(0xAA.toByte(), 0xBB.toByte(), 0xCC.toByte(), 0xDD.toByte())
        assertNull(License.verifyCode(code, otherDevice))
    }

    @Test
    fun `赠送体验码不绑定设备可在任意设备激活`() {
        val code = License.generateCode(byteArrayOf(0, 0, 0, 1), "G", 0)
        val deviceA = byteArrayOf(0x11, 0x22, 0x33, 0x44)
        val deviceB = byteArrayOf(0xAA.toByte(), 0xBB.toByte(), 0xCC.toByte(), 0xDD.toByte())
        val infoA = License.verifyCode(code, deviceA)
        val infoB = License.verifyCode(code, deviceB)
        assertNotNull(infoA)
        assertEquals("L", infoA!!.plan)
        assertEquals(0, infoA.expireAt)
        assertNotNull(infoB)
        assertEquals("L", infoB!!.plan)
        assertEquals(0, infoB.expireAt)
    }

    @Test
    fun `赠送体验码流水号不同码值不同`() {
        val c1 = License.generateCode(byteArrayOf(0, 0, 0, 1), "G", 0)
        val c2 = License.generateCode(byteArrayOf(0, 0, 0, 2), "G", 0)
        assertNotEquals(c1, c2)
    }

    @Test
    fun `篡改的激活码无效`() {
        val code = License.generateCode(deviceId, "L", 0)
        val tampered = code.replaceRange(code.length - 1, code.length, "A")
        assertNull(License.verifyCode(tampered, deviceId))
    }

    @Test
    fun `过期月费码无效`() {
        val expire = System.currentTimeMillis() / 1000 - 100
        val code = License.generateCode(deviceId, "M", expire)
        assertNull(License.verifyCode(code, deviceId))
    }

    @Test
    fun `base32 往返一致`() {
        val data = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8)
        val encoded = License.base32Encode(data)
        val decoded = License.base32Decode(encoded)
        assertNotNull(decoded)
        assertEquals(data.toList(), decoded!!.toList())
    }
}
