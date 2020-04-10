package com.squid314.screeps

import com.screeps.native._
import com.screeps.native.Constants._

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.annotation.JSExportTopLevel

object Screeps {
    @JSExportTopLevel("loop")
    def loop(): Unit = {
        memoryMaintenance() // maybe this will become uncommon at some point
        executeUncommonTasks()

        println(s"time: ${Game.time}")
//        println(s"forcing type usage: ${BodypartType.Work} ${Bodyparts.Work}")
//        println(s"forcing type usage: ${BodypartType.Move} ${Bodyparts.Move}")
//        println(s"forcing type usage: ${BodypartType.Carry} ${Bodyparts.Carry}")
        val s = Game.spawns("Spawn1")
        println(s)
        if (s.store(ResourceType.Energy).getOrElse(0) >= 300) {
            println("trying to spawn")
            var foo: js.Array[String @@ BodypartType] = js.Array(BodypartType.Work.name, BodypartType.Work.name, BodypartType.Move.name, BodypartType.Carry.name)
            val err = s.spawnCreep(foo)
            println(s"error: ${Error.values.find(_.id == err)}")
            println(foo)
        }

        val origin = Game.rooms.values.head.getPositionAt(1, 2)
        PathFinder.search(origin,
            RangeGoal(Game.spawns.values.head, 3),
            new PathFinderOpts(
                swampCost = Some(9),
                maxOps = Some(400),
            ))

        for ((name, spawn) <- Game.spawns) {
            println(s"spawn: $name -> ${spawn.store(ResourceType.Energy)}")
            // TODO assess economy of spawn
        }
        for ((name, creep) <- Game.creeps) {
            println(s"creep: $name -> $creep: ${creep.ticksToLive}")
        }
    }

    def executeUncommonTasks(): Unit = {
        val check = Game.time & 0xF

        uncommonTasks.get(check)
            .foreach(f => f())
    }

    val uncommonTasks: scala.collection.immutable.Map[Int, () => Unit] = scala.collection.immutable.Map(
        0 -> repairScan,
        1 -> marketeering,
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
