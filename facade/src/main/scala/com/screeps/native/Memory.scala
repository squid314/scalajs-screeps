package com.screeps.native

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal


@js.native
@JSGlobal
object Memory extends GlobalMemory {
}

@js.native
trait GlobalMemory extends Memory

@js.native
trait Memory extends js.Object {
    val creeps: js.Dictionary[js.Dynamic] = js.native
    val spawns: js.Dictionary[js.Dynamic] = js.native
    val rooms: js.Dictionary[js.Dynamic] = js.native
    val flags: js.Dictionary[js.Dynamic] = js.native
}
