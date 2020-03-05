package com.notification.group.demo.communicationsdk

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import java.io.*
import java.net.*
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*

class Communicator() {
    private var TAG :String ="Communicator"
    private var _end: Boolean = false
    private val PROTOCOL_HTTP = "HTTP"
    private val PROTOCOL_HTTPS = "HTTPS"
    private val TIMEOUT_IN_MILLIS = 30 * 1000
    val HTTP_CACHE_SIZE = (20 * 1024 * 1024).toLong()// 20 MiB

    constructor(end: Boolean) : this() {
        _end = end
    }
    companion object {
        fun initCookieManager() {
            val cookieManager = CookieManager()
            CookieHandler.setDefault(cookieManager)
        }

        fun trustAllSSLCertificates() {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }

                @SuppressLint("TrustAllX509TrustManager")
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                    try {
                        chain[0].checkValidity()
                    } catch (e: Exception) {
                        throw CertificateException("Certificate not valid or trusted.")
                    }

                }
            })

            try {
                val sslContext: SSLContext = SSLContext.getInstance("TLS")
                sslContext.init(null, trustAllCerts, java.security.SecureRandom())
                HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        fun installHttpResponseCache(context: Context) {
            try {
                val httpCacheDir = File(context.cacheDir, "http")
                Class.forName("android.net.http.HttpResponseCache")
                    .getMethod("install", File::class.java, Long::class.javaPrimitiveType)
                    .invoke(null, httpCacheDir, (20 * 1024 * 1024).toLong())
            } catch (httpResponseCacheNotAvailable: Exception) {
                httpResponseCacheNotAvailable.localizedMessage
            }
        }
    }

    @Throws(CommunicationException::class)
    fun call(operation: ICommunicationOperation?): ICommunicationResponseProcessor? {
        val processor = operation?.getProcessor()
        var connection: HttpURLConnection? = null

        try {

            val url = URL(operation?.getPath())
            connection = getHttpURLConnection(url)
            if (connection != null) {
                connection.requestMethod = operation?.getMethod()?.value
                connection.useCaches = operation?.shouldUseCache()!!
                connection.connectTimeout = TIMEOUT_IN_MILLIS
                connection.readTimeout = TIMEOUT_IN_MILLIS

                val headers = operation.getHeaders()
                if (headers!=null) {
                    for ((key, value) in headers) {
                        connection.setRequestProperty(key, value)
                    }
                }
                //connection.connect();
                Log.d(TAG,operation?.getMethod().toString())
                if (operation?.getMethod() == CommunicationHttpMethod.POST || operation?.getMethod() == CommunicationHttpMethod.PUT) {
                    if (operation?.getPayload() != null) {
                        connection.doOutput = true
                        val outputStream: OutputStream = connection.outputStream
                        val bufferedWriter = BufferedWriter(OutputStreamWriter(outputStream, "UTF-8"))
                        bufferedWriter.write(operation.getPayload())
                        bufferedWriter.flush()
                        bufferedWriter.close()
                        outputStream.close()
                    } else {
                        throw CommunicationException(
                            "Payload cannot be null.",
                            CommunicationException.PAYLOAD_NULL,
                            -1,
                            null
                        )
                    }
                }
                val responseCode = connection.responseCode
                Log.d(TAG,responseCode.toString())
                var responseText = ""
                val responseHeaders = HashMap(connection.headerFields)
                if (responseCode == HttpURLConnection.HTTP_OK) {

                    val bufferedReader = BufferedReader(InputStreamReader(connection.inputStream))
                    val stringBuilder = StringBuilder()
                    var line: String?
                    do {
                        line = bufferedReader.readLine()
                        if (line == null)
                            break
                        stringBuilder.append(line)
                        stringBuilder.append("\n")
                        if (_end) {
                            throw CommunicationException(
                                "User forcefully ended.",
                                CommunicationException.FORCE_ENDED,
                                200,
                                stringBuilder.toString()
                            )
                        }
                    } while (true)
                    bufferedReader.close()
                    responseText = stringBuilder.toString()
                    Log.d(TAG,responseText.toString())
                }
                if (processor != null) {
                    val processed = processor.process(responseCode, responseText, responseHeaders)
                    if (!processed) {
                        throw CommunicationException(
                            "Unable to process",
                            CommunicationException.UNABLE_TO_PROCESS,
                            responseCode,
                            responseText
                        )
                    }
                } else {
                    throw CommunicationException(
                        "Response processor cannot be null",
                        CommunicationException.PROCESSOR_IS_NULL,
                        responseCode,
                        responseText
                    )
                }
            }

        } catch (ex: SocketException) {
            throw CommunicationException(
                ex.message,
                CommunicationException.SOCKET_TIMEOUT,
                -1,
                null
            )
        } catch (ex: MalformedURLException) {
            throw  CommunicationException(
                ex.message,
                CommunicationException.MALFORMED_URL,
                -1,
                null
            )
        } catch (ex: ProtocolException) {
            throw  CommunicationException(
                ex.message,
                CommunicationException.PROTOCOL_NOT_SUPPORTED,
                -1,
                null
            )
        } catch (ex: EOFException) {
            throw  CommunicationException(
                ex.message,
                CommunicationException.EOF_EXCEPTION,
                -1,
                null
            )
        } catch (ex: IOException) {
            throw  CommunicationException(
                ex.message,
                CommunicationException.IO_EXCEPTION,
                -1,
                null
            )
        } catch (ex: CommunicationException) {
            throw ex
        } catch (ex: Exception) {
            throw CommunicationException(ex.message, CommunicationException.UNKNOWN, -1, null)
        } finally {
            connection?.disconnect()
        }
        return processor
    }

    @Throws(IOException::class)
    fun getHttpURLConnection(url: URL): HttpURLConnection? {
        // instantiating the URL Connection based on the protocol.
        val connection: HttpURLConnection?

        when {
            url.protocol.equals(PROTOCOL_HTTP, true) -> connection = url.openConnection() as HttpURLConnection?
            url.protocol.equals(PROTOCOL_HTTPS, true) -> {
                val https: HttpsURLConnection = url.openConnection() as HttpsURLConnection

                https.hostnameVerifier = HostnameVerifier { hostname, session -> true }
                connection = https
            }
            else -> run { throw ProtocolException("Only http and https protocols are supported.") }
        }
        return connection
    }

    fun end() {
        _end = true
    }

    fun destroy() {

    }
}