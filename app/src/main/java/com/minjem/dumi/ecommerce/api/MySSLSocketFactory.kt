package com.hendi.pulsa.api
/*

import org.apache.http.conn.ssl.SSLSocketFactory
import java.io.IOException
import java.net.Socket
import java.net.UnknownHostException
import java.security.*
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.security.cert.CertificateException
import javax.security.cert.X509Certificate


class MySSLSocketFactory @Throws(
    NoSuchAlgorithmException::class,
    KeyManagementException::class,
    KeyStoreException::class,
    UnrecoverableKeyException::class
)
constructor(truststore: KeyStore) : SSLSocketFactory(truststore) {
    internal var sslContext = SSLContext.getInstance("TLS")

    init {

        val tm = object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out java.security.cert.X509Certificate>?, authType: String?) {
            }

            override fun checkServerTrusted(chain: Array<out java.security.cert.X509Certificate>?, authType: String?) {
            }

            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate>? {
                return null
            }

            val acceptedIssuers: Array<X509Certificate>?
                get() = null

            @Throws(CertificateException::class)
            fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            @Throws(CertificateException::class)
            fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            }
        }

        sslContext.init(null, arrayOf<TrustManager>(tm), null)
    }

    @Throws(IOException::class, UnknownHostException::class)
    override fun createSocket(socket: Socket, host: String, port: Int, autoClose: Boolean): Socket {
        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose)
    }

    @Throws(IOException::class)
    override fun createSocket(): Socket {
        return sslContext.getSocketFactory().createSocket()
    }
}
*/
