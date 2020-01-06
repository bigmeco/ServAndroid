package com.bigmeco.servandroid

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.provider.Settings
import android.support.constraint.ConstraintSet
import android.support.v7.app.AppCompatActivity
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import com.github.sumimakito.awesomeqr.AwesomeQrRenderer
import com.github.sumimakito.awesomeqr.RenderResult
import com.github.sumimakito.awesomeqr.option.RenderOption
import com.github.sumimakito.awesomeqr.option.color.Color
import com.koushikdutta.async.AsyncServer
import com.koushikdutta.async.http.WebSocket
import com.koushikdutta.async.http.server.AsyncHttpServer
import kotlinx.android.synthetic.main.activity_host.*
import java.net.NetworkInterface
import java.util.*
import kotlin.collections.ArrayList


class HostActivity : AppCompatActivity() {
    lateinit var users: ArrayList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)
        users = arrayListOf(Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID))


        val users = ArrayList<UserPojo>()


        val httpServer = AsyncHttpServer()
        httpServer.listen(AsyncServer.getDefault(), 8080)
        httpServer.websocket(
            "/live"
        ) { webSocket, request ->
            val user = UserPojo()
            user.socet = webSocket
            users.add(user)
            webSocket.setClosedCallback { ex ->
                try {
                    if (ex != null)
                        Log.e("WebSocket", "An error occurred", ex)
                } finally {
                    users.remove(user)
                }
            }
            webSocket.stringCallback = WebSocket.StringCallback { s ->
                if ("Hello Server" == s) {
                    webSocket.send("Welcome Client!")
                    Log.d("WebSocket", "sent")

                }
            }

        }
        startServer()

        buttonQr.setOnClickListener {
            val set = ConstraintSet()
            set.clone(mainView)
            changeConstraints(set)
            TransitionManager.beginDelayedTransition(mainView)
            set.applyTo(mainView)
            for (user in users) {
                user.socet?.send("scan")
            }
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
        getIPAddress {
            textView.text = "$it"
            val color = Color()
            color.dark = 0xFF00574b.toInt() // for blank spaces
            color.background = 0xBD00574B.toInt() // for non-blank spaces
            color.light = 0xFFb8f502.toInt() // for the background (will be overriden by background images, if set)

            val renderOption = RenderOption()
            renderOption.content = it // содержимое для кодирования
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
                        web.loadUrl("http://127.0.0.1:8080")

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


    private fun getIPAddress(listener: (String) -> Unit) {
        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    println(
                        "Display name: "
                                + addr.hostAddress
                    )
                    if (addr.hostAddress.subSequence(0, 3) == "192" || addr.hostAddress.subSequence(0, 3) == "172") {
                        listener.invoke(addr.hostAddress.toUpperCase())
                    }

                }
            }
        } catch (ex: Exception) {
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
