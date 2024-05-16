package com.alirezasn80.learn_en.feature.home


import com.alirezasn80.learn_en.utill.debug
import java.io.BufferedReader
import java.io.IOException
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

    suspend fun getJsonFromUrl(urlString: String): String? {
        var connection: HttpURLConnection? = null
        return try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.bufferedReader().use { reader ->
                    reader.readText()
                }
            } else {
                null // Handle error response
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null // Handle the IOException
        } finally {
            connection?.disconnect()
        }
    }

    fun dictionaryHttpURLConnection(
        text: String,
        to: String = "fa",
        from: String = "en",
    ): String {
        try {
            val hl = URLEncoder.encode(to, charset)
            val sl = URLEncoder.encode(from, charset)
            val q = URLEncoder.encode(text, charset)
            try {
                val url = String.format(
                    "https://translate.google.com/translate_a/single?&client=gtx&sl=%s&tl=%s&q=%s&dt=bd&dt=t&dt=md&dt=ex",
                    sl,
                    hl,
                    q
                )
                debug(url)
                return getTextHttpURLConnection(url)

            } catch (ignored: java.lang.Exception) {
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun translateHttpURLConnection(
        text: String,
        to: String = "fa",
        from: String = "en",
    ): String {
        try {
            val hl = URLEncoder.encode(to, charset)
            val sl = URLEncoder.encode(from, charset)
            val q = URLEncoder.encode(text, charset)
            try {
                val url = String.format(
                    "https://translate.google.com/translate_a/single?&client=gtx&sl=%s&tl=%s&q=%s&dt=t",
                    sl,
                    hl,
                    q
                )
                return getTextHttpURLConnection(url)

            } catch (ignored: java.lang.Exception) {
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun imagesHttpUrl(
        query: String
    ): String {
        try {
            val q = URLEncoder.encode(query, charset)
            try {
                val url = String.format("https://www.google.com/search?q=%s&sclient=img&udm=2", q)
                debug(url)
                return getTextHttpURLConnection(url)

            } catch (ignored: java.lang.Exception) {
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return ""
    }
}