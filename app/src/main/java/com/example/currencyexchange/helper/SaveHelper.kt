package com.example.currencyexchange.helper

import android.content.Context
import java.io.File

object SaveHelper {

    fun saveResponseToFile(context: Context, filename: String, jsonResponse: String) {
        val file = File(context.filesDir, filename)
        file.writeText(jsonResponse)
    }

    fun loadResponseFromFile(context: Context, filename: String): String? {
        val file = File(context.filesDir, filename)
        return if (file.exists()) {
            file.readText()
        } else {
            null
        }
    }

}