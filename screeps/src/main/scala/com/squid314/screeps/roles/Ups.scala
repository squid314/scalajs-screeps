package com.squid314.screeps.roles

import com.screeps.native._
import com.screeps.native.Constants._
import com.squid314.screeps._
import com.squid314.screeps.ext._
import com.squid314.screeps.proto._

import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal
import scala.scalajs.js.UndefOr

object Ups extends ToggleEnergyWorker {
    override def work(creep: Creep): Unit =
        LazyList.from(creep.room.spawns)
            .lazyAppendedAll(creep.room.extensions)
            .lazyAppendedAll(creep.room.terminal.toOption.filter(_.store(ResourceType.Energy).filter(_ < 30_000).isDefined))
            .lazyAppendedAll(creep.room.storage.toOption)
            .lazyAppendedAll(creep.room.terminal.toOption)
            .lazyAppendedAll(creep.room.containers
                .filter((c: StructureContainer) => c.memory.deliverable.asInstanceOf[js.UndefOr[Boolean]].getOrElse(false)))
            .find(_.store.getFreeCapacity(ResourceType.Energy).getOrElse(0) > 0)
            .map(s =>
                Some(creep.transfer(s, ResourceType.Energy))
                    .filterNot(_ == Error.NotInRange.id)
                    .getOrElse(creep.travelTo(s.pos))
            )
            .getOrElse(println("ups has nowhere to deliver"))

    override def collect(creep: Creep): Boolean = {
        // NOTE: ups excludes "deliverable=true" structures
        // TODO this code (and its source) should be cleaned up
        val filter: js.Function1[Structure, Boolean] = (s: Structure) => {
            allowedEnergyCollections.contains(s.structureType) &&
                            !s.asInstanceOf[OwnedStructureWithStorage].memory.deliverable.asInstanceOf[js.UndefOr[Boolean]].getOrElse(false) &&
                            super.storeHasEnoughEnergy(creep, s).getOrElse(false)
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


    override def startAction(creep: Creep): Unit = creep.say("\uD83D\uDE9A deliver")
}
