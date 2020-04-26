package com.squid314.screeps.roles

import scalajs.js
import com.screeps.native._
import com.screeps.native.Constants._

import scala.scalajs.js.Dynamic.literal

import com.squid314.screeps.ext._

object Upgrader extends ToggleEnergyWorker {
    def work(creep: Creep): Unit = {
        // take loaded energy to controller
        creep.room.controller
            .map(controller =>
                if (creep.upgradeController(controller) == Error.NotInRange.id)
                    creep.travelTo(controller.pos, literal(range = 3, visualizePathStyle = literal(stroke = "#FFFFFF")))
                else Error.OK.id)
            // how did an upgrader creep end up in a room without a controller?
            .getOrElse(creep.suicide())
    }

    override def startAction(creep: Creep): Unit = creep.say("⚡ upgrade")
}
