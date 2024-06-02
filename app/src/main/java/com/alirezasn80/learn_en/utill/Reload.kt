package com.alirezasn80.learn_en.utill

object Reload {

    var favorite = false
        get() {
            val current = field
            favorite = false
            return current
        }


    var local = false
        get() {
            val current = field
            local = false
            return current
        }
}