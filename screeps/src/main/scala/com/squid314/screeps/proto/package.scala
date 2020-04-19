package com.squid314.screeps

import scalajs.js
import scalajs.js._
import com.screeps.native._
import com.screeps.native.Constants._
import com.squid314.screeps.macros.Macros
import com.squid314.screeps.Config

import scala.language.experimental.macros
import scala.reflect._
import scala.reflect.macros._
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

package object proto {

    /** This extension provides access to additional attributes without mangling the original facade. */
    @js.native
    trait RoomExtension extends Room {
        var _sources: UndefOr[js.Array[Source]] = js.native
        var _mineral: UndefOr[Mineral] = js.native
        var _keepers: UndefOr[js.Array[StructureKeeperLair]] = js.native
        var _spawns: UndefOr[js.Array[StructureSpawn]] = js.native
        var _extensions: UndefOr[js.Array[StructureExtension]] = js.native
        var _labs: UndefOr[js.Array[StructureLab]] = js.native
        var _containers: UndefOr[js.Array[StructureContainer]] = js.native
        var _links: UndefOr[js.Array[StructureLink]] = js.native
        var _towers: UndefOr[js.Array[StructureTower]] = js.native
        var _nuker: UndefOr[StructureNuker] = js.native
        var _observer: UndefOr[StructureObserver] = js.native
    }

    @JSExportTopLevel("StructureCache")
    class StructureCache(val maxAge: Int = 0, val ids: js.Array[String] = js.Array()) extends js.Object

    implicit class RoomOps(r: Room) {
        private val room = r.asInstanceOf[RoomExtension]

        def sources(): js.Array[Source] = {
            // TODO simplify? generify?
            if (room._sources.isEmpty) {
                val sourceIds = room.memory.sourceIds.asInstanceOf[UndefOr[js.Array[String]]]
                if (sourceIds.isDefined) {
                    room._sources = sourceIds.get
                        .flatMap(id => Game.getObjectById(id)
                            .asInstanceOf[UndefOr[Source]].toOption)
                        .asInstanceOf[UndefOr[js.Array[Source]]]
                } else {
                    val sources = room.find(Find.Sources).asInstanceOf[js.Array[Source]]
                    room.memory.sourceIds = sources.map(_.id)
                    room._sources = sources.asInstanceOf[UndefOr[js.Array[Source]]]
                }
            }

            room._sources.get
        }

        def mineral(): UndefOr[Mineral] = {
            js.undefined
        }

        val keepers: () => js.Array[StructureKeeperLair] = ???
//            () => Helper.structurePropertize(room._keepers)

        def spawns() = Macros.structuresCached(room._spawns, StructureType.Spawn)
        def extensions() = Macros.structuresCached(room._extensions, StructureType.Extension)
        def labs() = Macros.structuresCached(room._labs, StructureType.Lab)
        def containers() = Macros.structuresCached(room._containers, StructureType.Container)
        def links() = Macros.structuresCached(room._links, StructureType.Link)
        def towers() = Macros.structuresCached(room._towers, StructureType.Tower)
        def nuker() = Macros.structureCached(room._nuker, StructureType.Nuker)
        def observer() = Macros.structureCached(room._observer, StructureType.Observer)
    }
}
