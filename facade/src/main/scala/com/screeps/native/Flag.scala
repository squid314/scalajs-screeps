package com.screeps.native

import com.screeps.native.Constants._

import scala.scalajs.js
import scala.scalajs.js.|

/** A flag. Flags can be used to mark particular spots in a room. Flags are visible to their owners only. You cannot have more than 10,000 flags. */
@js.native
trait Flag extends RoomObject {
    /**
     * Flag’s name. You can choose the name while creating a new flag, and it cannot be changed later.
     * This name is a hash key to access the spawn via the Game.flags object.
     */
    val name: String = js.native
    /** Flag primary color. One of the [[Color]] constants. */
    val color: Int = js.native
    /** Flag secondary color. One of the [[Color]] constants. */
    val secondaryColor: Int = js.native
    /** A shorthand to [[Memory.flags]][flag.name]. You can use it for quick access the flag's specific memory data object. */
    val memory: js.Any = js.native

    /**
     * Remove the flag.
     *
     * @return [[Error.OK]]
     */
    def remove(): Int = js.native

    /**
     * Set new color of the flag
     *
     * @param color          Primary color of the flag. One of the COLOR_* constants.
     * @param secondaryColor Secondary color of the flag. One of the COLOR_* constants.
     * @return One of the following codes:
     *         OK - The operation has been scheduled successfully.
     *         InvalidArgs - The color is not a valid color constant
     */
    def setColor(color: Int @@ Color, secondaryColor: Int @@ Color = Color.White): Int = js.native

    /**
     * Set new position of the flag.
     *
     * @param x The X position in the room.
     * @param y The Y position in the room.
     * @return One of the following codes:
     *         OK - The operation has been scheduled successfully.
     *         InvalidTarget - The target provided is invalid.
     */
    def setPosition(x: Int, y: Int): Int = js.native

    /**
     * Set new position of the flag.
     *
     * @param pos The position in the room.
     * @return One of the following codes:
     *         OK - The operation has been scheduled successfully.
     *         InvalidTarget - The target provided is invalid.
     */
    def setPosition(pos: RoomPosition | {val pos: RoomPosition}): Int = js.native
}
