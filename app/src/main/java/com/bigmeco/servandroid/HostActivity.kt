package com.bigmeco.servandroid

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.support.constraint.ConstraintSet
import android.support.v7.app.AppCompatActivity
import android.transition.TransitionManager
import android.view.View
import com.github.sumimakito.awesomeqr.AwesomeQrRenderer
import com.github.sumimakito.awesomeqr.RenderResult
import com.github.sumimakito.awesomeqr.option.RenderOption
import com.github.sumimakito.awesomeqr.option.color.Color
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import kotlinx.android.synthetic.main.activity_host.*
import java.net.NetworkInterface
import java.util.*
import java.util.concurrent.TimeUnit
import android.os.AsyncTask.execute
import android.text.Editable
import android.text.TextWatcher
import com.bigmeco.servandroid.R.id.textView
import io.ktor.http.cio.websocket.*
import io.ktor.routing.patch
import io.ktor.websocket.WebSocketServerSession
import kotlinx.coroutines.channels.mapNotNull


class HostActivity : AppCompatActivity() {

    var server = embeddedServer(CIO, port = 8080) {
          install(WebSockets)
        routing {
            get("/") {
                call.respondText("ты пидр не очень", ContentType.Text.Plain)
            }
            get("/demo") {
                call.respondText("HELLO WORLD!")
            }
              webSocket("/myws") {
                  for (frame in incoming.mapNotNull { it as? Frame.Text }) {
                      val text = frame.readText()
                      editText.addTextChangedListener(object : TextWatcher {

                          override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                          }

                          override fun beforeTextChanged(s: CharSequence, arg1: Int, arg2: Int, arg3: Int) {

                          }

                          override fun afterTextChanged(arg0: Editable) {
                              outgoing.offer(Frame.Text("А ты пидор!! ${editText.text}"))
                          }

                      })

                      outgoing.send(Frame.Text("А ты пидор $text"))
                      if (text.equals("bye", ignoreCase = true)) {
                          close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                      }
                  }
              }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)

        startServer()
        buttonQr.setOnClickListener {
            val set = ConstraintSet()
            set.clone(mainView)
            changeConstraints(set)
            TransitionManager.beginDelayedTransition(mainView)
            set.applyTo(mainView)
        }

    }

    private fun changeConstraints(set: ConstraintSet) {
        if (cv.visibility == View.GONE) {
            imageView3.visibility = View.INVISIBLE
            set.clear(R.id.buttonQr, ConstraintSet.START)
            set.clear(R.id.buttonQr, ConstraintSet.BOTTOM)

            set.connect(R.id.buttonQr, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            set.connect(R.id.buttonQr, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
            cv.visibility = View.INVISIBLE
            cv.animate().alpha(1.0f).setDuration(350)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        cv.visibility = View.VISIBLE
                    }
                })


        } else {
            cv.animate().alpha(0.0f).setDuration(300)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        cv.visibility = View.GONE
                    }
                })
            imageView3.animate().alpha(1.0f).setDuration(400)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        imageView3.visibility = View.VISIBLE
                    }
                })

            set.clear(R.id.buttonQr, ConstraintSet.START)
            set.clear(R.id.buttonQr, ConstraintSet.BOTTOM)

            set.connect(R.id.buttonQr, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.END)
            set.connect(R.id.buttonQr, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.TOP)

        }

    }

    private fun startServer() {
        server.start()
        getIPAddress {
            textView.text = "$it:8080"
            val color = Color()
            color.dark = 0xFF00574b.toInt() // for blank spaces
            color.background = 0xBD00574B.toInt() // for non-blank spaces
            color.light = 0xFFb8f502.toInt() // for the background (will be overriden by background images, if set)

            val renderOption = RenderOption()
            renderOption.content = "$it:8080"  // содержимое для кодирования
            renderOption.size = 800  // размер окончательного изображения QR-кода
            renderOption.roundedPatterns = true
            renderOption.borderWidth = 40 // ширина пустого пространства вокруг QR-кода
            renderOption.color = color // установить цветовую палитру для QR- кода
            try {
                val result = AwesomeQrRenderer.render(renderOption)

                when {
                    result.bitmap != null -> {
                        // игра с растровым изображением
                        imageView3.setImageBitmap(result.bitmap)
                        imageQr.setImageBitmap(result.bitmap)
                        web.loadUrl("http://localhost:8080")

                    }
                    result.type == RenderResult.OutputType.GIF -> {
                    }
                    else -> {
                        // Ой, что-то пошло не так.
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Упс, что-то пошло не так.
            }
        }

    }


    fun getIPAddress(listener: (String) -> Unit) {
        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs = Collections.list(intf.getInetAddresses())
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress()) {
                        listener.invoke(addr.getHostAddress().toUpperCase())
                    }
                }
            }
        } catch (ex: Exception) {
        }
    }

    override fun onBackPressed() {
        server.stop(0, 0, TimeUnit.SECONDS)
        super.onBackPressed()
    }
}
