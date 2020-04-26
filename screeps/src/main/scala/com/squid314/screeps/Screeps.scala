package com.squid314.screeps

import language.implicitConversions
import com.screeps.native.Constants._
import com.screeps.native._
import com.squid314.screeps.economy.{Assessment, Need, RoleNeed}
import com.squid314.screeps.economy.Assessment.State._
import com.squid314.screeps.roles._
import com.squid314.screeps.proto._

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.JSExportTopLevel

object Screeps {
    @JSExportTopLevel("loop")
    def loop(): Unit = {
        memoryMaintenance() // maybe this will become uncommon at some point
        executeUncommonTasks()

        println(s"time: ${Game.time}")

        val origin = Game.rooms.values.head.getPositionAt(1, 2)
        //        PathFinder.search(origin,
        //            RangeGoal(Game.spawns.values.head, 3),
        //            PathFinderOptions()
        //                .withSwampCost(9)
        //                .withMaxOps(400))

        for (room <- Game.spawns.values.toList.map(_.room).distinct) {
            val a = Assessment(room)
            val needs: List[Need] = a.state match {
                case Initializing =>
                    //val needs = "miner" :: "miner" :: "miner" :: "miner" :: Nil
                    // this is kind of hacky, i think, but whatever
                    val needs =
                        RoleNeed("miner",List(Bodypart.Work, Bodypart.Work, Bodypart.Move), "miner1") ::
                        RoleNeed("miner",List(Bodypart.Work, Bodypart.Work, Bodypart.Move), "miner1") ::
                        Nil
                    needs
                case Fledgling =>
                    val needs = List()
                    needs
                case Expanding => ???
                case Producing => ???
            }
            println(s"room.minerals: ${room.minerals}")
            println(s"room.spawns: ${room.spawns}")
        }

        for ((_, creep) <- Game.creeps) {
            if (!creep.spawning) {
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
        "builder" -> Builder,
        "ups" -> Ups,
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
