package com.bigmeco.servandroid

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import kotlinx.android.synthetic.main.activity_main.*
import java.net.NetworkInterface
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startServer()
        getIPAddress {
            web.loadUrl("http://localhost:8080")
            textView.text = "$it:8080"
        }

    }

    private fun startServer() {
        val server = embeddedServer(CIO, port = 8080) {
            routing {
                get("/") {
                    call.respondText("Hello World!", ContentType.Text.Plain)
                }
                get("/demo") {
                    call.respondText("HELLO WORLD!")
                }
            }
        }
        server.start()
    }


    fun getIPAddress(listener: (String) -> Unit) {
        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs = Collections.list(intf.getInetAddresses())
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress()) {
                        val sAddr = addr.getHostAddress().toUpperCase()
                        Log.d("rfgergerg", sAddr)
                        listener.invoke(sAddr)
                    }
                }
            }
        } catch (ex: Exception) {
        }
    }
}
