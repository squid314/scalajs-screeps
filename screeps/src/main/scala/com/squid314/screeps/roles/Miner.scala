package com.squid314.screeps.roles

import com.screeps.native.Constants._
import com.screeps.native._

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.JSExportTopLevel

import com.squid314.screeps.ext._
import com.squid314.screeps.proto._

/** This role is specifically one which mines [[com.screeps.native.Source]]s in a room. */
object Miner extends Role {
    def run(creep: Creep): Unit = {
        val targetHarvest = creep.memory.target.asInstanceOf[UndefOr[Target]]

        if (targetHarvest.isEmpty) {
        } else for (target <- targetHarvest) {
            val targetPos = new RoomPosition(target.pos.x, target.pos.y, target.pos.roomName)
            if (creep.pos.roomName != targetPos.roomName) {
                creep.travelTo(targetPos)
            } else if (creep.pos.x != targetPos.x || creep.pos.y != targetPos.y) {
                if (creep.pos.isNearTo(targetPos)) {
                    creep.move(creep.pos.getDirectionTo(targetPos.x, targetPos.y))
                    // TODO original reason was to send in a reinforcement as soon as it may be able to replace the current miner
                } else {
                    creep.travelTo(targetPos)
                }
            } else {
                val err = creep.harvest(Game.getObjectById(target.id).asInstanceOf[Source])
                if (err != Error.OK.id)
                    println(s"err:  $err   ${Error.values.find(_.id == err)}")
            }
        }
    }

    @JSExportTopLevel("Miner_Target")
    class Target(
                    val id: String,
                    val pos: RoomPosition,
                ) extends js.Object

}
