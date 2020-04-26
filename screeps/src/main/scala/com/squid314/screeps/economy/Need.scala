package com.squid314.screeps.economy

import com.screeps.native.Constants._
import com.squid314.screeps.economy

import scala.scalajs.js
import scala.scalajs.js.JSConverters._

sealed trait Need {
    def `type`: Int @@ Need
}

object Need {
    val Role:Int @@ Need = 0.asInstanceOf[Int @@ Need]
    val Task:Int @@ Need = 1.asInstanceOf[Int @@ Need]

    def toDynamic(need: Need): js.Dynamic = ???
/*
        js.Dynamic.literal.apply((
            "type" -> (need.`type`:js.Any)
                ::
                (need match {
                    case RoleNeed(name, body, id) => List[(String,js.Any)]("name" -> name, "body" -> body.toJSArray, "id" -> id)
                    case TaskNeed(name, body, id) => List[(String,js.Any)]("name" -> name, "body" -> body.toJSArray, "id" -> id)
                })).toArray[(String,js.Any)]: _*)
*/

    def fromDynamic(dyn: js.Dynamic): Option[Need] = {
        val flerb: Need = ???
        dyn.`type`.asInstanceOf[js.UndefOr[Int]]
            .toOption
            .map {
                case Role => flerb
                case Task => flerb
            }
    }
}

case class RoleNeed(
                       roleName: String,
                       body: List[String @@ Bodypart],
                       id: String,
                   ) extends Need {
    override def `type` = Need.Role
}

case class TaskNeed(
                       taskName: String,
                       body: List[String @@ Bodypart],
                       id: String,
                   ) extends Need {
    override def `type` = Need.Task
}
