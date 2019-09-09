package com.bigmeco.servandroid

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import androidx.work.WorkManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textHost.setOnClickListener {
            startActivity(Intent(this, HostActivity::class.java))
        }
        textStart.setOnClickListener {
            startActivity(Intent(this, UserActivity::class.java))

        }
    }

    override fun onRestart() {
        super.onRestart()
       //// WorkManager.getInstance().
    }
}
