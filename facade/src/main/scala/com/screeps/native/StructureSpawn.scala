package com.screeps.native

import com.screeps.native.Constants._

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.scalajs.js.{UndefOr, |}

/**
 * If the spawn is in process of spawning a new creep, this object will contain the new creep’s information.
 */
@js.native
trait Spawning extends js.Object {
    /** The name of a new creep. */
    val name: String = js.native

    /** Time needed in total to complete the spawning. */
    val needTime: Int = js.native

    /** Remaining time to go. */
    val remainingTime: Int = js.native
}

/**
 * An object with additional options for the spawning process.
 *
 * @param memory           Memory of the new creep; will be immediately stored into [[Memory.creeps]]`[name]`.
 * @param energyStructures Array of spawns/extensions from which to draw energy for the spawning process. Structures will be used according to the array order.
 * @param dryRun           If true, the operation will only check if it is possible to create a creep.
 * @param directions       Set desired [[Direction]]s where the creep should move when spawned.
 */
@JSExportTopLevel("SpawnOptions")
case class SpawnOptions(
                           @JSExport memory: js.UndefOr[Memory.CreepMemory] = js.undefined,
                           @JSExport energyStructures: js.UndefOr[js.Array[StructureSpawn | StructureExtension]] = js.undefined,
                           @JSExport dryRun: js.UndefOr[Boolean] = js.undefined,
                           @JSExport directions: js.UndefOr[js.Array[Int @@ Direction]] = js.undefined,
                       )

/**
 * Spawn is your colony center. This structure can create, renew, and recycle creeps.
 * All your spawns are accessible through Game.spawns hash list. Spawns auto-regenerate a little amount of
 * energy each tick, so that you can easily recover even if all your creeps died.
 *
 * Controller level
 * 1-6	1 spawn
 * 7	2 spawns
 * 8	3 spawns
 *
 * Cost	5,000
 * Hits	5,000
 * Capacity	300
 * Spawn time	3 ticks per each body part
 * Energy auto-regeneration	1 energy unit per tick while energy available in the room is less than 300
 */
@js.native
trait StructureSpawn extends OwnedStructureWithStorage {
    /**
     * Spawn’s name. You choose the name upon creating a new spawn, and it cannot be changed later.
     * This name is a hash key to access the spawn via the Game.spawns object.
     */
    val name: String = js.native
    /** A shorthand to Memory.spawns[spawn.name]. You can use it for quick access the spawn’s specific memory data object.
     * [[http://support.screeps.com/hc/en-us/articles/203016642-Working-with-memory Learn more about memory]]
     */
    val memory: js.Dynamic = js.native
    /**
     * If the spawn is in process of spawning a new creep, this object
     * will contain the new creep’s information, or null otherwise.
     */
    val spawning: UndefOr[Spawning] = js.native

    /**
     * Start the creep spawning process. The required energy amount can be withdrawn from all spawns and extensions in the room.
     *
     * @param body An array describing the new creep’s body. Should contain 1 to 50 elements of the [[BodypartType]]s.
     * @param name The name of a new creep. It must be a unique creep name, i.e. the [[Game.creeps]] object should not contain another creep with the same name (hash key).
     * @param opts An object with additional options for the spawning process.
     * @return One of [[Error.OK]], [[Error.NotOwner]], [[Error.NameExists]], [[Error.Busy]], [[Error.NotEnoughResources]], [[Error.InvalidArgs]], [[Error.RCLNotEnough]].
     */
    def spawnCreep(body: js.Array[String @@ BodypartType], name: String, opts: SpawnOptions = ???): Int @@ Error = js.native

    /**
     * Kill the creep and drop up to 100% of resources spent on its spawning and boosting depending on remaining life
     * time. The target should be at adjacent square. Energy return is limited to 125 units per body part.
     *
     * @param target The target creep object
     * @return The name of the new creep or one of the following:
     *         OK - The operation has been scheduled successfully.
     *         NotOwner - You are not the owner of the target creep.
     *         InvalidTarget - The specified target object is not a creep
     *         NotInRange - The target creep is too far away.
     * @note CPU Cost: CONST
     */
    def recycleCreep(target: Creep): Int = js.native

