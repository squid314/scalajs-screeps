package com.screeps.native

import scala.scalajs.js
import scala.scalajs.js.|


@js.native
trait RouteOptions extends js.Object {
    /**
     * This can be used to calculate the cost of entering that room.
     * You can use this to do things like prioritize your own rooms, or avoid some rooms.
     * You can return a floating point cost or Infinity to block the room.
     *
     * @param roomName     the room we are considering on the route
     * @param fromRoomName the room from which we are leaving on the route
     * @return a floating point cost or Infinity to block the room.
     */
    def routeCallback(roomName: String, fromRoomName: String): Float
}

@js.native
trait RoutePart extends js.Object {
    val exit: Short = js.native
    val room: String = js.native
}

@js.native
trait RoomStatus extends js.Object {
    /**
     * One of the following statuses:
     * - normal  – the room has no restrictions
     * - closed  – the room is not available
     * - novice  – the room is part of a novice area
     * - respawn – the room is part of a respawn area
     */
    val status: String = js.native
    /** Status expiration time in milliseconds since UNIX epoch time. This property is null if the status is permanent.  */
    val timestamp: Int = js.native
}


/** An object representing the world map. Use it to navigate between rooms.
 * The object is accessible via Game.map property.
 */
@js.native
trait Map extends js.Object {

    /**
     * List all exits available from the room with the given name.
     *
     * @param roomName The room name.
     * @return The exits information in the following format, or null if the room not found.
     * {{{   {
     *   "1": "W8N4",    // TOP
     *   "3": "W7N3",    // RIGHT
     *   "5": "W8N2",    // BOTTOM
     *   "7": "W9N3"     // LEFT
     * } }}}
     * @note CPU Cost: LOW
     */
    def describeExits(roomName: String): js.Object = js.native

    /**
     * Find the exit direction from the given room en route to another room.
     *
     * @param fromRoom Start room name or room object.
     * @param toRoom   Finish room name or room object.
     * @param opts     The route finding options. See [findRoute] and [RouteOptions].
     * @return The room direction constant, one of the following:
     *         Top, Right, Bottom, Left
     *         Or one of the following error codes:
     *         NoPath, InvalidArgs
     * @note CPU Cost: HIGH
     */
    def findExit(fromRoom: String, toRoom: String, opts: RouteOptions): Short = js.native

    /**
     * Find route from the given room to another room.
     *
     * @param fromRoom Start room name or room object.
     * @param toRoom   Finish room name or room object.
     * @param opts     The route finding options. See [findRoute] and [RouteOptions].
     * @return The route array or NoPath
     */
    // Note: return should be js.Array[RoutePart]
    def findRoute(fromRoom: String, toRoom: String, opts: RouteOptions): Short | js.Array[RoutePart] = js.native

    /**
     * Get the linear distance (in rooms) between two rooms. You can use this function to estimate
     * the energy cost of sending resources through terminals, or using observers and nukes.
     *
     * @param roomName1  the name of the first room
     * @param roomName2  the name of the second room
     * @param continuous Whether to treat the world map continuous on borders. Set to {{{true}}} if you want to calculate the trade or terminal send cost. Default is {{{false}}}.
     * @return the number of rooms between the given two rooms
     */
    def getRoomLinearDistance(roomName1: String, roomName2: String, continuous: Boolean = ???): Int = js.native

    /**
     * Get a [[Room.Terrain]] object which provides fast access to static terrain data. This method works for any room in the world even if you have no access to it.
     *
     * @param roomName
     * @return new [[Room.Terrain]] object.
     */
    def getRoomTerrain(roomName: String): Room.Terrain

    /**
     * Get terrain type at the specified room position. This method works
     * for any room in the world even if you have no access to it.
     *
     * @param x        X position in the room.
     * @param y        Y position in the room.
     * @param roomName The room name.
     * @return One of the following string values:
     *         "plain", "swamp", "wall
     */
    @deprecated("Please use faster method getRoomTerrain()", "Facade-screeps v??")
    def getTerrainAt(x: Int, y: Int, roomName: String): String = js.native

    /** Returns the world size as a number of rooms between world corners. For example, for a world with rooms from {{{W50N50}}} to {{{E50S50}}} this method will return 102. */
    def getWorldSize(): Int = js.native

    def getRoomStatus(roomName: String): RoomStatus
}
