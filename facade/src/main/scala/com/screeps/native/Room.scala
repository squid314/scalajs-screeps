package com.screeps.native

import com.screeps.native.Constants._

import scala.scalajs.js
import scala.scalajs.js.annotation._
import scala.scalajs.js.typedarray.Uint8Array
import scala.scalajs.js.|

@js.native
@JSGlobal
object Room extends js.Object {
    /**
     * Serialize a path array into a short string representation, which is suitable to store in memory.
     *
     * @param path A path array retrieved from Room.findPath
     * @return A serialized string form of the given path.
     * @example {{{var path = spawn.pos.findPathTo(source);
     *         Memory.path = Room.serializedPath(path);
     *         creep.moveByPath(Memory.path);}}}
     * @note CPU Cost: LOW
     */
    def serializePath(path: js.Array[RouteStep]): String = js.native

    /**
     * Deserialize a short string path representation into an array form.
     *
     * @param path A serialized path string.
     * @return A path array.
     * @example {{{var path = Room.deserializePath(Memory.path);
     *         creep.moveByPath(path);}}}
     * @note CPU Cost: LOW
     */
    def deserializePath(path: String): js.Array[RouteStep] = js.native

    /**
     * An object which provides fast access to room terrain data. These objects can be constructed for any room in the world even if you have no access to it.
     *
     * Technically every Room.Terrain object is a very lightweight adapter to underlying static terrain buffers with corresponding minimal accessors.
     *
     * @param roomName The room name.
     */
    @js.native
    class Terrain(val roomName: String) extends js.Object {
        /**
         * Get terrain type at the specified room position by {{{(x,y)}}} coordinates. Unlike the {{{Game.map.getTerrainAt(...)}}} method, this one doesn't perform any string operations and returns integer terrain type values (see below).
         *
         * @param x
         * @param y
         * @return One of the following integer values:
         *         0 - terrain is plain
         *         1 - terrain is wall - TERRAIN_MASK_WALL
         *         2 - terrain is swamp - TERRAIN_MASK_SWAMP
         */
        def get(x: Int, y: Int): Short = js.native

        /** Expermiental API, return may be adjusted or function deprecated at any time. */
        def getRawBuffer(destinationArray: Uint8Array = ???): Short | Uint8Array = js.native
    }
}

/**
 * An object representing the room in which your units and structures are in.
 * It can be used to look around, find paths, etc. Every object in the room
 * contains its linked Room instance in the room property.
 */
@js.native
trait Room extends js.Object {
    /** The Controller structure of this room, if present, otherwise undefined. */
    val controller: js.UndefOr[StructureController] = js.native
    /** Total amount of energy available in all spawns and extensions in the room. */
    val energyAvailable: Int = js.native
    /** Total amount of energyCapacity of all spawns and extensions in the room. */
    val energyCapacityAvailable: Int = js.native
    /** A shorthand to Memory.rooms[room.name]. You can use it for quick access the room’s specific memory data object. */
    val memory: Memory.RoomMemory = js.native
    /** The game mode, one of: Simulation, Survival, World, Arena */
    val mode: String = js.native
    /** The name of the room */
    val name: String = js.native
    /** The Storage structure of this room, if present, otherwise undefined. */
    val storage: js.UndefOr[StructureStorage] = js.native
    /** The Terminal structure of this room, if present, otherwise undefined. */
    val terminal: js.UndefOr[StructureTerminal] = js.native

    /**
     * Create new ConstructionSite at the specified location.
     *
     * @param x             The X position
     * @param y             The Y position
     * @param structureType One of the StructureType's
     * @return One of the following codes:
     *         OK - The operation has been scheduled successfully.
     *         InvalidTarget -	The structure cannot be placed at the specified location.
     *         Full - You have too many construction sites. The maximum number of construction sites per player is 100.
     *         InvalidArgs - The location is incorrect.
     *         RCLNotEnough - The Room Controller Level is not enough. Learn more
     * @note CPU Cost: CONST
     */
    def createConstructionSite(x: Int, y: Int, structureType: String): Short = js.native

    /**
     * Create new Flag at the specified location.
     *
     * @param x              The X position.
     * @param y              The Y position.
     * @param name           (optional) The name of a new flag. It should be unique, i.e. the Game.flags object should not contain
     *                       another flag with the same name (hash key). If not defined, a random name will be generated
     * @param color          (optional) The color of a new flag. Should be one of the COLOR_* constants. The default value is COLOR_WHITE
     * @param secondaryColor (optional) The secondary color of a new flag. Should be one of the COLOR_* constants. The default value is equal to color.
     * @return The name of a new flag, or one of the following error codes:
     *         NameExists - There is a flag with the same name already.
     *         InvalidArgs - The location or the color constant is incorrect.
     * @note CPU Cost: CONST
     */
    def createFlag(x: Int, y: Int, name: String = "", color: Int = Color.White.id, secondaryColor: Int = Color.White.id): js.Any = js.native

