package com.squid314.screeps

import language.implicitConversions

import com.screeps.native._

package object proto {
    @inline implicit def roomOps(room: Room): RoomOps = new RoomOps(room)

    @inline implicit def structureOps[T <: Structure](str: T): StructureOps[T] = new StructureOps[T](str)
}
