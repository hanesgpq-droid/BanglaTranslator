package com.banglatranslator

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast

abstract class ProcessTextActivity(private val to: String) : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString()
        val readOnly = intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false)
        if (text.isNullOrBlank()) { finish(); return }

        Toast.makeText(this, "অনুবাদ হচ্ছে…", Toast.LENGTH_SHORT).show()
        Thread {
            val result = try { Result.success(Translator.translate(this, text, to)) }
            catch (e: Exception) { Result.failure(e) }
            runOnUiThread {
                if (isFinishing) return@runOnUiThread
                result.fold(
                    onSuccess = { out -> if (readOnly) showDialog(out) else replace(out) },
                    onFailure = { e -> showDialog("⚠ " + (e.message ?: "Translation failed"), false) }
                )
            }
        }.start()
    }

    /** Editable field: replace the selected text (Ctrl+Z / undo works in most apps). */
    private fun replace(out: String) {
        setResult(RESULT_OK, Intent().putExtra(Intent.EXTRA_PROCESS_TEXT, out))
        finish()
    }

    /** Read-only text (web page, received message): show result with Copy button. */
    private fun showDialog(out: String, canCopy: Boolean = true) {
        val b = AlertDialog.Builder(this)
            .setMessage(out)
            .setNegativeButton("বন্ধ") { _, _ -> finish() }
            .setOnCancelListener { finish() }
        if (canCopy) b.setPositiveButton("Copy") { _, _ ->
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newPlainText("translation", out))
            Toast.makeText(this, "Copied ✓", Toast.LENGTH_SHORT).show()
            finish()
        }
        b.show()
    }
}

class ToBnActivity : ProcessTextActivity("bn")
class ToEnActivity : ProcessTextActivity("en")
