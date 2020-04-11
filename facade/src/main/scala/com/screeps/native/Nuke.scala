package com.screeps.native

import com.screeps.native.Constants._

import scala.scalajs.js

/** A nuke landing position. This object cannot be removed or modified. You can [[Room.find]] incoming nukes in the room [[FindType.Nukes]]. */
@js.native
trait Nuke extends RoomObject {
    /** A unique object identifier. You can use [[Game.getObjectById]] method to retrieve an object instance by its `id`. */
    val id: String = js.native
    /** The name of the room where this nuke has been launched from. */
    val launchRoomName: String = js.native
    /** The remaining landing time. */
    val timeToLand: Int @@ Ticks = js.native
}
