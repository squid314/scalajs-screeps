package com.screeps.native

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal


@js.native
@JSGlobal
object Memory extends GlobalMemory {
    // TODO should this be removed?
}

@js.native
trait GlobalMemory extends Memory

@js.native
trait Memory extends js.Object {
    // TODO should these memory object declarations be removed? client may want to use more specialized memory objects to provide better type safety
    type CreepMemory = js.Dynamic
    type SpawnMemory = js.Dynamic
    type RoomMemory = js.Dynamic
    type FlagMemory = js.Dynamic
    val creeps: js.Dictionary[CreepMemory] = js.native
    val spawns: js.Dictionary[SpawnMemory] = js.native
    val rooms: js.Dictionary[RoomMemory] = js.native
    val flags: js.Dictionary[FlagMemory] = js.native
}
