package com.example.verify_certificate_kotlin

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.TextView
import android.os.StrictMode.ThreadPolicy
import android.os.Bundle
import com.example.verify_certificate_kotlin.R
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

class MainActivity : AppCompatActivity() {

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
            val inputUrl = xmlUrl.text.toString()
            xmlMessage.text = ""
            xmlDomain.text = "Domain: $inputUrl"
            try {
                checkSslCertificate(inputUrl, xmlResult, xmlMessage)
            } catch (e: SSLHandshakeException) {
                val errString = """
                    Error:
                    ${e.message}
                    """.trimIndent()
                setInvalidCertificate(errString, xmlResult, xmlMessage)
            } catch (e: Exception) {
                val errString = """
                    Error:
                    ${e.message}
                    """.trimIndent()
                setInvalidDomain(errString, xmlResult, xmlMessage)
            }
        })
    }

    private companion object {
        private fun checkSslCertificate(inputUrl: String, result: TextView, message: TextView): Unit {
            var validOutput = ""
            validOutput = getCertificates(inputUrl)
            setValidStatus(validOutput, result, message)
        }
        private fun setValidStatus(validOutput: String, result: TextView, message: TextView): Unit {
            result.setTextColor(Color.GREEN)
            result.text = "Valid"
            message.text = validOutput
        }

        private fun setInvalidCertificate(err: String, result: TextView, message: TextView): Unit {
            result.setTextColor(Color.RED)
            result.text = "Invalid"
            message.text = err
        }

        private fun setInvalidDomain(err: String, result: TextView, message: TextView): Unit {
            result.setTextColor(Color.RED)
            result.text = "Invalid Domain"
            message.text = err
        }

        /**
         * Returns a String representation of the given URL's Certificate Chain.
         * Throws an IOException if there are any connection errors.
         */
        @Throws(IOException::class)
        private fun getCertificates(inputUrl: String): String {
            val https = "https://"
            var formattedCerts = ""
            val url = URL(https + inputUrl)
            var urlConnection: HttpsURLConnection? = null
            urlConnection = url.openConnection() as HttpsURLConnection
            formattedCerts = try {
                urlConnection.responseCode
                formatCerts(
                    urlConnection.serverCertificates
                )
            } finally {
                urlConnection.disconnect()
            }
            return formattedCerts
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
    }


}