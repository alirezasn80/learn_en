package com.alirezasn80.learn_en.feature.home


import android.text.TextUtils
import android.util.Log
import com.alirezasn80.learn_en.utill.debug
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object TranslationConnection {
    private const val charset = "UTF-8"

    private fun getTextHttpURLConnection(url: String): String {
        var connection: HttpURLConnection? = null
        val response = StringBuilder()
        try {
            val string = "UTF-8"
            connection = URL(url).openConnection() as HttpURLConnection
            connection.setRequestProperty("Accept-Charset", "UTF-8")
            connection.addRequestProperty(
                "User-Agent",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30)"
            )
            val `in` = BufferedReader(
                InputStreamReader(
                    connection.inputStream, string
                )
            )
            while (true) {
                val inputLine = `in`.readLine()
                if (inputLine == null) {
                    `in`.close()
                    return response.toString()
                }
                response.append(inputLine)
            }
        } catch (e: java.lang.Exception) {
        } finally {
            connection?.disconnect()
        }
        return response.toString()
    }

    fun translateHttpURLConnection(
        to_translate: String?,
        to_language: String?,
        from_language: String?,
    ): String {
        try {
            val hl = URLEncoder.encode(to_language, charset)
            val sl = URLEncoder.encode(from_language, charset)
            val q = URLEncoder.encode(to_translate, charset)
            try {
                val sb = java.lang.StringBuilder()
                val url = String.format(
                    "https://translate.google.com/translate_a/single?&client=gtx&sl=%s&tl=%s&q=%s&dt=bd&dt=t",
                    sl,
                    hl,
                    q
                )
                debug(url)
                var text = getTextHttpURLConnection(url)
                return text

            } catch (ignored: java.lang.Exception) {
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return ""
    }
}