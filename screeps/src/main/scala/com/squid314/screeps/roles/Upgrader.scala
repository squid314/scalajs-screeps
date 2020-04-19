package com.squid314.screeps.roles

import scalajs.js
import com.screeps.native._
import com.screeps.native.Constants._

import scala.scalajs.js.Dynamic.literal

object Upgrader extends ToggleWorker {
    def work(creep: Creep): Unit = {
        // take loaded energy to controller
        creep.room.controller
            .map(controller =>
                if (creep.upgradeController(controller) == Error.NotInRange.id) {
                    creep.moveTo(controller.pos, literal(visualizePathStyle = literal(stroke= "#FFFFFF")))
                }
            )
            // how did an upgrader creep end up in a room without a controller?
            .getOrElse(creep.suicide())
    }


    override def startAction(creep: Creep): Unit = creep.say("⚡ upgrade")

    override def stopAction(creep: Creep): Unit = creep.say("\uD83D\uDD04 collect")
}
