package com.squid314.screeps.ext

import scala.scalajs.js
import scala.scalajs.js.annotation._

import com.screeps.native._
import com.screeps.native.Constants._


@js.native
@JSImport("Traveler", "Traveler")
class Traveler extends js.Object

@js.native
@JSImport("Traveler", "Traveler")
object Traveler extends js.Object {
    def travelTo(creep: Creep, destination: RoomPosition, opts: js.Object = js.native): Int @@ Error = js.native
}
