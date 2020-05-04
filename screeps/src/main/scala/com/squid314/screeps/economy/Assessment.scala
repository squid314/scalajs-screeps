package com.squid314.screeps.economy

import com.screeps.native.Constants.{Find, StructureType}
import com.screeps.native._

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

/**
 * Provides information about the economic capability of a room.
 *
 * @param age      the [[Game.time]] at which the assessment was made.
 * @param reassess the [[Game.time]] at (or after) which to examine the state of the room again.
 * @param state    the broad strokes state.
 */
case class Assessment(
                         age: Int,
                         reassess: Int,
                         state: Assessment.State.Value,
//                         roleNeeds: List[RoleNeed],
                         // TODO put needs in here, i think
                         //@JSExport val needs: js.Array[js.Object with js.Dynamic],
                     )

object Assessment {

    object State extends Enumeration {
        /** Represents a room just after [[StructureSpawn]] creation which has almost no resources. */
        val Initializing = Value
        /** Represents a room in early stages. Probably just starting, may have collapsed. */
        val Fledgling = Value
        /** Represents a room which may start farming adjacent rooms for energy and/or minerals. */
        val Expanding = Value
        /** Resresents a room that is stable and may be working on combining minerals (or other stuff?). */
        val Producing = Value
    }

    def assess(room: Room): Assessment = {
        fromDynamic(room.memory)
            .filter(a => a.reassess > Game.time)
            .getOrElse {
                for (cont <- room.controller) {
                    if (cont.level < 3) {
                        return Assessment(Game.time, Game.time + 500, State.Initializing)
                    }
                }
                val assessment = Assessment(Game.time, Game.time + 1000, State.Fledgling)
                room.memory.assessment = toDynamic(assessment)
                assessment
            }
    }

    // these two defs are needed because we can't store a scala class into a js.Any object
    private def toDynamic(a: Assessment): js.Dynamic =
        js.Dynamic.literal(
            age = a.age,
            reassess = a.reassess,
            state = a.state.toString,
        )

    private def fromDynamic(memory: js.Dynamic): Option[Assessment] =
        for {
            d <- memory.assessment.asInstanceOf[UndefOr[js.Dynamic]].toOption
            age <- d.age.asInstanceOf[UndefOr[Int]].toOption
            reassess <- d.reassess.asInstanceOf[UndefOr[Int]].toOption
            stateName <- d.state.asInstanceOf[UndefOr[String]].toOption
            state <- State.values.find(_.toString == stateName)
//            roleNeeds <- d.roleNeeds.asInstanceOf[UndefOr[]]
        } yield new Assessment(age, reassess, state)
}
