package com.sun.carddemo

import com.sun.carddemo.util.KtPreferences


object SpConstant : KtPreferences() {
    var HAS_PERMISSION by booleanPref()
    var KEY_TOKEN by stringPref()
    var KEY_EXPIRES by stringPref()
}