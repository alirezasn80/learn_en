package com.alirezasn80.learn_en.feature.home


import com.alirezasn80.learn_en.utill.debug
import org.jsoup.Jsoup
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object GoogleTranslate {
    private const val charset = "UTF-8"

    private fun getResultByUrl(url: String): String {
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


    fun getJsonDictionary(
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
                return getResultByUrl(url)

            } catch (ignored: java.lang.Exception) {
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun getJsonTranslate(
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
                return getResultByUrl(url)

            } catch (ignored: java.lang.Exception) {
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun getTranslate(
        text: String,
        from: String = "en",
        to: String = "fa",
    ): String {
        try {
            val doc = Jsoup.connect(
                "https://translate.google.com/m?hl=en" +
                        "&sl=$from" +
                        "&tl=$to" +
                        "&ie=UTF-8&prev=_m" +
                        "&q=$text"
            )
                .timeout(6000)
                .get()

            val element = doc.getElementsByClass("result-container")

            return if (element.isNotEmpty()) {
                element[0].text()
            } else
                ""
        } catch (e: Exception) {
            return ""
        }


    }

}