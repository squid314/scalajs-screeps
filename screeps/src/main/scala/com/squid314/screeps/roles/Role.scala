package com.squid314.screeps.roles

import com.screeps.native.Constants._
import com.screeps.native._

import scala.scalajs.js
import scala.scalajs.js._

trait Role {
    def run(creep: Creep): Unit
}

trait EnergyUser {
    val allowedCollectSourceTypes = js.Array[String @@ StructureType](
        StructureType.Container,
        StructureType.Storage,
        StructureType.Terminal,
    )

    def collectRoomEnergy(finish: Creep => Unit)(creep: Creep): Unit = {
        val structureToBoolean: Structure => Boolean = (s: Structure) => allowedCollectSourceTypes.contains(s.structureType)
        for (container <- creep.pos.findClosestByRange(FindType.Structures,
            FindOptions[Structure](structureToBoolean))
            .asInstanceOf[UndefOr[Structure]]) {

        }
    }
}
