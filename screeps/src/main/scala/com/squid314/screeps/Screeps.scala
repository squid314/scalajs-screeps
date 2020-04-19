package com.squid314.screeps

import language.implicitConversions

import com.screeps.native.Constants._
import com.screeps.native._
import com.squid314.screeps.economy.Assessment
import com.squid314.screeps.roles.{Miner, Role, Upgrader}
import com.squid314.screeps.proto.RoomOps

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.JSExportTopLevel

object Screeps {
    @JSExportTopLevel("loop")
    def loop(): Unit = {
        memoryMaintenance() // maybe this will become uncommon at some point
        executeUncommonTasks()

        g.console.log(s"time: ${Game.time}")

        val origin = Game.rooms.values.head.getPositionAt(1, 2)
        PathFinder.search(origin,
            RangeGoal(Game.spawns.values.head, 3),
            new PathFinderOpts(
                swampCost = Some(9),
                maxOps = Some(400),
            ))

        for (room <- Game.spawns.values.toList.map(_.room).distinct) {
            //            val a = Assessment(room)
            //            if (a.state == Fledgling)
            println(s"sources: ${room.sources()}")
            println(s"keepers: ${room.keepers()}")
        }

        for ((name, creep) <- Game.creeps) {
            if (creep.spawning)
                g.console.log(s"creep: $name -> $creep: spawning")
            else {
                g.console.log(s"creep: $name -> $creep: ${creep.ticksToLive}")
                for (role <- creep.memory.role.asInstanceOf[UndefOr[String]]) {
                    roles.get(role)
                        .foreach(_.run(creep))
                }
            }
        }
    }

    def executeUncommonTasks(): Unit = {
        val check = Game.time & 0xF

        uncommonTasks.drop(check).headOption
            .foreach(_ ())
    }

    val uncommonTasks: js.Array[() => Unit] = js.Array(
        repairScan,
        marketeering,
    )

    val roles: js.Dictionary[Role] = js.Dictionary(
        "miner" -> Miner,
        "upgrader" -> Upgrader,
    )

    def repairScan(): Unit = {
        // TODO this
    }

    def marketeering(): Unit = {
        // TODO this
    }

    def memoryMaintenance(): Unit = {
        def cleanMemory[T, U](mem: js.Dictionary[T], game: js.Dictionary[U]): Unit = {
            mem.keys
                .filterNot(game.contains)
                .foreach { name: String => mem.remove(name) }
        }

        cleanMemory(Memory.creeps, Game.creeps)
        cleanMemory(Memory.spawns, Game.spawns)
        cleanMemory(Memory.rooms, Game.rooms)
        cleanMemory(Memory.flags, Game.flags)
    }
}
