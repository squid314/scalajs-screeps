package com.squid314.screeps.roles

import com.screeps.native.Constants._
import com.screeps.native.{OwnedStructureWithStorage, _}

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{literal, global => g}
import scala.scalajs.js.UndefOr

import com.squid314.screeps.ext._

trait ToggleEnergyWorker extends Role {
    override final def run(creep: Creep): Unit = {
        val working = checkWork(creep)

        if (working) {
            work(creep)
        } else {
            if (!collect(creep)) {
                if (creep.getActiveBodyparts(Bodypart.Work) > 0) {
                    harvest(creep)
                }
            }
        }
    }

    def work(creep: Creep): Unit

    def collect(creep: Creep): Boolean = {
        // TODO find is an expensive operation. i could probably use lookForAtArea, room caching, and/or creep caching
        val filter: js.Function1[Structure, Boolean] = (s: Structure) => {
            allowedEnergyCollections.contains(s.structureType) &&
                storeHasEnoughEnergy(creep, s).getOrElse(false)
        }
        val container = creep.pos.findClosestByRange(Find.Structures, FindOptions[Structure](f = filter)).asInstanceOf[UndefOr[Structure]]
        for (c <- container) {
            val err = creep.withdraw(c, ResourceType.Energy)
            if (err == Error.OK.id) {
                return true
            } else if (err == Error.NotInRange.id) {
                creep.travelTo(c.pos)
                return true
            }
        }

        // look for dropped stuff (drop mining)
        val droppedFilter = (r: Resource) => {
            r.resourceType == ResourceType.Energy.name
        }
        val dropped = creep.pos.findClosestByRange(Find.DroppedResources, FindOptions(droppedFilter)).asInstanceOf[UndefOr[Resource]]
        for (d <- dropped) {
            val err = creep.pickup(d)
            if (err == Error.OK.id) {
                return true
            } else if (err == Error.NotInRange.id) {
                creep.travelTo(d.pos, literal(visualizePathStyle = literal(stroke = "#FFAA00")))
                return true
            }
        }

        false
    }

    val allowedEnergyCollections: List[String @@ StructureType] = List(StructureType.Container, StructureType.Storage, StructureType.Terminal)

    @inline def storeHasEnoughEnergy(creep: Creep, s: Structure) =
        for {
            stored <- s.asInstanceOf[OwnedStructureWithStorage].store(ResourceType.Energy)
            creepCap <- creep.store.getCapacity(ResourceType.Energy)
        } yield {
            stored > creepCap / 2
        }

    def harvest(creep: Creep): Unit = {
        for (source <- creep.pos.findClosestByRange(Find.SourcesActive).asInstanceOf[js.UndefOr[Source]]) {
            if (creep.harvest(source) == Error.NotInRange.id) {
                creep.travelTo(source.pos)
            }
        }
    }

    @inline def working(creep: Creep): Boolean =
        creep.memory.working.asInstanceOf[UndefOr[Boolean]]
            .getOrElse(false)

    def checkWork(creep: Creep): Boolean = {
        for (creepCap <- creep.store.getCapacity()) {
            if (working(creep) &&
                creep.store(ResourceType.Energy).getOrElse(0) == 0) {
                stopWork(creep)
            } else if (!working(creep) &&
                creep.store(ResourceType.Energy).getOrElse(0) == creepCap) {
                startWork(creep)
            }
        }
        working(creep)
    }

    @inline def startWork(creep: Creep): Unit = {
        creep.memory.working = true
        startAction(creep)
    }

    def startAction(creep: Creep): Unit = {}

    @inline def stopWork(creep: Creep): Unit = {
        creep.memory.working = false
        stopAction(creep)
    }

    def stopAction(creep: Creep): Unit = creep.say("\uD83D\uDD04 collect")
}
