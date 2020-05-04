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

    sealed trait Direction
    object Direction extends Enumeration {
        private implicit def tagId(i: Int): Int @@ Direction = tag[Direction](i)
        type Value = Valu
        protected case class Valu(name: String, i: Int @@ Direction = nextId) extends super.Val(i, name) {
            override def id: Int @@ Direction = i
        }
        val Top         = Valu("Top")
        val TopRight    = Valu("TopRight")
        val Right       = Valu("Right")
        val BottomRight = Valu("BottomRight")
        val Bottom      = Valu("Bottom")
        val BottomLeft  = Valu("BottomLeft")
        val Left        = Valu("Left")
        val TopLeft     = Valu("TopLeft")

        @inline implicit def name(v: Value): Int @@ Direction = v.id
    }

    sealed trait Error
    object Error extends Enumeration {
        private implicit def tagError[T](t: T): T @@ Error = tag[Error](t)
        type Value = Valu
        protected case class Valu(i: Int @@ Error, name: String) extends super.Val(i, name) {
            override def id: Int @@ Error = i
        }
        val OK                 = Valu(0, "OK")
        val NotOwner           = Valu(-1, "NotOwner")
        val NoPath             = Valu(-2, "NoPath")
        val NameExists         = Valu(-3, "NameExists")
        val Busy               = Valu(-4, "Busy")
        val NotFound           = Valu(-5, "NotFound")
        val NotEnoughResources = Valu(-6, "NotEnoughResources")
        val InvalidTarget      = Valu(-7, "InvalidTarget")
        val Full               = Valu(-8, "Full")
        val NotInRange         = Valu(-9, "NotInRange")
        val InvalidArgs        = Valu(-10, "InvalidArgs")
        val Tired              = Valu(-11, "Tired")
        val NoBodypart         = Valu(-12, "NoBodypart")
        val RCLNotEnough       = Valu(-14, "RCLNotEnough")
        val GCLNotEnough       = Valu(-15, "GCLNotEnough")
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
        type Value = Valu
        protected case class Valu(name: String @@ StructureType, cost: Int @@ ResourceAmount = 0) extends super.Val(name) {
            def constructable: Boolean = cost != 0
        }

        val Spawn       = Valu("spawn",           15_000)
        val Extension   = Valu("extension",       3_000)
        val Road        = Valu("road",            300)
        val Wall        = Valu("constructedWall", 1)
        val Rampart     = Valu("rampart",         1)
        val KeeperLair  = Valu("keeperLair")
        val Portal      = Valu("portal")
        val Controller  = Valu("controller")
        val Link        = Valu("link",            5_000)
        val Storage     = Valu("storage",         30_000)
        val Tower       = Valu("tower",           5_000)
        val Observer    = Valu("observer",        8_000)
        val PowerBank   = Valu("powerBank")
        val PowerSpawn  = Valu("powerSpawn",      100_000)
        val Extractor   = Valu("extractor",       5_000)
        val Lab         = Valu("lab",             50_000)
        val Terminal    = Valu("terminal",        100_000)
        val Container   = Valu("container",       5_000)
        val Nuker       = Valu("nuker",           100_000)
        val Factory     = Valu("factory",         100_000)
        val InvaderCore = Valu("invaderCore")

        @inline implicit def name(v: Valu): String @@ StructureType = v.name
        @inline implicit def cost(v: Valu): Int @@ ResourceAmount = v.cost
    }

    sealed trait Color
    object Color extends Enumeration {
        private implicit def tagColor[T](t: T): T @@ Color = tag[Color](t)
        type Value = Valu
        protected case class Valu(name: String @@ Color) extends super.Val(name)
        val Red    = Valu("red")
        val Purple = Valu("purple")
        val Blue   = Valu("blue")
        val Cyan   = Valu("cyan")
        val Green  = Valu("green")
        val Yellow = Valu("yellow")
        val Orange = Valu("orange")
        val Brown  = Valu("brown")
        val Grey   = Valu("grey")
        val White  = Valu("white")

        @inline implicit def id(v: Valu): Int @@ Color = v.id
        @inline implicit def name(v: Valu): String @@ Color = v.name
    }

    sealed trait Bodypart
    object Bodypart extends Enumeration {
        private implicit def tagName(s: String): String @@ Bodypart = tag[Bodypart](s)
        private implicit def tagCost(i: Int): Int @@ ResourceAmount = tag[ResourceAmount](i)
        type Value = Valu
        protected case class Valu(name: String @@ Bodypart, cost: Int @@ ResourceAmount, maxLifetime: Int = 1500) extends super.Val(name)
        val Move          = Valu("move", 50)
        val Work          = Valu("work", 100)
        val Carry         = Valu("carry", 50)
        val Attack        = Valu("attack", 80)
        val Ranged_attack = Valu("ranged_attack", 150)
        val Tough         = Valu("tough", 10)
        val Heal          = Valu("heal", 250)
        val Claim         = Valu("claim", 600, 600)

        @inline implicit def name(v: Valu): String @@ Bodypart = v.name
        @inline implicit def cost(v: Valu): Int @@ ResourceAmount = v.cost
        @inline implicit def maxLifetime(v: Valu): Int = v.maxLifetime
    }

    sealed trait Find
    object Find extends Enumeration {
        private implicit def tagId(i: Int): Int @@ Find = tag[Find](i)
        private implicit def tagName(name: String): String @@ Find = tag[Find](name)
        type Value = Valu
        protected case class Valu(i: Int @@ Find, name: String @@ Find) extends super.Val(i, name)

        val ExitTop                  = Valu(Direction.Top.id, "EXIT_TOP")
        val ExitRight                = Valu(Direction.Right.id, "EXIT_RIGHT")
        val ExitBottom               = Valu(Direction.Bottom.id, "EXIT_BOTTOM")
        val ExitLeft                 = Valu(Direction.Left.id, "EXIT_LEFT")
        val Exit                     = Valu(10, "EXIT")
        val Creeps                   = Valu(101, "CREEPS")
        val MyCreeps                 = Valu(102, "MY_CREEPS")
        val HostileCreeps            = Valu(103, "HOSTILE_CREEPS")
        val SourcesActive            = Valu(104, "SOURCES_ACTIVE")
        val Sources                  = Valu(105, "SOURCES")
        val DroppedResources         = Valu(106, "DROPPED_RESOURCES")
        val Structures               = Valu(107, "STRUCTURES")
        val MyStructures             = Valu(108, "MY_STRUCTURES")
        val HostileStructures        = Valu(109, "HOSTILE_STRUCTURES")
        val Flags                    = Valu(110, "FLAGS")
        val ConstructionSites        = Valu(111, "CONSTRUCTION_SITES")
        val MySpawns                 = Valu(112, "MY_SPAWNS")
        val HostileSpawns            = Valu(113, "HOSTILE_SPAWNS")
        val MyConstructionSites      = Valu(114, "MY_CONSTRUCTION_SITES")
        val HostileConstructionSites = Valu(115, "HOSTILE_CONSTRUCTION_SITES")
        val Minerals                 = Valu(116, "MINERALS")
        val Nukes                    = Valu(117, "NUKES")
        val Tombstones               = Valu(118, "FIND_TOMBSTONES")
        val PowerCreeps              = Valu(119, "FIND_POWER_CREEPS")
        val MyPowerCreeps            = Valu(120, "FIND_MY_POWER_CREEPS")
        val HostilePowerCreeps       = Valu(121, "FIND_HOSTILE_POWER_CREEPS")
        val Deposits                 = Valu(122, "FIND_DEPOSITS")
        val Ruins                    = Valu(123, "FIND_RUINS")

        @inline implicit def id(v: Valu): Int @@ Find = v.i
        @inline implicit def name(v: Valu): String @@ Find = v.name
    }

    sealed trait Look
    object Look extends Enumeration {
        private implicit def tagName(s: String): String @@ Look = tag[Look](s)
        type Value = Valu
        protected case class Valu(name: String @@ Look) extends super.Val(name)

        val Creeps            = Valu("creep")
        val Energy            = Valu("energy")
        val Resources         = Valu("resource")
        val Sources           = Valu("source")
        val Minerals          = Valu("mineral")
        val Structures        = Valu("structure")
        val Flags             = Valu("flag")
        val ConstructionSites = Valu("constructionSite")
        val Nukes             = Valu("nuke")
        val Terrain           = Valu("terrain")
        val Tombstones        = Valu("tombstone")
        val PowerCreeps       = Valu("powerCreep")
        val Ruins             = Valu("ruin")

        @inline implicit def name(v: Valu): String @@ Look = v.name
    }

    sealed trait ResourceType
    object ResourceType extends Enumeration {
        private implicit def tagName(s: String): String @@ ResourceType = tag[ResourceType](s)
        type Value = Valu
        protected case class Valu(name: String @@ ResourceType) extends super.Val(name)
        val Energy = Valu("energy")
        val Power  = Valu("power")

        val Hydrogen  = Valu("H")
        val Oxygen    = Valu("O")
        val Utrium    = Valu("U")
        val Lemergium = Valu("L")
        val Keanium   = Valu("K")
        val Zynthium  = Valu("Z")
        val Catalyst  = Valu("X")
        val Ghodium   = Valu("G")

        val Hydroxie        = Valu("OH")
        val ZynthiumKeanite = Valu("ZK")
        val UtriumLemergite = Valu("UL")

        val UtriumHydride    = Valu("UH")
        val UtriumOxide      = Valu("UO")
        val KeaniumHydride   = Valu("KH")
        val KeaniumOxide     = Valu("KO")
        val LemergiumHydrive = Valu("LH")
        val LemergiumOxide   = Valu("LO")
        val ZynthiumHydride  = Valu("ZH")
        val ZynthiumOxide    = Valu("ZO")
        val GhodiumHydride   = Valu("GH")
        val GhodiumOxide     = Valu("GO")

        val UtriumAcide       = Valu("UH2O")
        val UtriumAlkalide    = Valu("UHO2")
        val KeaniumAcid       = Valu("KH2O")
        val KeaniumAlkalide   = Valu("KHO2")
        val LemergiumAcid     = Valu("LH2O")
        val LemergiumAlkalide = Valu("LHO2")
        val ZynthiumAcid      = Valu("ZH2O")
        val ZynthiumAlkalide  = Valu("ZHO2")
        val GhodiumAcid       = Valu("GH2O")
        val GhodiumAlkalide   = Valu("GHO2")

        val CatalyzedUtriumAcid        = Valu("XUH2O")
        val CatalyzedUtriumAlkalide    = Valu("XUHO2")
        val CatalyzedKeaniumAcid       = Valu("XKH2O")
        val CatalyzedKeaniumAlkalide   = Valu("XKHO2")
        val CatalyzedLemergiumAcid     = Valu("XLH2O")
        val CatalyzedLemergiumAlkalide = Valu("XLHO2")
        val CatalyzedZynthiumAcide     = Valu("XZH2O")
        val CatalyzedZynthiumAlkalide  = Valu("XZHO2")
        val CatalyzedGhodiumAcid       = Valu("XGH2O")
        val CatalyzedGhodiumAlkalide   = Valu("XGHO2")

        @inline implicit def name(v: Valu): String @@ ResourceType = v.name
    }

    sealed trait Density
    object Density extends Enumeration {
        private implicit def tagDensity[A](a: A): A @@ Density = tag[Density](a)
        private implicit def tagAmount[A](a: A): A @@ ResourceAmount = tag[ResourceAmount](a)
        type Value = Valu
        // name is not used in game, but enumeration likes it better to be explicit
        protected case class Valu(i: Int @@ Density, name: String @@ Density, amount: Int @@ ResourceAmount, probability: Float) extends super.Val(i, name)

        val Low = Valu(1, "Low", 15000, 0.1F)
        val Moderate = Valu(2, "Moderate", 35000, 0.4F)
        val High = Valu(3, "High", 70000, 0.4F)
        val Ultra = Valu(4, "Ultra", 100000, 0.1F)

        @inline implicit def density(v: Valu): Int @@ Density = v.i
        @inline implicit def amount(v: Valu): Int @@ ResourceAmount = v.amount
    }
}