    /**
     * Increase the remaining time to live of the target creep. The target should be at adjacent square. The spawn
     * should not be busy with the spawning process. Each execution increases the creep's timer by amount of ticks
     * according to this formula: {{{floor(600/body_size)}}}. Energy required for each execution is determined using this
     * formula: {{{ceil(creep_cost/2.5/body_size)}}}. Renewing a creep removes all of its boosts.
     *
     * @param target The target creep object
     * @return One of the following error codes:
     *         [[Error.OK]] - The operation has been scheduled successfully.
     *         [[Error.NotOwner]] - You are not the owner of this spawn.
     *         [[Error.Busy]] - The spawn is spawning another creep.
     *         [[Error.NotEnoughResources]] - The spawn does not have enough energy.
     *         [[Error.InvalidTarget]] - The specified target object is not a creep.
     *         [[Error.Full]] - The target creep's time to live timer is full.
     *         [[Error.NotInRange]] - The target creep is too far away.
     *         [[Error.RCLNotEnough]] - Your Room Controller level is insufficient to use this spawn.
     * @note CPU Cost: CONST
     */
    def renewCreep(target: Creep): Int = js.native


    /**
     * Check if a creep can be created.
     *
     * @param body An array describing the new creep’s body. Should contain 1 to 50 elements with one of these constants:
     *             WORK, MOVE, CARRY, ATTACK, RANGED_ATTACK, HEAL, TOUGH, CLAIM
     * @param name The name of a new creep. It should be unique creep name, i.e. the Game.creeps object should not
     *             contain another creep with the same name (hash key). If not defined, a random name will be generated.
     * @return One of the following codes:
     *         OK - The operation has been scheduled successfully.
     *         NotOwner - You are not the owner of this spawn.
     *         NameExists - There is a creep with the same name already
     *         Busy - The spawn is already in process of spawning another creep.
     *         NotEnoughResources - The spawn and its extensions contain not enough energy to create a creep with the given body.
     *         InvalidArgs - Body is not properly described.
     *         RCLNotEnough - The Room Controller Level is not enough.
     * @note CPU Cost: LOW
     * @deprecated use [[spawnCreep()]]
     */
    @deprecated("use StructureSpawn.spawnCreep()", "Facade-screeps v??")
    def canCreateCreep(body: js.Array[String], name: String = ""): Int = js.native

    /**
     * Start the creep spawning process. The required energy amount can be withdrawn
     * from all spawns and extensions in the room.
     *
     * @param body   An array describing the new creep’s body. Should contain 1 to 50 elements with one of these constants:
     *               WORK, MOVE, CARRY, ATTACK, RANGED_ATTACK, HEAL, TOUGH, CLAIM
     * @param name   The name of a new creep. It should be unique creep name, i.e. the Game.creeps object should not
     *               contain another creep with the same name (hash key). If not defined, a random name will be generated.
     * @param memory The memory of a new creep. If provided, it will be immediately stored into Memory.creeps[name].
     * @return The name of the new creep or one of the following:
     *         OK - The operation has been scheduled successfully.
     *         NotOwner - You are not the owner of this spawn.
     *         NameExists - There is a creep with the same name already
     *         Busy - The spawn is already in process of spawning another creep.
     *         NotEnoughResources - The spawn and its extensions contain not enough energy to create a creep with the given body.
     *         InvalidArgs - Body is not properly described.
     *         RCLNotEnough - The Room Controller Level is not enough.
     * @note CPU Cost: CONST
     * @deprecated use [[spawnCreep()]]
     */
    @deprecated("use StructureSpawn.spawnCreep()", "Facade-screeps v??")
    def createCreep(body: js.Array[String @@ BodypartType], name: String = "", memory: js.Any = ???): js.Any = js.native
}
