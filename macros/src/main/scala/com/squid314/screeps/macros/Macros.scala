package com.squid314.screeps.macros

import com.screeps.native.Constants.StructureType.Value
import com.screeps.native._
import com.screeps.native.Constants._

import scalajs.js
import scalajs.js._
import scala.language.experimental.macros
import scala.reflect.macros.blackbox

object Macros {
    def structureCached[T <: Structure]
    (
        ext: UndefOr[Option[T]],
        structureType: StructureType.Value,
    )
    : Option[T] = macro Impls.structureCached_Impl[T]

    def structuresCached[T <: Structure]
    (
        ext: UndefOr[List[T]],
        structureType: StructureType.Value,
    )
    : List[T] = macro Impls.structuresCached_Impl[T]

    private object Impls {
        def structureCached_Impl[T <: Structure : c.WeakTypeTag]
        (c: blackbox.Context)
        (
            ext: c.Expr[UndefOr[Option[T]]],
            structureType: c.Expr[StructureType.Value],
        )
        : c.Expr[Option[T]] = {
            import c.universe._
            val t = weakTypeOf[T]
            val tree = genCodeForStructureCache(c)(ext, structureType, t)(multiple = false)
            c.Expr[Option[T]](tree)
        }

        def structuresCached_Impl[T <: Structure : c.WeakTypeTag]
        (c: blackbox.Context)
        (
            ext: c.Expr[UndefOr[List[T]]],
            structureType: c.Expr[StructureType.Value],
        )
        : c.Expr[List[T]] = {
            import c.universe._
            val t = weakTypeOf[T]
            val tree = genCodeForStructureCache(c)(ext, structureType, t)(multiple = true)
            c.Expr[List[T]](tree)
        }

        private def genCodeForStructureCache[T: c.WeakTypeTag](c: blackbox.Context)(ext: c.Expr[UndefOr[T]], structureType: c.Expr[Value], t: c.universe.Type)(multiple: Boolean) = {
            import c.universe._
            val q"""RoomOps.this.room.${TermName(s"_${memName}")}""" = ext.tree
            val boxT =
                if (multiple) tq"""List[$t]"""
                else tq"""Option[$t]"""
            val get =
                if (multiple) q"""listStructures"""
                else q"""listStructures.headOption"""
            q"""
                $ext.getOrElse(
                    room.memory.selectDynamic($memName).asInstanceOf[UndefOr[StructureCache]]
                        .toOption
                        .filter(_.maxAge <= Game.time)
                        .map(_.ids)
                        .map(List.from(_))
                        .map(ids => {
                            val listStructures = ids.flatMap(Game.getObjectById(_).asInstanceOf[UndefOr[$t]].toOption)
                            $ext = $get.asInstanceOf[UndefOr[$boxT]]
                            $get
                        })
                        .getOrElse({
                            val filter: scala.scalajs.js.Function1[Structure, Boolean] = (s: Structure) => s.structureType == $structureType.name
                            val structures = room.find(Find.Structures, FindOptions(filter)).asInstanceOf[js.Array[$t]]
                            room.memory.updateDynamic($memName)(StructureCache(Game.time + com.squid314.screeps.Config.maxStructureCache, structures.map(_.id)))
                            val listStructures = List.from(structures)
                            $ext = $get.asInstanceOf[UndefOr[$boxT]]
                            $get
                        })
                )
               """
        }
    }

}
