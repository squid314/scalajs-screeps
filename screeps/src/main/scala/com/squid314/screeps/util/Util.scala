package com.squid314.screeps.util

import com.screeps.native.{Memory, RoomObject}

object Util {
//    def genId: String = Memory.idBase
    def closerSort[T <: RoomObject](origin: RoomObject): (T, T) => Int =
        (a, b) => origin.pos.getRangeTo(a) - origin.pos.getRangeTo(b)
}
