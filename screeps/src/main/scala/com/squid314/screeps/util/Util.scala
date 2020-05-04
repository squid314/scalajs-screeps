package com.squid314.screeps.util

import com.screeps.native.RoomObject

object Util {
    def closerSort[T <: RoomObject](origin: RoomObject): (T, T) => Int =
        (a, b) => origin.pos.getRangeTo(a) - origin.pos.getRangeTo(b)

    def idPrint[T](str: String): T => T =
        (t: T) => {
            println(str.format(t))
            t
        }
}
