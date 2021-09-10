package com.levp.wolfquotes.activity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.levp.wolfquotes.R
import java.util.*

class PointsActivity : AppCompatActivity() {
    private lateinit var log:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_points)
        val arguments = intent.extras
        val points = arguments!!["points"] as ArrayList<*>?
        log = findViewById<TextView>(R.id.textView_points_content)
        val res = StringBuilder("\n")
        for (i in points!!.indices) {
            res.append(i.toString() + ": " + points[i])
            if (i % 2 == 1) res.append("\n") else for (j in 0 until 10 - points[i].toString().length) res.append(
                " "
            )
        }

        log.setText(res.toString())
    }
}