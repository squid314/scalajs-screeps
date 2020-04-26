package com.screeps

import language.implicitConversions
import com.screeps.native.Constants._

import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal
import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.JSExport

package object native {

    /*
        implicit class RoomOps(val room: Room) {
            @inline final def findStructures[T <: js.Object](opts: FindOptions[T] = null): js.Array[Structure] = room.find(Find.Structures, opts).asInstanceOf[js.Array[Structure]]

            @inline final def findMyStructures[T <: js.Object](opts: FindOptions[T] = null): js.Array[Structure] = room.find(Find.MyStructures, opts).asInstanceOf[js.Array[Structure]]

            @inline final def findHostileStructures[T <: js.Object](opts: FindOptions[T] = null): js.Array[Structure] = room.find(Find.HostileStructures, opts).asInstanceOf[js.Array[Structure]]
        }
    */

    implicit class PathFinderOptionsOps(pfo: PathFinderOptions) {
        // oh how i miss thee case class  :(
        /*
                @inline def roomCallback(roomCallback: js.Function2[String, CostMatrix, CostMatrix]): PathFinderOptions =
                    new PathFinderOptions(roomCallback, pfo.plainCost, pfo.swampCost, pfo.flee, pfo.maxOps, pfo.maxRooms, pfo.maxCost, pfo.heuristicWeight)


                @inline def noRoomCallback: PathFinderOptions =
                    new PathFinderOptions(js.undefined, pfo.plainCost, pfo.swampCost, pfo.flee, pfo.maxOps, pfo.maxRooms, pfo.maxCost, pfo.heuristicWeight)

        */

        @inline def withPlainCost(plainCost: Int): PathFinderOptions =
            new PathFinderOptions(plainCost, pfo.swampCost, pfo.flee, pfo.maxOps, pfo.maxRooms, pfo.maxCost, pfo.heuristicWeight)

        @inline def noPlainCost: PathFinderOptions =
            new PathFinderOptions(js.undefined, pfo.swampCost, pfo.flee, pfo.maxOps, pfo.maxRooms, pfo.maxCost, pfo.heuristicWeight)

        @inline def withSwampCost(swampCost: Int): PathFinderOptions =
            new PathFinderOptions(pfo.plainCost, swampCost, pfo.flee, pfo.maxOps, pfo.maxRooms, pfo.maxCost, pfo.heuristicWeight)

        @inline def noSwampCost: PathFinderOptions =
            new PathFinderOptions(pfo.plainCost, js.undefined, pfo.flee, pfo.maxOps, pfo.maxRooms, pfo.maxCost, pfo.heuristicWeight)

        @inline def withFlee(flee: Boolean): PathFinderOptions =
            new PathFinderOptions(pfo.plainCost, pfo.swampCost, flee, pfo.maxOps, pfo.maxRooms, pfo.maxCost, pfo.heuristicWeight)

        @inline def noFlee: PathFinderOptions =
            new PathFinderOptions(pfo.plainCost, pfo.swampCost, js.undefined, pfo.maxOps, pfo.maxRooms, pfo.maxCost, pfo.heuristicWeight)

        @inline def withMaxOps(maxOps: Int): PathFinderOptions =
            new PathFinderOptions(pfo.plainCost, pfo.swampCost, pfo.flee, maxOps, pfo.maxRooms, pfo.maxCost, pfo.heuristicWeight)

        @inline def noMaxOps: PathFinderOptions =
            new PathFinderOptions(pfo.plainCost, pfo.swampCost, pfo.flee, js.undefined, pfo.maxRooms, pfo.maxCost, pfo.heuristicWeight)

        @inline def withMaxRooms(maxRooms: Int): PathFinderOptions =
            new PathFinderOptions(pfo.plainCost, pfo.swampCost, pfo.flee, pfo.maxOps, maxRooms, pfo.maxCost, pfo.heuristicWeight)

        @inline def noMaxRooms: PathFinderOptions =
            new PathFinderOptions(pfo.plainCost, pfo.swampCost, pfo.flee, pfo.maxOps, js.undefined, pfo.maxCost, pfo.heuristicWeight)

        @inline def withMaxCost(maxCost: Int): PathFinderOptions =
            new PathFinderOptions(pfo.plainCost, pfo.swampCost, pfo.flee, pfo.maxOps, pfo.maxRooms, maxCost, pfo.heuristicWeight)

        @inline def noMaxCost: PathFinderOptions =
            new PathFinderOptions(pfo.plainCost, pfo.swampCost, pfo.flee, pfo.maxOps, pfo.maxRooms, js.undefined, pfo.heuristicWeight)

        @inline def withHeuristicWeight(heuristicWeight: Double): PathFinderOptions =
            new PathFinderOptions(pfo.plainCost, pfo.swampCost, pfo.flee, pfo.maxOps, pfo.maxRooms, pfo.maxCost, heuristicWeight)

        @inline def noHeuristicWeight: PathFinderOptions =
            new PathFinderOptions(pfo.plainCost, pfo.swampCost, pfo.flee, pfo.maxOps, pfo.maxRooms, pfo.maxCost, js.undefined)
    }
}
