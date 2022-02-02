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
            var validOutput = ""
            try {
                validOutput = MainActivity.Companion.getCertificates(inputUrl)
            } catch (e: SSLHandshakeException) {
                val errString = """
                    Error:
                    ${e.message}
                    """.trimIndent()
                xmlResult.setTextColor(Color.RED)
                xmlResult.text = "Invalid"
                xmlMessage.text = errString
            } catch (e: Exception) {
                val errString = """
                    Error:
                    ${e.message}
                    """.trimIndent()
                xmlResult.setTextColor(Color.RED)
                xmlResult.text = "Invalid Domain"
                xmlMessage.text = errString
            }
            if (validOutput !== "") {
                xmlResult.setTextColor(Color.GREEN)
                xmlResult.text = "Valid"
                xmlMessage.text = validOutput
            }
        })
    }

    companion object {
        /**
         * Returns a String representation of the given URL's Certificate Chain.
         * Throws an IOException if there are any connection errors.
         */
        @Throws(IOException::class)
        private fun getCertificates(inputURL: String): String {
            val https = "https://"
            var formattedCerts = ""
            val url = URL(https + inputURL)
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
            var i = 0
            while (i < certs.size) {
                val cert = certs[i] as X509Certificate
                buildCerts.append(" ").append(i + 1).append(":\n")
                buildCerts.append("   Issuer: ").append(cert.issuerDN.toString()).append("\n\n")
                buildCerts.append("   Expires: ").append(cert.notAfter.toString()).append("\n\n\n")
                i += 1
            }
            return buildCerts.toString()
        }
    }


}