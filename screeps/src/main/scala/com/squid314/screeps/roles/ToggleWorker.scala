package com.squid314.screeps.roles

import com.screeps.native.Constants._
import com.screeps.native.{OwnedStructureWithStorage, _}

import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal
import scala.scalajs.js.UndefOr

trait ToggleWorker extends Role {
    override final def run(creep: Creep): Unit = {
        val working = checkWork(creep)
        println(s"toggle worker is working? $working")

        if (working) {
            work(creep)
        } else {
            println("not working, look for energy")
            if (!collect(creep)) {
                println("nothing to collect, attempt to harvest")
                if (creep.getActiveBodyparts(Bodypart.Work) > 0) {
                    harvest(creep)
                }
            }
        }
    }

    def work(creep: Creep): Unit

    /** This is a candidate for overriding if the worker does not carry energy (or can carry others) */
    @inline def collect(creep: Creep): Boolean = collectInRoom(creep)

    def allowedEnergyCollections(): List[String @@ StructureType] = List(StructureType.Container, StructureType.Storage, StructureType.Terminal)

    def collectInRoom(creep: Creep): Boolean = {
        println("looking for energy")
        val filter = ((s: Structure) => {
            println(s"found a structure to test: $s")
            allowedEnergyCollections().contains(s.structureType) &&
                storeHasEnoughEnergy(creep, s).getOrElse(false)
        }).asInstanceOf[js.Object => Boolean]
        val container = creep.pos.findClosestByRange(Find.Structures, FindOptions(filter)).asInstanceOf[UndefOr[Structure]]
        println(s"found containers: $container")
        for (c <- container) {
            val err = creep.withdraw(c, ResourceType.Energy)
            if (err == Error.OK.id) {
                return true
            } else if (err == Error.NotInRange.id) {
                creep.moveTo(c.pos, literal(visualizePathStyle = literal(stroke = "#FFAA00")))
                return true
            }
        }

        false
    }

    @inline private def storeHasEnoughEnergy(creep: Creep, s: Structure) =
        for {
            stored <- s.asInstanceOf[OwnedStructureWithStorage].store(ResourceType.Energy)
            creepCap <- creep.store.getCapacity(ResourceType.Energy)
        } yield {
            println(s"store $s has $stored energy and creep has capacity $creepCap")
            stored > creepCap / 2
        }

    def harvest(creep: Creep): Unit = {
        println("in harvest")
        for (source <- creep.pos.findClosestByRange(Find.SourcesActive).asInstanceOf[js.UndefOr[Source]]) {
            println(s"found source: $source")
            if (creep.harvest(source) == Error.NotInRange.id) {
                creep.moveTo(source.pos)
            }
        }
    }

    @inline def working(creep: Creep): Boolean =
        creep.memory.working.asInstanceOf[UndefOr[Boolean]]
            .getOrElse(false)

    def checkWork(creep: Creep): Boolean = {
        for (creepCap <- creep.store.getCapacity()) {
            if (working(creep) &&
                creep.store.getFreeCapacity().getOrElse(0) == creepCap) {
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

    def stopAction(creep: Creep): Unit = {}
}
