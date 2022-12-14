package com.vk.sarthi.utli

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.compose.runtime.MutableState
import com.vk.sarthi.R
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel
import java.util.*

object Util {

    fun checkForFileSize(fileSize: Long): Boolean {
        val result = fileSize.toDouble() / 1024.0 / 1024.0
        return result < 10
    }


    fun copyFile(inChannel: FileInputStream, dst: File?) {
        val inGChannel: FileChannel = inChannel.channel
        val outChannel: FileChannel = FileOutputStream(dst).channel
        try {
            inGChannel.transferTo(0, inGChannel.size(), outChannel)
        } finally {
            inChannel.close()
            outChannel.close()
        }
    }

    fun showDatePicker(mContext: Context, mDate: MutableState<String>):DatePickerDialog{
        val mYear: Int
        val mMonth: Int
        val mDay: Int

        // Initializing a Calendar
        val mCalendar = Calendar.getInstance()

        // Fetching current year, month and day
        mYear = mCalendar.get(Calendar.YEAR)
        mMonth = mCalendar.get(Calendar.MONTH)
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

        mCalendar.time = Date()


       return  DatePickerDialog(
            mContext,
            R.style.DialogWindowTheme,
            { _: DatePicker, mY: Int, mM: Int, mDayOfMonth: Int ->
                mDate.value = "$mDayOfMonth/${mM+1}/$mY"
            }, mYear, mMonth, mDay
        )

    }
}

