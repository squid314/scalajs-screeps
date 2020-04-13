package com.squid314.screeps.economy

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
@JSExportTopLevel("Assessment")
case class Assessment(@JSExport age: Int, @JSExport reassess: Int, @JSExport state: Assessment.State.Value)

object Assessment {

    object State extends Enumeration {
        /** Represents a room in early stages. Probably just starting, may have collapsed. */
        val Fledgling: Value = Value("Fledgling")
        /** Represents a room which may start farming adjacent rooms for energy and/or minerals. */
        val Expanding: Value = Value("Expanding")
        /** Resresents a room that is stable and may be working on combining minerals (or other stuff?). */
        val Producing: Value = Value("Producing")
    }

    def apply(room: Room): Assessment = {
        g.console.log("assessing room")
        val existing = fromDynamic(room.memory)
                .filter(a => a.reassess > Game.time)
        if (existing.isDefined) {
            g.console.log("using existing assessment")
            existing.get
        } else {
            // TODO everything is "fledgling" right now
            val assessment = new Assessment(Game.time, Game.time + 1000, State.Fledgling)
            room.memory.assessment = toDynamic(assessment)
            assessment
        }
    }

    // these two defs are needed because we can't store a scala class into a js.Any object
    private def toDynamic(a: Assessment): js.Dynamic =
        js.Dynamic.literal(age = a.age, reassess = a.reassess, state = a.state.toString)

    private def fromDynamic(memory: js.Dynamic): Option[Assessment] =
        for {
            d <- memory.assessment.asInstanceOf[UndefOr[js.Dynamic]].toOption
            age <- d.age.asInstanceOf[UndefOr[Int]].toOption
            reassess <- d.reassess.asInstanceOf[UndefOr[Int]].toOption
            stateName <- d.state.asInstanceOf[UndefOr[String]].toOption
            state <- State.values.find(_.toString == stateName)
        } yield new Assessment(age, reassess, state)
}
