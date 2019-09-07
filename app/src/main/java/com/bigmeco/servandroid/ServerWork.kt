package com.bigmeco.servandroid

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.stopServerOnCancellation
import java.util.concurrent.TimeUnit


class ServerWork(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    var server = embeddedServer(CIO, port = 8080) {
        routing {
            get("/") {
                call.respondText("ты пидр не очень", ContentType.Text.Plain)
            }
            get("/demo") {
                call.respondText("HELLO WORLD!")
            }
        }
    }
    override fun doWork(): Result {
        return try {
            server.start()
            Result.success()
        } catch (error: Throwable) {
            Result.failure()
        }
    }

    override fun onStopped() {
        server.stop(0,0,TimeUnit.SECONDS)
        Log.d("sdsd", "sdsfdsfdsfdsf        222222222222")
        super.onStopped()
    }

}