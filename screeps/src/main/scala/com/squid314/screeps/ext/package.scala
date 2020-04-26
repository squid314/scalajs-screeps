package com.squid314.screeps

import language.implicitConversions

import com.screeps.native._
import com.screeps.native.Constants._

package object ext {
    @inline implicit def creepOps(creep: Creep): CreepOps = new CreepOps(creep)
}
