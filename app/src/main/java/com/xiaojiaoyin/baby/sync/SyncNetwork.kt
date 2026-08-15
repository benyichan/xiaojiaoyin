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
                val response = onBundleReceived(body)
                android.util.Log.d("SyncNet", "response ${response.length} bytes")
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
                    s.getInputStream().readBytes().toString(Charsets.UTF_8)
                }
            }
    }
}