    /**
     * Find all objects of the specified type in the room.
     *
     * @param findType The type of object to find
     * @param opts     An object with additional options:
     *                 filter object, function, string
     *                 The result list will be filtered using the Lodash.filter method.
     * @return an array of objects found (return type is based on find type)
     * @note CPU Cost: MEDIUM
     */
    def find[T <: js.Object](findType: Int, opts: js.Object @@ FindOptions = js.native):
    js.Array[RoomPosition] |
        js.Array[Creep] |
        js.Array[Source] |
        js.Array[Resource] |
        js.Array[Structure] |
        js.Array[Flag] |
        js.Array[StructureSpawn] |
        js.Array[ConstructionSite] |
        js.Array[Mineral] |
        js.Array[Nuke] |
        js.Array[Tombstone]
    = js.native

    /**
     * Find the exit direction en route to another room.
     *
     * @param room Another room name or room object.
     * @return The room direction constant, one of the following:
     *         Top, Right, Bottom, Left
     *         Or one of the following error codes:
     *         NoPath - Path can not be found
     *         InvalidArgs - The location is incorrect
     * @note CPU Cost: HIGH
     */
    def findExitTo(room: String): Int = js.native

    /**
     * Find an optimal path inside the room between fromPos and toPos using
     * <a href="http://en.wikipedia.org/wiki/A*_search_algorithm">A* search algorithm</a> and
     *
     * @param fromPos the start position
     * @param toPos   the destination
     * @param opts    An object containing additonal pathfinding flags:
     *                ignoreCreeps boolean - Treat squares with creeps as walkable. Can be useful with too many moving creeps around or in some other cases. The default value is false.
     *                ignoreDestructibleStructures boolean - Treat squares with destructible structures (constructed walls, ramparts, spawns, extensions) as walkable. Use this flag when you need to move through a territory blocked by hostile structures. If a creep with an ATTACK body part steps on such a square, it automatically attacks the structure. The default value is false.
     *                ignoreRoads boolean - Ignore road structures. Enabling this option can speed up the search. The default value is false. This is only used when the new PathFinder is enabled.
     *                costCallback function(string, CostMatrix) - You can use this callback to modify a CostMatrix for any room during the search. The callback accepts two arguments, roomName and costMatrix. Use the costMatrix instance to make changes to the positions costs. If you return a new matrix from this callback, it will be used instead of the built-in cached one. This option is only used when the new PathFinder is enabled.
     *                ignore array - An array of the room's objects or RoomPosition objects which should be treated as walkable tiles during the search. This option cannot be used when the new PathFinder is enabled (use costCallback option instead).
     *                avoid array - An array of the room's objects or RoomPosition objects which should be treated as obstacles during the search. This option cannot be used when the new PathFinder is enabled (use costCallback option instead).
     *                maxOps number - The maximum limit of possible pathfinding operations. You can limit CPU time used for the search based on ratio 1 op ~ 0.001 CPU. The default value is 2000.
     *                heuristicWeight number - Weight to apply to the heuristic in the A* formula F = G + weight * H. Use this option only if you understand the underlying A* algorithm mechanics! The default value is 1.2.
     *                serialize boolean - If true, the result path will be serialized using Room.serializePath. The default is false.
     *                maxRooms number - The maximum allowed rooms to search. The default (and maximum) is 16. This is only used when the new PathFinder is enabled.
     * @return An array with path steps
     * @note CPU Cost HIGH
     */
    def findPath(fromPos: RoomPosition, toPos: RoomPosition, opts: PathFinderOptions = js.native): js.Array[PathStep] = js.native

    /**
     * Creates a RoomPosition object at the specified location.
     *
     * @param x The X position.
     * @param y The Y position.
     * @return A [RoomPosition] object or null if it cannot be obtained.
     * @note CPU Cost: LOW
     */
    def getPositionAt(x: Int, y: Int): RoomPosition = js.native

    /**
     * Get the list of objects at the specified room position.
     *
     * @param x X position in the room
     * @param y Y position in the room
     * @return An array with objects at the specified position in the following format: {{{
     *           [
     *             { type: 'creep', creep: {...} },
     *             { type: 'structure', structure: {...} },
     *             ...
     *             { type: 'terrain', terrain: 'swamp' }
     *           ]
     * }}}
     * @note CPU Cost: MEDIUM
     */
    def lookAt(x: Int, y: Int): js.Array[js.Object] = js.native

