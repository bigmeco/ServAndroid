package com.bigmeco.servandroid

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




class HostActivity : AppCompatActivity() {
    private val myWorkRequest = OneTimeWorkRequest.Builder(ServerWork::class.java).build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)

        startServer()


    }

    private fun startServer() {
        WorkManager.getInstance().enqueue(myWorkRequest).state.observeForever { it ->
            Log.d("rfgergerg", "$it  !!!")
            if (it == Operation.SUCCESS) {
                getIPAddress {
                    textView.text = "$it:8080"

                    val color = Color()
                    color.light = 0xFFFbfff00.toInt() // for blank spaces
                    color.dark = 0xFFF00574B.toInt() // for non-blank spaces
                    color.background =
                            0xFFFFFFFFF.toInt() // for the background (will be overriden by background images, if set)

                    val renderOption = RenderOption()
                    renderOption.content = "$it:8080"  // содержимое для кодирования
                    renderOption.size = 800  // размер окончательного изображения QR-кода
                    renderOption.borderWidth = 60  // ширина пустого пространства вокруг QR-кода
                    renderOption.color = color // установить цветовую палитру для QR- кода
                    try {
                        val result = AwesomeQrRenderer.render(renderOption)
                        when {
                            result.bitmap != null -> {
                                // игра с растровым изображением
                                imageView.setImageBitmap(result.bitmap)
                                web.loadUrl("http://localhost:8080")

                            }
                            result.type == RenderResult.OutputType.GIF -> {
                                // Если ваш фон является GifBackground , изображение
                                // будет сохранено в выходном файле, заданном в GifBackground
                                //, и не будет возвращено здесь. В результате
                                // result.bitmap будет нулевым.
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
        }


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

    override fun onBackPressed() {
        super.onBackPressed()
            //проблем
        WorkManager.getInstance().cancelWorkById(myWorkRequest.id)
    }
}
