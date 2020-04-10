package com.screeps.native

import scala.language.implicitConversions
import scalajs.js.Dynamic.{ global => g }

trait Tagging {
    type Tagged[A] = { type Tag = A }
    type @@[T, U] = T with Tagged[U]

    // making these package private so that you as a user must rebuild them if you want to use something outside the api
    private[native] sealed class Tagger[U] { def apply[T](t: T): T @@ U = t.asInstanceOf[T @@ U]}
    private[native] def tag[U] = new Tagger[U]
}

object Constants extends Tagging {
    sealed trait ResourceAmount // tagging trait
    sealed trait Ticks

    sealed trait Direction
    object Direction extends Enumeration {
        private implicit def tagId(i: Int) = tag[Direction](i)
        protected case class Val(name: String, i: Int @@ Direction = nextId) extends super.Val(i, name) {
            override def id: Int @@ Direction = i
        }
        val Top         = Val("Top")
        val TopRight    = Val("TopRight")
        val Right       = Val("Right")
        val BottomRight = Val("BottomRight")
        val Bottom      = Val("Bottom")
        val BottomLeft  = Val("BottomLeft")
        val Left        = Val("Left")
        val TopLeft     = Val("TopLeft")

        implicit def name(v: Value): Int @@ Direction = v.id
    }

    sealed trait Error
    object Error extends Enumeration {
        private implicit def tagError[T](t: T): T @@ Error = tag[Error](t)
        protected case class Val(i: Int @@ Error, name: String) extends super.Val(i, name) {
            override def id: Int @@ Error = i
        }
        val OK                 = Val(0, "OK")
        val NotOwner           = Val(-1, "NotOwner")
        val NoPath             = Val(-2, "NoPath")
        val NameExists         = Val(-3, "NameExists")
        val Busy               = Val(-4, "Busy")
        val NotFound           = Val(-5, "NotFound")
        val NotEnoughResources = Val(-6, "NotEnoughResources")
        val InvalidTarget      = Val(-7, "InvalidTarget")
        val Full               = Val(-8, "Full")
        val NotInRange         = Val(-9, "NotInRange")
        val InvalidArgs        = Val(-10, "InvalidArgs")
        val Tired              = Val(-11, "Tired")
        val NoBodypart         = Val(-12, "NoBodypart")
        val RCLNotEnough       = Val(-14, "RCLNotEnough")
        val GCLNotEnough       = Val(-15, "GCLNotEnough")
    }

    object GameMode extends Enumeration {
        val Simulation = Value("simulation")
        val Survival   = Value("survival")
        val World      = Value("world")
        val Arena      = Value("arena")
    }

    sealed trait StructureType
    object StructureType extends Enumeration {
        private implicit def tagName(s: String): String @@ StructureType = tag[StructureType](s)
        private implicit def tagCost(c: Int): Int @@ ResourceAmount = tag[ResourceAmount](c)
        protected case class Val(name: String @@ StructureType, cost: Int @@ ResourceAmount = 0) extends super.Val(name) {
            def constructable: Boolean = cost != 0
        }

        val Spawn       = Val("spawn",           15_000)
        val Extension   = Val("extension",       3_000)
        val Road        = Val("road",            300)
        val Wall        = Val("constructedWall", 1)
        val Rampart     = Val("rampart",         1)
        val KeeperLair  = Val("keeperLair")
        val Portal      = Val("portal")
        val Controller  = Val("controller")
        val Link        = Val("link",            5_000)
        val Storage     = Val("storage",         30_000)
        val Tower       = Val("tower",           5_000)
        val Observer    = Val("observer",        8_000)
        val PowerBank   = Val("powerBankPowerBank")
        val PowerSpawn  = Val("powerSpawn",      100_000)
        val Extractor   = Val("extractor",       5_000)
        val Lab         = Val("lab",             50_000)
        val Terminal    = Val("terminal",        100_000)
        val Container   = Val("container",       5_000)
        val Nuker       = Val("nuker",           100_000)
        val Factory     = Val("factory",         100_000)
        val InvaderCore = Val("invaderCore")

        implicit def name(v: Val): String @@ StructureType = v.name
        implicit def cost(v: Val): Int @@ ResourceAmount = v.cost
    }