    /**
     * Get the list of objects at the specified room position.
     *
     * @param target the position to look at
     * @return An array with objects at the specified position in the following format: {{{
     *           [
     *             { type: 'creep', creep: {...} },
     *             { type: 'structure', structure: {...} },
     *             ...
     *             { type: 'terrain', terrain: 'swamp' }
     *           ]
     * }}}
     * @note CPU Cost: MEDIUM
     */
    def lookAt(target: RoomPosition): js.Array[js.Object] = js.native

    /**
     * Get the list of objects at the specified room area.
     *
     * @param top     The top Y boundary of the area.
     * @param left    The left X boundary of the area.
     * @param bottom  The bottom Y boundary of the area.
     * @param right   The right X boundary of the area.
     * @param asArray Set to true if you want to get the result as a plain array.
     * @return If asArray is set to false or undefined, the method returns an object with all the objects in the specified area in the following format: {{{
     *         // 10,5,11,7
     *
     *         {
     *           10: {
     *             5: [{ type: 'creep', creep: {...} },
     *                 { type: 'terrain', terrain: 'swamp' }],
     *             6: [{ type: 'terrain', terrain: 'swamp' }],
     *             7: [{ type: 'terrain', terrain: 'swamp' }]
     *           },
     *           11: {
     *             5: [{ type: 'terrain', terrain: 'normal' }],
     *             6: [{ type: 'structure', structure: {...} },
     *                 { type: 'terrain', terrain: 'swamp' }],
     *             7: [{ type: 'terrain', terrain: 'wall' }]
     *           }
     *         } }}}
     *         If asArray is set to true, the method returns an array in the following format:{{{
     *         [
     *           {x: 5, y: 10, type: 'creep', creep: {...}},
     *           {x: 5, y: 10, type: 'terrain', terrain: 'swamp'},
     *           {x: 6, y: 10, type: 'terrain', terrain: 'swamp'},
     *           {x: 7, y: 10, type: 'terrain', terrain: 'swamp'},
     *           {x: 5, y: 11, type: 'terrain', terrain: 'normal'},
     *           {x: 6, y: 11, type: 'structure', structure: {...}},
     *           {x: 6, y: 11, type: 'terrain', terrain: 'swamp'},
     *           {x: 7, y: 11, type: 'terrain', terrain: 'wall'}
     *         ]}}}
     * @note CPU Cost: MEDIUM
     */
    def lookAtArea(top: Int, left: Int, bottom: Int, right: Int, asArray: Boolean = false): js.Any = js.native

    /**
     * Get the list of objects at the specified room position.
     *
     * @param lookType The type of object we're looking for
     * @param x        X position in the room
     * @param y        Y position in the room
     * @return An array of objects of the given type at the specified position if found.
     * @note CPU Cost: LOW
     */
    def lookForAt(lookType: String @@ Look, x: Int, y: Int): js.Array[js.Object] = js.native

    /**
     * Get the list of objects at the specified room position.
     *
     * @param lookType The type of object we're looking for
     * @param target   the position to look at
     * @return An array of objects of the given type at the specified position if found.
     * @note CPU Cost: LOW
     */
    def lookForAt(lookType: String @@ Look, target: RoomPosition): js.Array[js.Object] = js.native

    /**
     * Get the list of objects at the specified room area.
     *
     * @param lookType
     * @param top     The top Y boundary of the area.
     * @param left    The left X boundary of the area.
     * @param bottom  The bottom Y boundary of the area.
     * @param right   The right X boundary of the area.
     * @param asArray Set to true if you want to get the result as a plain array.
     * @return If asArray is set to false or undefined, the method returns an object with all the objects in the specified area in the following format: {{{
     *         // 10,5,11,7
     *
     *         {
     *           10: {
     *             5: [{ ... }],
     *             6: undefined,
     *             7: undefined
     *           },
     *           11: {
     *             5: undefined,
     *             6: [{ ... }, { ... }],
     *             7: undefined
     *           }
     *         } }}}
     *         If asArray is set to true, the method returns an array in the following format:{{{
     *         [
     *           {x: 5, y: 10, structure: {...}},
     *           {x: 6, y: 11, structure: {...}},
     *           {x: 6, y: 11, structure: {...}}
     *         ]}}}
     * @note CPU Cost: LOW
     */
    def lookForAtArea(lookType: String @@ Look, top: Int, left: Int, bottom: Int, right: Int, asArray: Boolean = false):
    js.Array[LookArrayResult] |
    LookObjectResult = js.native
}

@js.native
@JSGlobal
class LookArrayResult extends js.Object {
    val x: Int = js.native
    val y: Int = js.native
    val structure: js.Dynamic = js.native
    // TODO determine what this structure is
}

@js.native
@JSGlobal
class LookObjectResult extends js.Object {
    // TODO do i even really care?
}
