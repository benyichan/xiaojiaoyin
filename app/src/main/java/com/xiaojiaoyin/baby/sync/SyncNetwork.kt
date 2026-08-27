package com.xiaojiaoyin.baby.sync

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.ServerSocket
import java.net.Socket

/**
 * 极简局域网同步：裸 Socket 传输变更集 JSON。
 * 接收端启动 ServerSocket；发送端连接并发送变更集，接收端合并后返回结果。
 * 照片等大文件暂不通过此通道（用文件同步兜底），真机版本再扩展。
 */
class SyncNetwork(
    private val onBundleReceived: suspend (String) -> String
) {
    private var server: ServerSocket? = null
    private var serving = false

    fun start(): Int {
        server?.close()
        server = ServerSocket(0).also { it.reuseAddress = true }
        serving = true
        return server!!.localPort
    }

    suspend fun serve() = withContext(Dispatchers.IO) {
        val socket = server ?: return@withContext
        while (serving) {
            val client = runCatching { socket.accept() }.getOrNull() ?: break
            launch { handle(client) }
        }
    }

    private suspend fun handle(client: Socket) {
        try {
            client.use { s ->
                val body = s.getInputStream().readBytes().toString(Charsets.UTF_8)
                android.util.Log.d("SyncNet", "received ${body.length} bytes")
                val merged = try {
                    onBundleReceived(body)
                } catch (e: Exception) {
                    android.util.Log.e("SyncNet", "bundle handling failed", e)
                    val msg = (e.message ?: e.javaClass.simpleName).take(500)
                    s.getOutputStream().write("ERR:$msg".toByteArray(Charsets.UTF_8))
                    s.getOutputStream().flush()
                    return
                }
                android.util.Log.d("SyncNet", "response ${merged.length} bytes")
                // 首行状态协议：OK 后跟合并结果；ERR 说明处理失败，发送端不会误以为成功
                val response = "OK\n$merged"
                s.getOutputStream().write(response.toByteArray(Charsets.UTF_8))
                s.getOutputStream().flush()
            }
        } catch (e: Exception) {
            android.util.Log.e("SyncNet", "handle failed", e)
        }
    }

    fun stop() {
        serving = false
        runCatching { server?.close() }
        server = null
    }

    companion object {
        suspend fun sendBundle(host: String, port: Int, bundleJson: String): String =
            withContext(Dispatchers.IO) {
                Socket(host, port).use { s ->
                    s.getOutputStream().write(bundleJson.toByteArray(Charsets.UTF_8))
                    s.getOutputStream().flush()
                    s.shutdownOutput()
                    val resp = s.getInputStream().readBytes().toString(Charsets.UTF_8)
                    val lines = resp.lineSequence().filter { it.isNotBlank() }.toList()
                    when {
                        lines.isEmpty() -> throw java.io.IOException("对端无响应")
                        lines.first() == "OK" -> lines.drop(1).joinToString("\n")
                        lines.first().startsWith("ERR") ->
                            throw java.io.IOException(lines.first().removePrefix("ERR:"))
                        else -> throw java.io.IOException("对端返回异常响应")
                    }
                }
            }
    }
}
