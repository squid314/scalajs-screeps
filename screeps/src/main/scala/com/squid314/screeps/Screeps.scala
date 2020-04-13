package com.squid314.screeps

import com.screeps.native.Constants._
import com.screeps.native._
import com.squid314.screeps.economy.Assessment
import com.squid314.screeps.roles.{Miner, Role}

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{literal, global => g}
import scala.scalajs.js.annotation.JSExportTopLevel

object Screeps {
    @JSExportTopLevel("loop")
    def loop(): Unit = {
        memoryMaintenance() // maybe this will become uncommon at some point
        executeUncommonTasks()

        g.console.log(s"time: ${Game.time}")
        //g.console.log(s"forcing type usage: ${BodypartType.Work} ${Bodyparts.Work}")
        //g.console.log(s"forcing type usage: ${BodypartType.Move} ${Bodyparts.Move}")
        //g.console.log(s"forcing type usage: ${BodypartType.Carry} ${Bodyparts.Carry}")
        val s = Game.spawns("Spawn1")

        //if (s.store(ResourceType.Energy).getOrElse(0) >= 300) {
        //    val err = s.spawnCreep(js.Array(BodypartType.Work, BodypartType.Work, BodypartType.Carry, BodypartType.Move), "name", SpawnOptions(memory = literal(role = "miner")))
        //}

        val origin = Game.rooms.values.head.getPositionAt(1, 2)
        PathFinder.search(origin,
            RangeGoal(Game.spawns.values.head, 3),
            new PathFinderOpts(
                swampCost = Some(9),
                maxOps = Some(400),
            ))

        for (room <- Game.spawns.values.toList.map(_.room).distinct) {
            val filter: Structure => Boolean = (s: Structure) =>
                s.structureType == StructureType.Spawn.name || s.structureType == StructureType.Extension.name
//            for (struct: Structure <- room.find(FindType.MyStructures,
//                /*ugh*/ js.Dynamic.literal(filter = filter))
//                .asInstanceOf[js.Array[Structure]]) {
//            }
            for (struct: Structure <- room.findMyStructures()) {
                g.console.log(s"my structure: $struct")
            }
            Assessment(room)
        }
        for ((name, creep) <- Game.creeps) {
            if (creep.spawning)
                g.console.log(s"creep: $name -> $creep: spawning")
            else {
                g.console.log(s"creep: $name -> $creep: ${creep.ticksToLive}")
                if (creep.memory.hasOwnProperty("role").asInstanceOf[Boolean]) {
                    roles.get(creep.memory.role.asInstanceOf[String])
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
        "miner" -> Miner
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
