package com.squid314.screeps.ext

import scala.scalajs.js
import scala.scalajs.js._

import com.screeps.native._
import com.screeps.native.Constants._

final class CreepOps(val creep: Creep) {
    def travelTo(destination: RoomPosition) = Traveler.travelTo(creep, destination)
    def travelTo(destination: RoomPosition, opts: js.Object) = Traveler.travelTo(creep, destination, opts)
}
