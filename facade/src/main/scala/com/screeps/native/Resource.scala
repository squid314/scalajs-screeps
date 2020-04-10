package com.screeps.native

import com.screeps.native.Constants._

import scala.scalajs.js

/** A dropped piece of resource. It will decay after a while if not picked up. Dropped resource pile decays for {{{ceil(amount/1000)}}} units per tick. */
@js.native
trait Resource extends RoomObject {
    /** A unique object identificator. You can use [[Game.getObjectById()]] method to retrieve an object instance by its id. */
    val id: String
    /** The amount of resource units containing */
    val amount: Int @@ ResourceAmount
    /** One of the [[ResourceType]] constants. */
    val resourceType: String @@ ResourceType
}