    sealed trait Color
    object Color extends Enumeration {
        private implicit def tagColor[T](t: T): T @@ Color = tag[Color](t)
        protected case class Val(name: String @@ Color) extends super.Val(name)
        val Red    = Val("red")
        val Purple = Val("purple")
        val Blue   = Val("blue")
        val Cyan   = Val("cyan")
        val Green  = Val("green")
        val Yellow = Val("yellow")
        val Orange = Val("orange")
        val Brown  = Val("brown")
        val Grey   = Val("grey")
        val White  = Val("white")

        implicit def id(v: Val): Int @@ Color = v.id
        implicit def name(v: Val): String @@ Color = v.name
    }

    sealed trait BodypartType
    object BodypartType extends Enumeration {
        private implicit def tagName(s: String): String @@ BodypartType = tag[BodypartType](s)
        private implicit def tagCost(i: Int): Int @@ ResourceAmount = tag[ResourceAmount](i)
        private implicit def tagTicks(i: Int): Int @@ Ticks = tag[Ticks](i)
        protected case class Val(name: String @@ BodypartType, cost: Int @@ ResourceAmount, maxLifetime: Int @@ Ticks = 1500) extends super.Val(name)
        val Move          = Val("move", 50)
        val Work          = Val("work", 100)
        val Carry         = Val("carry", 50)
        val Attack        = Val("attack", 80)
        val Ranged_attack = Val("ranged_attack", 150)
        val Tough         = Val("tough", 10)
        val Heal          = Val("heal", 250)
        val Claim         = Val("claim", 600, 600)

        implicit def name(v: Val): String @@ BodypartType = v.name
        implicit def cost(v: Val): Int @@ ResourceAmount = v.cost
        implicit def maxLifetime(v: Val): Int @@ Ticks = v.maxLifetime
    }
    object Bodyparts {
        val Move: String @@ BodypartType = g.MOVE.asInstanceOf[String @@ BodypartType]
        val Work: String @@ BodypartType = g.WORK.asInstanceOf[String @@ BodypartType]
        val Carry: String @@ BodypartType = g.CARRY.asInstanceOf[String @@ BodypartType]
    }

    sealed trait FindType
    object FindType extends Enumeration {
        private implicit def tagId(i: Int): Int @@ FindType = tag[FindType](i)
        private implicit def tagName(name: String): String @@ FindType = tag[FindType](name)
        protected case class Val(i: Int @@ FindType, name: String @@ FindType) extends super.Val(i, name)

        val ExitTop                  = Val(Direction.Top.id, "EXIT_TOP")
        val ExitRight                = Val(Direction.Right.id, "EXIT_RIGHT")
        val ExitBottom               = Val(Direction.Bottom.id, "EXIT_BOTTOM")
        val ExitLeft                 = Val(Direction.Left.id, "EXIT_LEFT")
        val Exit                     = Val(10, "EXIT")
        val Creeps                   = Val(101, "CREEPS")
        val MyCreeps                 = Val(102, "MY_CREEPS")
        val HostileCreeps            = Val(103, "HOSTILE_CREEPS")
        val SourcesActive            = Val(104, "SOURCES_ACTIVE")
        val Sources                  = Val(105, "SOURCES")
        val DroppedResources         = Val(106, "DROPPED_RESOURCES")
        val Structures               = Val(107, "STRUCTURES")
        val MyStructures             = Val(108, "MY_STRUCTURES")
        val HostileStructures        = Val(109, "HOSTILE_STRUCTURES")
        val Flags                    = Val(110, "FLAGS")
        val ConstructionSites        = Val(111, "CONSTRUCTION_SITES")
        val MySpawns                 = Val(112, "MY_SPAWNS")
        val HostileSpawns            = Val(113, "HOSTILE_SPAWNS")
        val MyConstructionSites      = Val(114, "MY_CONSTRUCTION_SITES")
        val HostileConstructionSites = Val(115, "HOSTILE_CONSTRUCTION_SITES")
        val Minerals                 = Val(116, "MINERALS")
        val Nukes                    = Val(117, "NUKES")
        val Tombstones               = Val(118, "FIND_TOMBSTONES")
        val PowerCreeps              = Val(119, "FIND_POWER_CREEPS")
        val MyPowerCreeps            = Val(120, "FIND_MY_POWER_CREEPS")
        val HostilePowerCreeps       = Val(121, "FIND_HOSTILE_POWER_CREEPS")
        val Deposits                 = Val(122, "FIND_DEPOSITS")
        val Ruins                    = Val(123, "FIND_RUINS")

