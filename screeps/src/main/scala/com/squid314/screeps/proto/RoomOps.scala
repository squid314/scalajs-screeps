package com.squid314.screeps.proto

import com.screeps.native.Constants._
import com.screeps.native._
import com.squid314.screeps.macros.Macros

import scala.language.experimental.macros
import scala.scalajs.js
import scala.scalajs.js.{UndefOr, |}
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

final class RoomOps(r: Room) {
    private val room = r.asInstanceOf[RoomExtension]

    def sources: List[Source] =
        room._sources.getOrElse(
            room.memory.sourceIds.asInstanceOf[UndefOr[js.Array[String]]]
                .map(List.from(_))
                .map(ids => {
                    val ss = ids.map(id => Game.getObjectById(id).get.asInstanceOf[Source])
                    room._sources = ss.asInstanceOf[UndefOr[List[Source]]]
                    ss
                })
                .getOrElse({
                    val sources = room.find(Find.Sources).asInstanceOf[js.Array[Source]]
                    room.memory.sourceIds = sources.map(_.id)
                    val listSources = List.from(sources)
                    room._sources = listSources.asInstanceOf[UndefOr[List[Source]]]
                    listSources
                }))

    def minerals: List[Mineral] =
        room._minerals.getOrElse(
            room.memory.mineralIds.asInstanceOf[UndefOr[js.Array[String]]]
                .map(List.from(_))
                .map(ids => {
                    val ss = ids.map(id => Game.getObjectById(id).get.asInstanceOf[Mineral])
                    room._minerals = ss.asInstanceOf[UndefOr[List[Mineral]]]
                    ss
                })
                .getOrElse({
                    val minerals = room.find(Find.Minerals).asInstanceOf[js.Array[Mineral]]
                    room.memory.mineralIds = minerals.map(_.id)
                    val listMinerals = List.from(minerals)
                    room._minerals = listMinerals.asInstanceOf[UndefOr[List[Mineral]]]
                    listMinerals
                }))

    def keepers: List[StructureKeeperLair] =
        room._keepers.getOrElse(
            room.memory.keeperIds.asInstanceOf[UndefOr[js.Array[String]]]
                .map(List.from(_))
                .map(ids => {
                    val ss = ids.map(id => Game.getObjectById(id).get.asInstanceOf[StructureKeeperLair])
                    room._keepers = ss.asInstanceOf[UndefOr[List[StructureKeeperLair]]]
                    ss
                })
                .getOrElse({
                    val filter = (s: Structure) => s.structureType == StructureType.KeeperLair.name
                    val keepers = room.find(Find.Structures, FindOptions(filter)).asInstanceOf[js.Array[StructureKeeperLair]]
                    room.memory.keeperIds = keepers.map(_.id)
                    val listKeepers = List.from(keepers)
                    room._keepers = listKeepers.asInstanceOf[UndefOr[List[StructureKeeperLair]]]
                    listKeepers
                }))

    def spawns = Macros.structuresCached(room._spawns, StructureType.Spawn)

    def extensions = Macros.structuresCached(room._extensions, StructureType.Extension)

    def labs = Macros.structuresCached(room._labs, StructureType.Lab)

    def containers = Macros.structuresCached(room._containers, StructureType.Container)

    def links = Macros.structuresCached(room._links, StructureType.Link)

    def towers = Macros.structuresCached(room._towers, StructureType.Tower)

    def nuker = Macros.structureCached(room._nuker, StructureType.Nuker)

    def observer = Macros.structureCached(room._observer, StructureType.Observer)
}

/** This extension provides access to additional attributes without mangling the original facade. */
@js.native
trait RoomExtension extends Room {
    var _sources: UndefOr[List[Source]] = js.native
    var _minerals: UndefOr[List[Mineral]] = js.native
    var _keepers: UndefOr[List[StructureKeeperLair]] = js.native
    var _spawns: UndefOr[List[StructureSpawn]] = js.native
    var _extensions: UndefOr[List[StructureExtension]] = js.native
    var _labs: UndefOr[List[StructureLab]] = js.native
    var _containers: UndefOr[List[StructureContainer]] = js.native
    var _links: UndefOr[List[StructureLink]] = js.native
    var _towers: UndefOr[List[StructureTower]] = js.native
    var _nuker: UndefOr[Option[StructureNuker]] = js.native
    var _observer: UndefOr[Option[StructureObserver]] = js.native
}

/**
 * Cache of ids of structures in a room.
 *
 * @param maxAge Age after which a room should look for updated structures.
 * @param ids    The ids of the structures in the room.
 */
@JSExportTopLevel("StructureCache")
class StructureCache(
                        val maxAge: Int,
                        val ids: js.Array[String],
                    ) extends js.Object

object StructureCache {
    def apply(maxAge: Int, ids: js.Array[String]): StructureCache = new StructureCache(maxAge, ids)
}
