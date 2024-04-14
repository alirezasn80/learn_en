package com.alirezasn80.learn_en.utill

object Reload {

    var favorite = false
        get() {
            val current = field
            favorite = false
            return current
        }


    var created = false
        get() {
            val current = field
            created = false
            return current
        }
}