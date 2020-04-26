package com.squid314.screeps.proto

import com.screeps.native.Constants._
import com.screeps.native._
import com.squid314.screeps._

import scala.scalajs.js

final class StructureOps[T <: Structure](val str: T) {
    def memory: js.Dynamic = {
        val structureMemory = str.room.memory.structures.asInstanceOf[js.UndefOr[js.Dynamic]]
            .getOrElse({
                val newStructureMemory: js.Dynamic = js.Dynamic.literal()
                str.room.memory.structures = newStructureMemory
                newStructureMemory
            })
        structureMemory
            .selectDynamic(str.id).asInstanceOf[js.UndefOr[js.Dynamic]]
            .getOrElse({
                val newStructureObject: js.Dynamic = js.Dynamic.literal()
                structureMemory.updateDynamic(str.id)(newStructureObject)
                newStructureObject
            })
    }
}
