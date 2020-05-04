package com.squid314.screeps.roles

import com.screeps.native._
import com.screeps.native.Constants._
import com.squid314.screeps._
import com.squid314.screeps.ext._
import com.squid314.screeps.proto._

import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal
import scala.scalajs.js.UndefOr
import scala.scalajs.js.JSConverters._

object Ups extends ToggleEnergyWorker {
    override def work(creep: Creep): Unit =
        LazyList.from(creep.room.spawns)
            .lazyAppendedAll(creep.room.extensions)
            .lazyAppendedAll(creep.room.towers)
            .lazyAppendedAll(creep.room.terminal.toOption.filter(_.store(ResourceType.Energy).filter(_ < 30_000).isDefined))
            .lazyAppendedAll(creep.room.storage.toOption)
            .lazyAppendedAll(creep.room.terminal.toOption)
            .lazyAppendedAll(creep.room.containers
                .filter((c: StructureContainer) => c.memory.deliverable.asInstanceOf[js.UndefOr[Boolean]].getOrElse(false)))
            .find(_.store.getFreeCapacity(ResourceType.Energy).getOrElse(0) > 0)
            .map(s =>
                Some(creep.transfer(s, ResourceType.Energy))
                    .filterNot(_ == Error.NotInRange.id)
                    .getOrElse(creep.travelTo(s.pos, literal(stuckValue = 1))))
            .getOrElse(println("ups has nowhere to deliver"))

    override def collect(creep: Creep): Boolean = {
        // TODO find operations are supposedly more expensive than looks, so replace?
        val cans = creep.room.containers
            .filterNot(_.memory.deliverable.asInstanceOf[UndefOr[Boolean]].getOrElse(false))
        val tombs = List.from(creep.room.find(Find.Tombstones).asInstanceOf[js.Array[Tombstone]])
        val drops = List.from(creep.room.find(Find.DroppedResources).asInstanceOf[js.Array[Resource]])

        val cansM = cans.map(c => (c.store(ResourceType.Energy).getOrElse(0), c.pos, Left(Left(c))))
        val tombsM = tombs.map(t => (t.store(ResourceType.Energy).getOrElse(0), t.pos, Left(Right(t))))
        val dropsM = drops.filter(_.resourceType == ResourceType.Energy.name).map(d => (d.amount * 5 / 4, d.pos, Right(d)))
        val all = cansM.appendedAll(tombsM).appendedAll(dropsM)

        all.maxByOption { case (nrgs, pos, _) => nrgs / Math.pow(3 + creep.pos.getRangeTo(pos), 1.5) }(Ordering.Double.IeeeOrdering)
            .exists { case (_, pos, thing) => {
                if (!creep.pos.isNearTo(pos)) creep.travelTo(pos)
                else thing match {
                    case Right(drop) => creep.pickup(drop)
                    case Left(Left(can)) => creep.withdraw(can, ResourceType.Energy)
                    case Left(Right(tomb)) => creep.withdraw(tomb, ResourceType.Energy)
                }
                true
            }
            }
    }

    def oldcollect(creep: Creep): Boolean = {
        // NOTE: ups excludes "deliverable=true" structures
        // TODO this code (and its source) should be cleaned up
        val filter: js.Function1[Structure, Boolean] = (s: Structure) => {
            allowedEnergyCollections.contains(s.structureType) &&
                !s.asInstanceOf[OwnedStructureWithStorage].memory.deliverable.asInstanceOf[UndefOr[Boolean]].getOrElse(false) &&
                super.storeHasEnoughEnergy(creep, s).getOrElse(false)
        }
        val container = creep.pos.findClosestByRange(Find.Structures, FindOptions[Structure](f = filter)).asInstanceOf[UndefOr[Structure]]
        for (c <- container) {
            val err = creep.withdraw(c, ResourceType.Energy)
            if (err == Error.OK.id) {
                return true
            } else if (err == Error.NotInRange.id) {
                creep.travelTo(c.pos, literal(stuckValue = 1))
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
                creep.travelTo(d.pos, literal(stuckValue = 1))
                return true
            }
        }

        false
    }


    override def startAction(creep: Creep): Unit = creep.say("\uD83D\uDE9A deliver")
}
