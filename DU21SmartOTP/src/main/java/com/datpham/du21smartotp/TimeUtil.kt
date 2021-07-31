package com.datpham.du21smartotp

import org.apache.commons.net.ntp.NTPUDPClient
import java.net.InetAddress

class TimeUtil {

    companion object {

        private const val TIME_SERVER_ADDRESS = "pool.ntp.org"

        fun getNetworkTime(): Long {
            val timeClient = NTPUDPClient()
            val inetAddress = InetAddress.getByName(TIME_SERVER_ADDRESS)
            val timeInfo = timeClient.getTime(inetAddress)
            return timeInfo.message.receiveTimeStamp.time
        }

    }

}