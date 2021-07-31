package com.datpham.du21smartotpexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.datpham.du21smartotp.DU21SmartOTP
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val smartOTP = DU21SmartOTP()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*
        * Step1:
        * let shareKey = smartOTP.createShareKey("YOUR_SECRET") //Should be your PIN code + your device id
        * Then send this secret to server to register. Your server will use this secret to generate OTP too
        * */

        /*
        * Step 2:
        * register listenser and expired handler
        * */
        smartOTP.listenerOTP = object : ((String, Long) -> Unit) {
            override fun invoke(otpValue: String, secondLeft: Long) {
                tvTimeLeft.text = "$secondLeft seconds"
                tvOTP.text = otpValue
            }
        }

        smartOTP.listenerOTPExpired = object : (() -> Unit) {
            override fun invoke() {
                btnGenOtp.visibility = View.VISIBLE
                tvTimeLeft.visibility = View.GONE
                tvOTP.visibility = View.GONE
            }
        }

        /*
        * Step 3:
        * Each time create otp, inout your secret (PIN, password,....) to generate right OTP
        * If inout wrong secret -> Your OTP will be wrong
        * */
        btnGenOtp.setOnClickListener {
            val secret =
                edtSecret.text.toString() //If your secret is wrong then your OTP will wrong
            if (secret.isEmpty()) {
                Toast.makeText(this, "Input secret", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            smartOTP.createTOTP(secret)
            btnGenOtp.visibility = View.GONE
            tvTimeLeft.visibility = View.VISIBLE
            tvOTP.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        smartOTP.clear()
    }
}