package com.screeps.native

import com.screeps.native.Constants._

import scala.scalajs.js

@js.native
trait Tombstone extends RoomObject {
    /** An object containing the deceased creep or power creep. */
    val creep: Creep = js.native
    /** Time of death. */
    val deathTime: Int @@ Ticks = js.native
    /** A unique object identifier. You can use [[Game.getObjectById]] to retrieve an object instance by its [[id]]. */
    val id: String = js.native
    /** A Store object that contains cargo of this structure. */
    val store: Store = js.native
    /** The amount of game ticks before this tombstone decays. */
    val ticksToDecay: Int @@ Ticks = js.native
}