        implicit def id(v: Val): Int @@ FindType = v.i
        implicit def name(v: Val): String @@ FindType = v.name
    }

    sealed trait LookType
    object LookType extends Enumeration {
        private implicit def tagName(s: String): String @@ LookType = tag[LookType](s)
        protected case class Val(name: String @@ LookType) extends super.Val(name)

        val Creeps            = Value("creep")
        val Energy            = Value("energy")
        val Resources         = Value("resource")
        val Sources           = Value("source")
        val Minerals          = Value("mineral")
        val Structures        = Value("structure")
        val Flags             = Value("flag")
        val ConstructionSites = Value("constructionSite")
        val Nukes             = Value("nuke")
        val Terrain           = Value("terrain")
        val Tombstones        = Value("tombstone")
        val PowerCreeps       = Value("powerCreep")
        val Ruins             = Value("ruin")

        implicit def name(v: Val): String @@ LookType = v.name
    }

    sealed trait ResourceType
    object ResourceType extends Enumeration {
        private implicit def tagName(s: String): String @@ ResourceType = tag[ResourceType](s)
        protected case class Val(name: String @@ ResourceType) extends super.Val(name)
        val Energy = Val("energy")
        val Power  = Val("power")

        val Hydrogen  = Val("H")
        val Oxygen    = Val("O")
        val Utrium    = Val("U")
        val Lemergium = Val("L")
        val Keanium   = Val("K")
        val Zynthium  = Val("Z")
        val Catalyst  = Val("X")
        val Ghodium   = Val("G")

        val Hydroxie        = Val("OH")
        val ZynthiumKeanite = Val("ZK")
        val UtriumLemergite = Val("UL")

        val UtriumHydride    = Val("UH")
        val UtriumOxide      = Val("UO")
        val KeaniumHydride   = Val("KH")
        val KeaniumOxide     = Val("KO")
        val LemergiumHydrive = Val("LH")
        val LemergiumOxide   = Val("LO")
        val ZynthiumHydride  = Val("ZH")
        val ZynthiumOxide    = Val("ZO")
        val GhodiumHydride   = Val("GH")
        val GhodiumOxide     = Val("GO")

        val UtriumAcide       = Val("UH2O")
        val UtriumAlkalide    = Val("UHO2")
        val KeaniumAcid       = Val("KH2O")
        val KeaniumAlkalide   = Val("KHO2")
        val LemergiumAcid     = Val("LH2O")
        val LemergiumAlkalide = Val("LHO2")
        val ZynthiumAcid      = Val("ZH2O")
        val ZynthiumAlkalide  = Val("ZHO2")
        val GhodiumAcid       = Val("GH2O")
        val GhodiumAlkalide   = Val("GHO2")

        val CatalyzedUtriumAcid        = Val("XUH2O")
        val CatalyzedUtriumAlkalide    = Val("XUHO2")
        val CatalyzedKeaniumAcid       = Val("XKH2O")
        val CatalyzedKeaniumAlkalide   = Val("XKHO2")
        val CatalyzedLemergiumAcid     = Val("XLH2O")
        val CatalyzedLemergiumAlkalide = Val("XLHO2")
        val CatalyzedZynthiumAcide     = Val("XZH2O")
        val CatalyzedZynthiumAlkalide  = Val("XZHO2")
        val CatalyzedGhodiumAcid       = Val("XGH2O")
        val CatalyzedGhodiumAlkalide   = Val("XGHO2")

        implicit def name(v: Val): String @@ ResourceType = v.name
    }

    sealed trait Density
    object Density extends Enumeration {
        private implicit def tagDensity[A](a: A): A @@ Density = tag[Density](a)
        private implicit def tagAmount[A](a: A): A @@ ResourceAmount = tag[ResourceAmount](a)
        // name is not used in game, but enumeration likes it better to be explicit
        protected case class Val(i: Int @@ Density, name: String @@ Density, amount: Int @@ ResourceAmount, probability: Float) extends super.Val(i, name)

        val Low = Val(1, "Low", 15000, 0.1F)
        val Moderate = Val(2, "Moderate", 35000, 0.4F)
        val High = Val(3, "High", 70000, 0.4F)
        val Ultra = Val(4, "Ultra", 100000, 0.1F)

        implicit def density(v: Val): Int @@ Density = v.i
        implicit def amount(v: Val): Int @@ ResourceAmount = v.amount
    }
}
