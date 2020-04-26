package com.squid314.screeps.roles

import com.screeps.native._
import com.screeps.native.Constants._

import scala.scalajs.js
import com.squid314.screeps.ext._

import scala.scalajs.js.Dynamic.literal

object Builder extends ToggleEnergyWorker {
    def work(creep: Creep): Unit = {
        if (!(attemptBuild(creep)))
            Upgrader.work(creep)
    }

    def checkMemorizedWork(creep: Creep): Boolean = ???

    // since each shard is limited to 100 construction sites, this is probably fast
    def attemptBuild(creep: Creep): Boolean =
        Game.constructionSites
            .values
            .to(LazyList.iterableFactory)
            .filter(_.pos.roomName == creep.pos.roomName)
            .sortBy(creep.pos.getRangeTo(_))
            .map(c => {
                val err = creep.build(c)
                if (err == Error.OK.id) true
                else if (err == Error.NotInRange.id) {
                    creep.travelTo(c.pos, literal(range = 3))
                    true
                }
                // should this actually be None to try other sites?
                else false
            })
            .headOption
            .getOrElse(false)

    /*
        def attemptRepair(creep: Creep): Boolean = {
            for (repairIds <- creep.room.memory.repairIds.asInstanceOf[js.UndefOr[js.Array[String]]]) {
                for (closestRepair <- repairIds
                    .flatMap(Game.getObjectById(_).asInstanceOf[js.UndefOr[Structure]].toOption)
                    .sort(Util.closerSort(creep))
                    .headOption) {
                    val err = creep.repair(closestRepair)
                    if (err == Error.OK.id) {
                        return true
                    } else if (err == Error.NotInRange.id) {
                        creep.travelTo(closestRepair.pos)
                        return true
                    }
                }
            }
            false
        }
    */

    override def startAction(creep: Creep): Unit = creep.say("\uD83D\uDEA7 build")
}
