package com.bigmeco.servandroid

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.github.sumimakito.awesomeqr.AwesomeQrRenderer
import com.github.sumimakito.awesomeqr.RenderResult
import com.github.sumimakito.awesomeqr.option.RenderOption
import com.github.sumimakito.awesomeqr.option.color.Color
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.stopServerOnCancellation
import kotlinx.android.synthetic.main.activity_host.*
import java.net.NetworkInterface
import java.util.*
import androidx.work.OneTimeWorkRequest
import androidx.work.Operation
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import javax.xml.datatype.DatatypeConstants.SECONDS
import android.transition.TransitionManager
import android.support.constraint.ConstraintSet
import android.view.View
import android.view.ViewStructure
import com.github.sumimakito.awesomeqr.option.background.StillBackground
import kotlin.time.minutes


class HostActivity : AppCompatActivity() {

//    var server = embeddedServer(CIO, port = 8080) {
//        routing {
//            get("/") {
//                call.respondText("ты пидр не очень", ContentType.Text.Plain)
//            }
//            get("/demo") {
//                call.respondText("HELLO WORLD!")
//            }
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)
        startServer()

        buttonQr.setOnClickListener {
            val set = ConstraintSet()
            set.clone(mainView)
            changeConstraints(set)

            TransitionManager.beginDelayedTransition(mainView)
            // apply constraints settings from set to current ConstraintLayout
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
//            imageQr.animate().alpha(1.0f)
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
          //  imageQr.visibility = View.GONE


        }

    }

    private fun startServer() {
       // server.start()
        getIPAddress {
            textView.text = "$it:8080"
            val color = Color()
            color.dark = 0xFF00574b.toInt() // for blank spaces
            color.background = 0xBD00574B.toInt() // for non-blank spaces
            color.light = 0xFFb8f502.toInt() // for the background (will be overriden by background images, if set)

            val renderOption = RenderOption()
            renderOption.content = "$it:8080"  // содержимое для кодирования
            renderOption.size = 800  // размер окончательного изображения QR-кода
            renderOption.roundedPatterns =  true
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
        //server.stop(0, 0, TimeUnit.SECONDS)
        super.onBackPressed()
    }
}
