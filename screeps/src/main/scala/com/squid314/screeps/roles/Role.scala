package com.squid314.screeps.roles

import com.screeps.native.Constants._
import com.screeps.native._

import scala.scalajs.js
import scala.scalajs.js._

trait Role {
    def run(creep: Creep): Unit
}
