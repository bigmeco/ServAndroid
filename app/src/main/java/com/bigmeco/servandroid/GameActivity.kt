package com.bigmeco.servandroid

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.koushikdutta.async.http.AsyncHttpClient
import com.koushikdutta.async.http.AsyncHttpGet
import com.koushikdutta.async.http.WebSocket
import kotlinx.android.synthetic.main.activity_game.*


class GameActivity : AppCompatActivity() {

    private var webSocket: WebSocket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        var url = intent.getStringExtra("url")
        Log.d("fdgdfgdfg", url)
        Log.d("fdgdfgdfg", "ws://$url:8080/live")


        AsyncHttpClient.getDefaultInstance().websocket("ws://$url:8080/live", null) { ex, webSocket ->
            run {
                Log.d("webSocket",webSocket?.isOpen.toString())

                this.webSocket = webSocket
                if (ex != null) {
                    Log.d("webSocket",webSocket?.isOpen.toString())

                    return@run
                }
                webSocket.send("Hello Server")


                webSocket.setStringCallback {
                    textView2.text = it
                    Log.d("dfdsfsdf", it + " m md")
                }
            }

        }
        button.setOnClickListener {
            Log.d("webSocket",webSocket?.isOpen.toString())

        }
    }

}