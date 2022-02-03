package com.example.verify_certificate_kotlin

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.TextView
import android.os.StrictMode.ThreadPolicy
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.Button
import java.io.IOException
import java.lang.Exception
import java.lang.StringBuilder
import java.net.URL
import java.security.cert.Certificate
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLHandshakeException
import kotlin.Throws

/**
 * The Main and only Activity for an app which takes an input domain
 * and verifies its server's SSL certificates. In the event of an invalid certificate,
 * an error message is displayed along with the invalid status. If the certificate is
 * valid, the certificate chain containing the issuer and expiration date is displayed
 * along with the valid status.
 */
class MainActivity : AppCompatActivity() {

    /* Set the thread policy to allow network access on the main thread */
    private val policy = ThreadPolicy.Builder().permitAll().build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        StrictMode.setThreadPolicy(policy)
        val xmlUrl = findViewById<EditText>(R.id.editTextUrl)
        val xmlVerify = findViewById<Button>(R.id.buttonVerify)
        val xmlResult = findViewById<TextView>(R.id.textViewResult)
        val xmlMessage = findViewById<TextView>(R.id.textViewMessage)
        val xmlDomain = findViewById<TextView>(R.id.textViewDomain)

        xmlVerify.setOnClickListener(View.OnClickListener {
            val domainName = xmlUrl.text.toString()
            resetMessageAndDomain(xmlMessage, xmlDomain, domainName)
            try {
                checkSslCertificate(domainName, xmlResult, xmlMessage)
            } catch (e: SSLHandshakeException) {
                displayInvalidCertificate(e.message, xmlResult, xmlMessage)
            } catch (e: Exception) {
                displayInvalidDomain(e.message, xmlResult, xmlMessage)
            }
        })
    }

    private companion object {
        private fun resetMessageAndDomain(message: TextView, domain: TextView,
                                          domainName: String): Unit {
            setTextViewTxt(message, "")
            setTextViewTxt(domain, "Domain: $domainName")
        }
        /** Establishes an https connection based on the given domain
         * and displays the certificates if any. If no connection is made
         * an IOException is passed up to the caller.
         */
        private fun checkSslCertificate(domain: String,
                                        result: TextView, message: TextView): Unit {
            val httpsConn = establishConnection(domain)
            val certChain = formatCerts(httpsConn.serverCertificates)
            displayValidStatus(certChain, result, message)
        }

        /**
         * Returns an HttpsURLConnection instance or throws an IOException
         * if a connection cannot be made.
         */
        @Throws(IOException::class)
        private fun establishConnection(domain: String): HttpsURLConnection {
            val url = URL("https://$domain")
            val httpConnection: HttpsURLConnection?
            httpConnection = url.openConnection() as HttpsURLConnection
            /*Get response code to ensure connection has been made*/
            httpConnection.responseCode
            httpConnection.connectTimeout = 5000

            return httpConnection
        }

        /**
         * Takes an ordered array of Certificates and formats the necessary
         * information into the returned String.
         */
        private fun formatCerts(certs: Array<Certificate>): String {
            val buildCerts = StringBuilder()
            buildCerts.append("Certificate Chain:\n\n")
            certs.forEachIndexed { i, c->
                val cert = c as X509Certificate
                buildCerts.append(" ").append(i + 1).append(":\n")
                buildCerts.append("   Issuer: ").append(cert.issuerDN.toString()).append("\n\n")
                buildCerts.append("   Expires: ").append(cert.notAfter.toString()).append("\n\n\n")
            }
            return buildCerts.toString()
        }

        private fun displayValidStatus(validOutput: String,
                                       result: TextView, message: TextView): Unit {
            result.setTextColor(Color.GREEN)
            setTextViewTxt(result, "Valid")
            setTextViewTxt(message, validOutput)
        }

        private fun displayInvalidCertificate(err: String?,
                                          result: TextView, message: TextView): Unit {
            result.setTextColor(Color.RED)
            setTextViewTxt(result, "Invalid")
            setTextViewTxt(message, "Error:\n${err}")
        }

        private fun displayInvalidDomain(err: String?,
                                     result: TextView, message: TextView): Unit {
            result.setTextColor(Color.RED)
            setTextViewTxt(result, "Invalid Domain")
            setTextViewTxt(message, "Error:\n${err}")
        }

        private fun setTextViewTxt(tv: TextView, text: String): Unit {
            tv.text = text
        }
    }
}