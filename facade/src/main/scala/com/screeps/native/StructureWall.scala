package com.screeps.native

import scala.scalajs.js

/**
 * Blocks movement of all creeps. Players can build destructible walls in controlled rooms. Some rooms also contain
 * indestructible walls separating novice and respawn areas from the rest of the world or dividing novice / respawn
 * areas into smaller sections. Indestructible walls have no hits property.
 * {{{
 * Controller level        2
 * Cost                    1
 * Hits when constructed   1
 * Max hits                300,000,000
 * }}}
 */
@js.native
trait StructureWall extends Structure
