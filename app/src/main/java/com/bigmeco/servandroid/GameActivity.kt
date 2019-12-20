package com.bigmeco.servandroid

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Contacts
import android.provider.Settings
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.ws
import io.ktor.client.request.get
import io.ktor.http.HttpMethod
import io.ktor.http.cio.websocket.*
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class GameActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        var url = intent.getStringExtra("url")
        val client = HttpClient {
            install(WebSockets)
        }

var i =0
        GlobalScope.launch {
            //  Log.e("dfghdfgdfgdfg",url)

            client.ws(
                method = HttpMethod.Get,
                host = url,
                port = 8080, path = "/myws"
            ) {
                // this: DefaultClientWebSocketSession


                // Send text frame.

                send(Frame.Text("user"))
                send(Frame.Text(Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)))

                    // launch new coroutine in background and continue

                for (frame in incoming.mapNotNull { it as? Frame.Text }) {
                    Log.e("dfghdfgdfgdfg", "fggffhfghfghfg")
                    val text = frame.readText()

                    outgoing.send(Frame.Text("YOU SAID: $text"))
                    if (text.equals("bye", ignoreCase = true)) {
                        close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                    }
                    runOnUiThread {
                        Log.e("dfghdfgdfgdfg", "runOnUiThread")

                        button.setOnClickListener {
                            outgoing.offer(Frame.Text("Hello dfgdfgdfgdf"))
                            Log.e("dfghdfgdfgdfg", "fggffhfghfghfg")

                        }
                        textView2.text = "${frame.readText()} |  ${i++}"
                    }


                }

//                // Receive frame.
//                val frame = incoming.receive()
//                when (frame) {
//
//                    is Frame.Text -> textView2.text = "${frame.readText()} |  ${textView2.text}"
//                }
            }
        }

    }


}
