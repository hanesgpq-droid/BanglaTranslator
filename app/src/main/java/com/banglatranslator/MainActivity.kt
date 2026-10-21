package com.banglatranslator

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val p = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val pad = (16 * resources.displayMetrics.density).toInt()

        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(pad, pad, pad, pad) }
        fun tv(t: String, size: Float = 14f, bold: Boolean = false) = TextView(this).apply {
            text = t; textSize = size; if (bold) setTypeface(typeface, android.graphics.Typeface.BOLD)
            setPadding(0, pad / 2, 0, 0)
        }

        col.addView(tv("Bangla ⇄ English Translator", 20f, true))
        col.addView(tv(
            "ব্যবহার: যেকোনো অ্যাপে (WhatsApp, Messenger, Chrome, Telegram…) টেক্সট সিলেক্ট করো → উপরের মেনুতে ⋮ (More) চাপো → \"বাংলায় অনুবাদ\" বা \"English-এ অনুবাদ\" চাপো।\n" +
            "• লেখার বক্সে সিলেক্ট করলে লেখাটা সরাসরি অনুবাদ হয়ে বদলে যাবে।\n" +
            "• পড়ার টেক্সটে (মেসেজ/ওয়েবপেজ) সিলেক্ট করলে রেজাল্ট পপ-আপে আসবে, Copy করা যাবে."
        ))

        col.addView(tv("Translation engine", 14f, true))
        val engine = Spinner(this)
        val labels = listOf("Google (ফ্রি, key লাগে না)", "Claude (Banglish এ বেশি accurate, API key লাগবে)")
        val values = listOf("google", "claude")
        engine.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, labels)
        engine.setSelection(values.indexOf(p.getString("engine", "google")).coerceAtLeast(0))
        col.addView(engine, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))

        col.addView(tv("Anthropic API key (শুধু Claude engine এর জন্য)", 14f, true))
        val key = EditText(this).apply {
            setText(p.getString("apiKey", "")); hint = "sk-ant-..."
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        col.addView(key, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))

        col.addView(tv("Model", 14f, true))
        val model = EditText(this).apply { setText(p.getString("model", Translator.DEFAULT_MODEL)) }
        col.addView(model, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))

        val save = Button(this).apply { text = "Save" }
        save.setOnClickListener {
            p.edit()
                .putString("engine", values[engine.selectedItemPosition])
                .putString("apiKey", key.text.toString().trim())
                .putString("model", model.text.toString().trim().ifBlank { Translator.DEFAULT_MODEL })
                .apply()
            Toast.makeText(this, "Saved ✓", Toast.LENGTH_SHORT).show()
        }
        col.addView(save)

        setContentView(ScrollView(this).apply { addView(col) })
    }
}
