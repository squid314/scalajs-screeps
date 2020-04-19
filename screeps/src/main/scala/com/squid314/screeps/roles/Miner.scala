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
        val targetHarvest = creep.memory.targetHarvest.asInstanceOf[UndefOr[TargetHarvest]]

        if (targetHarvest.isEmpty) {
            g.console.log("miner has nothing to do :(")
        } else for (target <- targetHarvest) {
            if (creep.pos.x != target.pos.x || creep.pos.y != target.pos.y) {
                if (creep.pos.isNearTo(target.pos)) {
                    creep.move(creep.pos.getDirectionTo(target.pos.x, target.pos.y))
                } else {
                    val err = creep.moveTo(target.pos.x, target.pos.y)
                }
            } else {
                if (creep.store.getFreeCapacity().get > 0) {
                    for (resource <- creep.pos.lookFor(Look.Resources).asInstanceOf[js.Array[Resource]]) {
                        creep.pickup(resource)
                    }
                }
                val isContainer = ((s: Structure) => s.structureType == StructureType.Container).asInstanceOf[js.Object => Boolean]
/*
                val newContainer: UndefOr[StructureContainer] = for {
                    constructionId <- target.constructionId
                } yield if (Game.getObjectById(constructionId).isDefined) null
                else creep.pos.findInRange(FindType.Structures, 1, FindOptions(isContainer))
                    .asInstanceOf[js.Array[StructureContainer]]
                    .headOption.orNull // we'll see if `null` will count as undefined for UndefOr[
*/
                if (target.containerId.isEmpty && target.constructionId.isEmpty) {
                    // finished construction, need to find the constructed container
                    g.console.log(s"miner look for nearby container")
                    val cons = creep.pos.findInRange(Find.Structures, 1, FindOptions(isContainer)).asInstanceOf[js.Array[StructureContainer]]
                    g.console.log(s"containers: $cons")
                    if (!cons.isEmpty) for (container <- cons.headOption) {
                        creep.memory.targetHarvest.containerId = container.id
                        // TODO creep will waste one tick of not knowing where to put its load, and only when the container is constructed, maybe i don't care
                        //target = new TargetHarvest(target.id, target.pos, container.id, js.undefined)
                    } else {
                        val filter = ((s: ConstructionSite) => s.structureType == StructureType.Container).asInstanceOf[js.Object => Boolean]
                        val cons = creep.pos.findInRange(Find.ConstructionSites, 1, FindOptions(filter)).asInstanceOf[js.Array[StructureContainer]]
                        g.console.log(s"constructions: $cons")
                        if (!cons.isEmpty) for (construction <- cons.headOption) {
                            creep.memory.targetHarvest.constructionId = construction.id
                        } else {
                            g.console.log("no construction or containers around, just mine")
                            val err = creep.harvest(Game.getObjectById(target.id).asInstanceOf[Source])
                            g.console.log(s"error: $err  ${Error.values.find(_.id == err)}")
                        }
                    }
                }
                for (constructionId <- target.constructionId) {
                    for (energy <- creep.store(ResourceType.Energy)) {
                        // unboosted work body parts construct at 5 energy units per tick
                        // unboosted work body parts harvest at 2 energy units per tick
                        // these 2 conditions check if all of a build can be used or part of a harvest will be dropped
                        // if so, it executes a build, otherwise it executes a harvest
                        val workParts = creep.getActiveBodyparts(Bodypart.Work)
                        if (energy >= workParts * 5 ||
                            creep.store.getFreeCapacity(ResourceType.Energy).get <= workParts * 2) {
                            g.console.log("miner has energy, will construct container")
                            val construction = Game.getObjectById(constructionId).asInstanceOf[ConstructionSite]
                            val err = creep.build(construction)
                            g.console.log(s"error: $err   ${Error.values.find(_.id == err)}")
                            // TODO if we finished, the id changes, hooray!
                            // TODO double hooray, if 2 miners work a single can (first tier), then this trigger doesn't work
                            if (construction.progressTotal - construction.progress < Math.min(workParts * 5, energy)) {
                                g.console.log("finished construction, does the object id change between the construction site and the real container?")
                                creep.memory.targetHarvest = new TargetHarvest(target.id, target.pos, target.constructionId, js.undefined)
                            }
                        } else {
                            g.console.log("miner needs energy, will harvest")
                            val err = creep.harvest(Game.getObjectById(target.id).orNull.asInstanceOf[Source])
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
