package com.datpham.du21smartotp

import android.os.CountDownTimer
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.function.Consumer

/*
* @params
*  period: otp will be expired after that period
*  digit: length of otp value
*  generateTime: how many otp will be generated
* */
class DU21SmartOTP(
    private val period: Int = DEFAULT_PERIOD,
    private val digit: Int = DEFAULT_DIGIT,
    private val maxGenerateTime: Int = DEFAULT_GEN_TIMES
) {

    private var mTimer: CountDownTimer? = null
    private var mGenerateTime = 0
    private var mInput = ""

    companion object {
        const val DEFAULT_PERIOD = 30000
        const val DEFAULT_DIGIT = 6
        const val DEFAULT_GEN_TIMES = 2
    }

    var listenerOTP: ((otpValue: String, secondLeft: Long) -> Unit)? = null
    var listenerOTPExpired: (() -> Unit)? = null

    fun clear() {
        mInput = ""
        mTimer?.cancel()
    }

    fun createShareKey(input: String): String {
        return HashUtil.sha256String(input)
    }

    fun createTOTP(input: String) {
        mGenerateTime = 0
        mInput = input
        getTOTP()
    }

    fun createTOTPFromShareKey(shareKey: String, success: (otp: String) -> Unit) {
        Observable.create<Long> {
            it.onNext(TimeUtil.getNetworkTime())
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { ntpTime ->
                val counter = ntpTime / period
                val totp = TOTP()
                val otp = totp.generateTOTP256(shareKey, "$counter", "$digit")
                success.invoke(otp)
            }
            .subscribe()
    }

    private fun getTOTP() {
        val shareKey = createShareKey(mInput)
        Observable.create<Long> {
            it.onNext(TimeUtil.getNetworkTime())
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { ntpTime ->
                val counter = ntpTime / period
                val totp = TOTP()
                val otp = totp.generateTOTP256(shareKey, "$counter", "$digit")

                //Calculate time left
                val timePeriodLimit = counter + 1
                val timePeriodLimitMilisecond = timePeriodLimit * period
                val timeGap = timePeriodLimitMilisecond - ntpTime

                mGenerateTime++
                countDownOtp(otp, timeGap)

            }
            .subscribe()
    }

    private fun countDownOtp(otpValue: String, timeGap: Long) {
        mTimer = object : CountDownTimer(timeGap, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                listenerOTP?.invoke(otpValue, millisUntilFinished / 1000)
            }

            override fun onFinish() {
                if (mGenerateTime < maxGenerateTime) {
                    getTOTP()
                } else {
                    listenerOTPExpired?.invoke()
                }
            }
        }
        mTimer?.start()
    }

}