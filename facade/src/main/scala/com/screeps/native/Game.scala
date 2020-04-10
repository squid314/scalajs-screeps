package com.screeps.native

import com.screeps.native.Constants._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

@js.native
trait CPU extends js.Object {
    val limit: Int = js.native
    val tickLimit: Int = js.native
    val bucket: Int = js.native
    val shardLimits: js.Dictionary[Int] = js.native

    /**
     * Get amount of CPU time used from the beginning of the current game tick.
     *
     * Always returns 0 in the Simulation mode.
     *
     * @return the currently used CPU time as a float number
     * @example
     * {{{
     * if(Game.cpu.getUsed() > Game.cpu.tickLimit / 2) {
     *   console.log("Used half of CPU already!");
     * }
     * }}}
     * @example
     * {{{
     * for(var name in Game.creeps) {
     *   var startCpu = Game.cpu.getUsed();
     *
     *   // creep logic goes here
     *
     *   var elapsed = Game.cpu.getUsed() - startCpu;
     *   console.log('Creep '+name+' has used '+elapsed+' CPU time');
     * }
     * }}}
     *
     */
    def getUsed(): Int = js.native

    /**
     * Allocate CPU limits to different shards. Total amount of CPU should remain equal to [[Game.cpu.shardLimits]]. This method can be used only once per 12 hours.
     *
     * @param limits An object with CPU values for each shard in the same format as [[Game.cpu.shardLimits]].
     * @return Error code; one of [[Error.OK]], [[Error.Busy]], or [[Error.InvalidArgs]].
     */
    def setShardLimits(limits: js.Dictionary[Int]): Int = js.native
}

@js.native
trait GlobalProgressLevel extends js.Object {
    /** The current level. */
    val level: Int = js.native
    /** The current progress to the next level. */
    val progress: Int = js.native
    /** The progress required to reach the next level. */
    val progressTotal: Int = js.native
}

@js.native
trait Shard extends js.Object {
    /** The name of the shard. */
    val name: String = js.native
    /** Currently always equals to {{{normal}}}. */
    val `type`: String = js.native
    /** Whether this shard belongs to the <a href="https://docs.screeps.com/ptr.html">PTR</a>. */
    val ptr: Boolean = js.native
}

/**
 * The main game object containing all the gameplay information. The object is accessible via the global Game variable.
 */
@js.native
@JSGlobal
object Game extends js.Object {
    /** A hash containing all your construction sites with their id as hash keys. */
    val constructionSites: js.Dictionary[ConstructionSite] = js.native
    /** A hash containing all your construction sites with their id as hash keys. */
    val cpu: CPU = js.native
    /** A hash containing all your creeps with creep names as hash keys. */
    val creeps: js.Dictionary[Creep] = js.native
    /** A hash containing all your flags with flag names as hash keys. */
    val flags: js.Dictionary[Flag] = js.native
    /** Your <a href="http://support.screeps.com/hc/en-us/articles/203086021-Territory-control">Global Control Level</a>. */
    val gcl: GlobalProgressLevel = js.native
    /** Your Global Power Level (similar to [[gcl]] in how it operates). */
    val gpl: GlobalProgressLevel = js.native
    /** A global object representing world map. */
    val map: Map = js.native
    /** A global object representing the in-game market. */
    val market: Market = js.native
    // TODO
    /** A hash containing all your power creeps with their names as hash keys. Even power creeps not spawned in the world can be accessed here. */
    // val powerCreeps: js.Dictionary[PowerCreep] = js.native
    /** An object with your global resources that are bound to the account, like subscription tokens. Each object key is a resource constant, values are resources amounts. */
    // val resources: js.Dictionary[???] = js.native
    /**
     * A hash containing all the rooms available to you with room names as hash keys.
     * A room is visible if you have a creep or an owned structure in it.
     */
    val rooms: js.Dictionary[Room] = js.native
    /** An object describing the world shard where your script is currently being executed in. */
    val shard: Shard = js.native
    /** A hash containing all your spawns with spawn names as hash keys. */
    val spawns: js.Dictionary[StructureSpawn] = js.native
    /** A hash containing all your structures with structure id as hash keys. */
    val structures: js.Dictionary[Structure] = js.native
    /**
     * System game tick counter. It is automatically incremented on every tick.
     *
     * {code}console.log(Game.time);{code}
     */
    val time: Int @@ Ticks = js.native

    /**
     * Get an object with the specified unique ID. It may be a game object of any type. Only objects from the rooms which are visible to you can be accessed.
     *
     * @param id The unique identificator.
     * @return Returns an object instance or null if it cannot be found.
     * @example {{{
     *         creep.memory.sourceId = creep.pos.findClosestByRange(FIND_SOURCES).id
     *         var source = Game.getObjectById(creep.memory.sourceId);
     * }}}
     */
    def getObjectById(id: String): js.Object = js.native

    /**
     * Send a custom message at your profile email. This way, you can set up notifications to yourself on any occasion
     * within the game. You can schedule up to 20 notifications during one game tick.
     * Not available in the Simulation Room.
     *
     * @param message       Custom text which will be sent in the message. Maximum length is 1000 characters.
     * @param groupInterval If set to 0 (default), the notification will be scheduled immediately. Otherwise, it will
     *                      be grouped with other notifications and mailed out later using the specified time in minutes.
     */
    def notify(message: String, groupInterval: Int): Unit = js.native
}
