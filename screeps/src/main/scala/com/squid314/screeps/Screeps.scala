package com.squid314.screeps

import language.implicitConversions
import com.screeps.native.Constants._
import com.screeps.native.Constants.Bodypart._
import com.screeps.native._
import com.squid314.screeps.economy.{Assessment, Need, RoleNeed}
import com.squid314.screeps.economy.Assessment.State._
import com.squid314.screeps.roles._
import com.squid314.screeps.proto._

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{literal => l}
import scala.scalajs.js.UndefOr
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.util.Try

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
            val a = Assessment.assess(room)
            val needs: List[Need] = a.state match {
                case Initializing =>
                    //val needs = "miner" :: "miner" :: "miner" :: "miner" :: Nil
                    // this is kind of hacky, i think, but whatever
                    val needs =
                    //RoleNeed("miner", List(Work, Work, Move), "miner1", l(target = l(id = "9263077296e02bb", pos = new RoomPosition(37, 7, "W7N3")))) ::
                        RoleNeed("miner", List(Work, Work, Work, Work, Work, Move), "miner1", l(target = l(id = "9263077296e02bb", pos = new RoomPosition(37, 7, "W7N3")))) ::
                            RoleNeed("ups", List(Carry, Carry, Carry, Move, Move, Move), "ups1") ::
                            RoleNeed("builder", List(Work, Carry, Move, Move), "builder1") ::
                            RoleNeed("upgrader", List(Work, Carry, Move, Move), "upgrader1") ::
                            RoleNeed("builder", List(Work, Carry, Move, Move), "builder2") ::
                            RoleNeed("upgrader", List(Work, Work, Work, Work, Carry, Move), "upgrader2") ::
                            Nil
                    needs
                case Fledgling =>
                    val needs =
                        RoleNeed("miner", List(Work, Work, Work, Work, Work, Move), "miner1", l(target = l(id = "9263077296e02bb", pos = new RoomPosition(37, 7, "W7N3")))) ::
                            RoleNeed("ups", List(Carry, Carry, Carry, Carry, Move, Move), "ups1") ::
                            RoleNeed("miner", List(Work, Work, Work, Work, Work, Move), "miner2", l(target = l(id = "c12d077296e6ac9", pos = new RoomPosition(41, 4, "W7N3")))) ::
                            RoleNeed("builder", List(Work, Work, Work, Work, Carry, Carry, Carry, Carry, Move, Move, Move, Move), "builder1") ::
                            RoleNeed("upgrader", List(Work, Work, Work, Work, Work, Work, Work, Work, Work, Work, Carry, Move), "upgrader1") ::
                            RoleNeed("ups", List(Carry, Carry, Carry, Carry, Move, Move), "ups2") ::
                            //RoleNeed("builder", List(Work, Work, Work, Work, Carry, Carry, Carry, Carry, Move, Move, Move, Move), "builder2") ::
                            //RoleNeed("upgrader", List(Work, Work, Work, Work, Work, Work, Work, Work, Carry, Move), "upgrader2") ::
                            //RoleNeed("ups", List(Carry, Carry, Carry, Move, Move, Move), "ups3") ::
                            Nil
                    needs
                case Expanding => ???
                case Producing => ???
            }
            Direction.apply(3)
            println(s"room.needs: ${needs.map(_.id)}")
            needs
                .find(need => {
                    // find the first need for which there is no creep set to be handling the need
                    !Game.creeps.values.toList
                        .exists(creep =>
                            creep.memory.handling.asInstanceOf[UndefOr[String]]
                                .map(_ == need.id).getOrElse(false))
                })
                .map(need => {
                    println(s"trying to find spawn among: ${room.spawns}")
                    room.spawns
                        .find(spawn => {
                            println(s"checking spawn: $spawn")
                            println(s"checking spawn.spawning: ${spawn.spawning}")
                            spawn.spawning == null
                        })
                        .map(s => need.`type` match {
                            case Need.Role =>
                                val roleNeed = need.asInstanceOf[RoleNeed]
                                println(s"attempting to spawn for unsatisfied roleNeed: $roleNeed")
                                val mem = roleNeed.otherMem
                                mem.role = roleNeed.roleName
                                mem.handling = roleNeed.id
                                val err = s.spawnCreep(roleNeed.body.toJSArray, roleNeed.id, SpawnOptions(mem))
                                if (err != Error.OK.id) println(s"error: $err   ${Error.values.find(_.id == err)}")
                            case Need.Task =>
                                s spawnCreep(null, null, null)
                        })
                        .getOrElse(())
                })
                .getOrElse(())
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
