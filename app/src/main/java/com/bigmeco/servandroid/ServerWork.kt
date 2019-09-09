package com.bigmeco.servandroid

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.impl.utils.taskexecutor.TaskExecutor
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.stopServerOnCancellation
import java.util.concurrent.Executor
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
           server.start().run {              Result.success()
           }
        } catch (error: Throwable) {
            Result.failure()
        }
    }




    override fun onStopped() {
        server.stop(0,0,TimeUnit.SECONDS)
        Log.d("rfgergerg", "fgbdfbdfdbdfbdfvbdfvbd efer we  3111111111113 33")
        super.onStopped()
    }

}