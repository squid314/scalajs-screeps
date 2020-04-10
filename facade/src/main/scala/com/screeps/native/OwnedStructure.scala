package com.screeps.native

import com.screeps.native.Constants._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSBracketAccess
import scala.scalajs.js.|

@js.native
trait Owner extends js.Object {
    val username: String = js.native
}

@js.native
trait OwnedStructure extends Structure {
    /** Whether this is your own structure. */
    val my: Boolean = js.native
    /**
     * An object with the structure’s owner info.
     */
    val owner: Owner = js.native
}

@js.native
trait OwnedStructureWithStorage extends OwnedStructure {
    /**
     * An object with the storage contents. Each object key is one of the RESOURCE_* constants, values
     * are resources amounts. RESOURCE_ENERGY is always defined and equals to 0 when empty, other
     * resources are undefined when empty. You can use lodash.sum to get the total amount of contents.
     */
    val store: Store = js.native
}

@js.native
trait Store extends js.Object {
    /**
     * Returns capacity of this store for the specified resource. For a general purpose store, it returns total capacity if {{{resource}}} is undefined.
     *
     * @return capacity number, or {{{null}}} in case of an invalid {{{resource}}} for this store type.
     */
    def getCapacity(resource: String @@ ResourceType = ???): Int @@ ResourceAmount | Unit = js.native

    /**
     * Returns free capacity for the store. For a limited store, it returns the capacity available for the specified resource if {{{resource}}} is defined and valid for this store.
     *
     * @return capacity number, or {{{null}}} in case of an invalid {{{resource}}} for this store type.
     */
    def getFreeCapacity(resource: String @@ ResourceType = ???): Int @@ ResourceAmount | Unit = js.native

    /**
     * Returns the capacity used by the specified resource. For a general purpose store, it returns total used capacity if {{{resource}}} is undefined.
     *
     * @return capacity number, or {{{null}}} in case of an invalid {{{resource}}} for this store type.
     */
    def getUsedCapacity(resource: String @@ ResourceType = ???): Int @@ ResourceAmount | Unit = js.native

    @JSBracketAccess
    def apply(resource: String @@ ResourceType): Int @@ ResourceAmount | Unit = js.native
}
