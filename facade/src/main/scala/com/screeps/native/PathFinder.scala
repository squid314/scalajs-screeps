package com.screeps.native

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal
import scala.scalajs.js.|

/**
 * Contains powerful methods for pathfinding in the game world. This module is written in fast native C++ code and supports custom navigation costs and paths which span multiple rooms.
 */
@js.native
@JSGlobal
object PathFinder extends js.Object {
    /**
     * Find an optimal path between {{{origin}}} and {{{goal}}}.
     *
     * @param origin The start position.
     * @param goal   A goal or an array of goals. If more than one goal is supplied then the cheapest path found out of all the goals will be returned. A goal is either a RoomPosition or an object as defined below.
     *
     *               Important: Please note that if your goal is not walkable (for instance, a source) then you should set {{{range}}} to at least 1 or else you will waste many CPU cycles searching for a target that you can't walk on.
     * @param opts   An object containing additional pathfinding flags.
     * @return The [[Path]] that was found.
     */
    def search(origin: RoomPosition, goal: RoomPosition | RangeGoal | js.Array[RoomPosition | RangeGoal], opts: PathFinderOpts = js.native): Path = js.native
}

/**
 * An object containing additional pathfinding flags.
 *
 * @param roomCallback    TODO Request from the PathFinder to generate a [[CostMatrix]] for a certain room. The callback accepts one argument, {{{roomName}}}. This callback will only be called once per room per search. If you are running multiple pathfinding operations in a single room and in a single tick you may consider caching your CostMatrix to speed up your code. Please read the CostMatrix documentation below for more information on CostMatrix. If you return {{{false}}} from the callback the requested room will not be searched, and it won't count against {{{maxRooms}}}.
 * @param plainCost       Cost for walking on plain positions. The default is 1.
 * @param swampCost       Cost for walking on swamp positions. The default is 5.
 * @param flee            Instead of searching for a path to the goals this will search for a path away from the goals. The cheapest path that is out of {{{range}}} of every goal will be returned. The default is false.
 * @param maxOps          The maximum allowed pathfinding operations. You can limit CPU time used for the search based on ratio 1 op ~ 0.001 CPU. The default value is 2000.
 * @param maxRooms        The maximum allowed rooms to search. The default is 16, maximum is 64.
 * @param maxCost         The maximum allowed cost of the path returned. If at any point the pathfinder detects that it is impossible to find a path with a cost less than or equal to {{{maxCost}}} it will immediately halt the search. The default is Infinity.
 * @param heuristicWeight Weight to apply to the heuristic in the A* formula {{{F = G + weight * H}}}. Use this option only if you understand the underlying A* algorithm mechanics! The default value is 1.2.
 */
class PathFinderOpts(
                        // TODO
                        // val roomCallback: Option[String => CostMatrix | Boolean] = None,
                        val plainCost: Option[Int] = None,
                        val swampCost: Option[Int] = None,
                        val flee: Option[Boolean] = None,
                        val maxOps: Option[Int] = None,
                        val maxRooms: Option[Int] = None,
                        val maxCost: Option[Int] = None,
                        val heuristicWeight: Option[Double] = None,
                    ) extends js.Object

class RangeGoal(val pos: RoomPosition, range: Int = 0) extends js.Object

object RangeGoal {

    import language.reflectiveCalls

    def apply(goal: RoomPosition): RangeGoal = new RangeGoal(goal)

    /** This apply will handle any objects which provide a RoomPosition as {{{goal.pos}}} (e.g. any RoomObject). */
    def apply(goal: RoomObject): RangeGoal = new RangeGoal(goal.pos)

    def apply(goal: RoomPosition, range: Int): RangeGoal = new RangeGoal(goal, range)

    /** This apply will handle any objects which provide a RoomPosition as {{{goal.pos}}} (e.g. any RoomObject). */
    def apply(goal: RoomObject, range: Int): RangeGoal = new RangeGoal(goal.pos, range)
}

@js.native
trait Path extends js.Object {
    val path: js.Array[RoomPosition] = js.native
    val ops: Int = js.native
    val cost: Int = js.native
    val incomplete: Boolean = js.native
}
