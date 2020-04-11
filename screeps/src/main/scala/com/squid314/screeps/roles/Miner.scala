package com.squid314.screeps.roles

import com.screeps.native.Constants._
import com.screeps.native._

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("TargetHarvest")
class TargetHarvest(
                       val id: String,
                       val pos: RoomPosition,
                       val containerId: UndefOr[String],
                       val constructionId: UndefOr[String],
                   ) extends js.Object

/** This role is specifically one which mines [[com.screeps.native.Source]]s in a room. */
object Miner extends Role {
    def run(creep: Creep): Unit = {
        g.console.log("doing miner things")
        val targetHarvest = creep.memory.targetHarvest.asInstanceOf[UndefOr[TargetHarvest]]
        g.console.log(s"targetHarvest: $targetHarvest")

        if (targetHarvest.isEmpty) {
            g.console.log("miner has nothing to do :(")
        } else for (target <- targetHarvest) {
            if (creep.pos.x != target.pos.x || creep.pos.y != target.pos.y) {
                g.console.log("need to move to target position")
                if (creep.pos.isNearTo(target.pos)) {
                    g.console.log("i am near!!")
                    creep.move(creep.pos.getDirectionTo(target.pos.x, target.pos.y))
                } else {
                    g.console.log("i am far")
                    val err = creep.moveTo(target.pos.x, target.pos.y)
                    g.console.log(s"error: $err   ${Error.values.find(_.id == err)}")
                }
            } else {
                g.console.log("miner is at source, need to mine")
                for (constructionId <- target.constructionId) {
                    g.console.log(s"miner has construction id: $constructionId")
                    for (energy <- creep.store(ResourceType.Energy)) {
                        // unboosted work body parts construct at 5 energy units per tick
                        // unboosted work body parts harvest at 2 energy units per tick
                        // these 2 conditions check if all of a build can be used or part of a harvest will be dropped
                        val workParts = creep.getActiveBodyparts(BodypartType.Work)
                        if (energy >= workParts * 5 ||
                            creep.store.getFreeCapacity(ResourceType.Energy).get <= workParts * 2) {
                            g.console.log("miner has energy, will construct container")
                            val construction = Game.getObjectById(constructionId).asInstanceOf[ConstructionSite]
                            val err = creep.build(construction)
                            g.console.log(s"error: $err   ${Error.values.find(_.id == err)}")
                            if (construction.progressTotal - construction.progress < Math.min(workParts * 5, energy)) {
                                g.console.log("finished construction, does the object id change between the construction site and the real container?")
                                creep.memory.targetHarvest = new TargetHarvest(target.id, target.pos, target.constructionId, js.undefined)
                            }
                        } else {
                            g.console.log("miner needs energy, will harvest")
                            val err = creep.harvest(Game.getObjectById(target.id).asInstanceOf[Source])
                            g.console.log(s"error: $err   ${Error.values.find(_.id == err)}")
                        }
                    }
                }
                for (containerId <- target.containerId) {
                    g.console.log(s"miner has container id: $containerId")
                    val err = creep.harvest(Game.getObjectById(target.id).asInstanceOf[Source])
                    g.console.log(s"error: $err   ${Error.values.find(_.id == err)}")
                    val container = Game.getObjectById(containerId).asInstanceOf[StructureContainer]
                    val err2 = creep.transfer(container, ResourceType.Energy)
                    g.console.log(s"error: $err2   ${Error.values.find(_.id == err2)}")
                }
            }
        }
    }

    def findUnclaimedSource(room: Room): UndefOr[TargetHarvest] = {

    }
}
