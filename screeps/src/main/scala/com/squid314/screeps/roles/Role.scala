package com.squid314.screeps.roles

import com.screeps.native.Creep

trait Role {
    def run(creep: Creep): Unit
}
