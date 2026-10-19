package com.fsmaster.sentrysample

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import io.sentry.Sentry

class CartRepository {
    // "12,50" is how price looks in many locales; toDouble() only knows "12.50"
    fun parsePrice(raw: String): Double = raw.toDouble()

    fun total(prices: List<String>): Double = prices.sumOf { parsePrice(it) }
}

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val button = Button(this).apply { text = "Send error to Sentry" }
        setContentView(button)
        button.setOnClickListener { sendError("manual", fresh = true) }

        // CI starts the app with: am start ... --es run_id <id> --ez fresh true
        intent.getStringExtra("run_id")?.let { run ->
            sendError(run, intent.getBooleanExtra("fresh", true))
        }
    }

    private fun sendError(run: String, fresh: Boolean) {
        Sentry.withScope { scope ->
            scope.setTag("run_id", run)
            if (fresh) scope.fingerprint = listOf("{{ default }}", run)
            try {
                CartRepository().total(listOf("4.99", "12,50"))
            } catch (e: NumberFormatException) {
                val id = Sentry.captureException(e)
                Log.i("SentrySample", "event id: $id run: $run")
            }
        }
        Sentry.flush(5000)
    }
}
