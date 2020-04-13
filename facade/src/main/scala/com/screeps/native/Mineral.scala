package com.screeps.native

import com.screeps.native.Constants._

import scala.scalajs.js

/**
 * A mineral deposit. Can be harvested by creeps with a [[BodypartType.Work]] using the extractor structure.
 * Learn more about minerals from [[https://docs.screeps.com/resources.html this article]].
 *
 * Regeneration amount    140-280K for Hydrogen and Oxygen
 * 70-140K for Utrium, Keanium, Lemergium, Zynthium, Catalyst
 * Regeneration time      50,000 ticks
 */
@js.native
trait Mineral extends RoomObject {
    /** A unique object identificator. You can use Game.getObjectById method to retrieve an object instance by its id. */
    val id: String = js.native
    /** The remaining amount of resources. */
    val mineralAmount: Int @@ ResourceAmount = js.native
    /** The resource type, one of the RESOURCE_* constants. */
    val mineralType: String @@ ResourceType = js.native
    /** The density that this mineral deposit will be refilled to once [[ticksToRegeneration]] reaches 0. This is one of the [[Density]] constants. */
    val density: Int @@ Density = js.native
    /** The remaining time after which the source will be refilled. */
    val ticksToRegeneration: Int = js.native
}
