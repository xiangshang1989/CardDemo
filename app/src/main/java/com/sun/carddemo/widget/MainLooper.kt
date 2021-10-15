package com.sun.carddemo.widget

import android.os.Handler
import android.os.Looper

/**
 * Author: sunhaitao
 * Date: 2021/10/14 16:44
 * Description:
 */
class MainLooper private constructor(looper: Looper) : Handler(looper) {
    companion object {
        val instance = MainLooper(Looper.getMainLooper())
    }

    fun isInMainThread() = Looper.myLooper() == Looper.getMainLooper()

    fun runOnUiThread(runnable: Runnable){
        if (isInMainThread()){
            runnable.run()
        }else{
            instance.post(runnable)
        }
    }

    fun runOnUiThread(runnable: Runnable,delayMillis:Long){
        instance.postDelayed(runnable,delayMillis)
    }
}