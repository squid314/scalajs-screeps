package com.screeps.native

import com.screeps.native.Constants._

import scala.scalajs.js

/**
 * A site of a structure which is currently under construction. A construction site can be created using the 'Construct' button at the left of the game field or the [[Room.createConstructionSite()]] method.
 *
 * To build a structure on the construction site, give a worker creep some amount of energy and perform [[Creep.build()]] action.
 *
 * You can remove enemy construction sites by moving a creep on it.
 */
@js.native
trait ConstructionSite extends RoomObject {
    /** A unique object identificator. You can use Game.getObjectById method to retrieve an object instance by its id. */
    val id: String = js.native
    /** Whether this is your own construction site. */
    val my: Boolean = js.native
    /** An object with the structure’s owner info. */
    val owner: Owner = js.native
    /** The current construction progress. */
    val progress: Int = js.native
    /** The total construction progress needed for the structure to be built. */
    val progressTotal: Int = js.native
    /** One of the [[StructureType]] constants. */
    val structureType: String @@ StructureType = js.native

    /**
     * Remove the construction site.
     *
     * @return [[Error.OK]] or [[Error.NotOwner]]
     */
    def remove(): Int = js.native
}
