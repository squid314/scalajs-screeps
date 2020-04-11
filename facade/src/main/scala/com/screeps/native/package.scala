package com.screeps

import com.screeps.native.Constants._

import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal

package object native {
    implicit class RoomOps(val room: Room) {
        @inline final def findStructures(opts: js.Object = literal()): js.Array[Structure] = room.find(FindType.Structures, opts).asInstanceOf[js.Array[Structure]]

        @inline final def findMyStructures(opts: js.Object = literal()): js.Array[Structure] = room.find(FindType.MyStructures, opts).asInstanceOf[js.Array[Structure]]

        @inline final def findHostileStructures(opts: js.Object = literal()): js.Array[Structure] = room.find(FindType.HostileStructures, opts).asInstanceOf[js.Array[Structure]]
    }
}
