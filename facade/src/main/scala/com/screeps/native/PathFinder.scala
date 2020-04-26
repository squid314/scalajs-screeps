package com.screeps.native

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel, JSGlobal}
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
    def search(origin: RoomPosition, goal: RoomPosition | RangeGoal | js.Array[RoomPosition | RangeGoal], opts: PathFinderOptions = js.native): Path = js.native
}

/**
 * An object containing additional pathfinding flags.
 *
 * @note not officially created in API; described at [[PathFinder.search]]
 */
@JSExportTopLevel("PathFinder_PathFinderOptions")
class PathFinderOptions(
                           // TODO
                           /** Request from the [[PathFinder]] to generate a [[CostMatrix]] for a certain room. The callback accepts one argument, {{{roomName}}}. This callback will only be called once per room per search. If you are running multiple pathfinding operations in a single room and in a single tick you may consider caching your CostMatrix to speed up your code. Please read the CostMatrix documentation below for more information on CostMatrix. If you return {{{false}}} from the callback the requested room will not be searched, and it won't count against {{{maxRooms}}}. */
                           // @JSExport val roomCallback: js.UndefOr[(String, CostMatrix) => CostMatrix] = js.undefined,
                           /** Cost for walking on plain positions. The default is 1. */
                           @JSExport val plainCost: js.UndefOr[Int] = js.undefined,

                           /** Cost for walking on swamp positions. The default is 5. */
                           @JSExport val swampCost: js.UndefOr[Int] = js.undefined,

                           /** Instead of searching for a path to the goals this will search for a path away from the goals. The cheapest path that is out of {{{range}}} of every goal will be returned. The default is false. */
                           @JSExport val flee: js.UndefOr[Boolean] = js.undefined,

                           /** The maximum allowed pathfinding operations. You can limit CPU time used for the search based on ratio 1 op ~ 0.001 CPU. The default value is 2000. */
                           @JSExport val maxOps: js.UndefOr[Int] = js.undefined,

                           /** The maximum allowed rooms to search. The default is 16, maximum is 64. */
                           @JSExport val maxRooms: js.UndefOr[Int] = js.undefined,

                           /** The maximum allowed cost of the path returned. If at any point the pathfinder detects that it is impossible to find a path with a cost less than or equal to {{{maxCost}}} it will immediately halt the search. The default is Infinity. */
                           @JSExport val maxCost: js.UndefOr[Int] = js.undefined,

                           /** Weight to apply to the heuristic in the A* formula `F = G + weight * H`. Use this option only if you understand the underlying A* algorithm mechanics! The default value is 1.2. */
                           @JSExport val heuristicWeight: js.UndefOr[Double] = js.undefined,
                       )

object PathFinderOptions {
    def apply(): PathFinderOptions = new PathFinderOptions()
}

@JSExportTopLevel("PathFinder_RangeGoal")
class RangeGoal(
                   @JSExport val pos: RoomPosition,
                   @JSExport val range: Int = 0,
               )

object RangeGoal {
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
