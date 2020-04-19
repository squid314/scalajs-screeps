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
        ext: UndefOr[T],
        structureType: StructureType.Value,
    )
    : T = macro Impls.structureCached_Impl[T]

    def structuresCached[T <: Structure]
    (
        ext: UndefOr[js.Array[T]],
        structureType: StructureType.Value,
    )
    : js.Array[T] = macro Impls.structuresCached_Impl[T]

    private object Impls {
        def structureCached_Impl[T <: Structure : c.WeakTypeTag]
        (c: blackbox.Context)
        (
            ext: c.Expr[UndefOr[T]],
            structureType: c.Expr[StructureType.Value],
        )
        : c.Expr[T] = {
            import c.universe._
            val t = weakTypeOf[T]
            val tree = genCodeForStructureCache(c)(ext, structureType, t)(multiple = false)
            c.Expr[T](tree)
        }

        def structuresCached_Impl[T <: Structure : c.WeakTypeTag]
        (c: blackbox.Context)
        (
            ext: c.Expr[UndefOr[js.Array[T]]],
            structureType: c.Expr[StructureType.Value],
        )
        : c.Expr[js.Array[T]] = {
            import c.universe._
            val t = weakTypeOf[T]
            val tree = genCodeForStructureCache(c)(ext, structureType, t)(multiple = true)
            c.Expr[js.Array[T]](tree)
        }

        private def genCodeForStructureCache[T: c.WeakTypeTag](c: blackbox.Context)(ext: c.Expr[UndefOr[T]], structureType: c.Expr[Value], t: c.universe.Type)(multiple: Boolean) = {
            import c.universe._
            val q"""RoomOps.this.room.${TermName(s"_${memName}")}""" = ext.tree
            val assignment =
                if (multiple) q"""$ext = structures.asInstanceOf[UndefOr[js.Array[$t]]]"""
                else q"""$ext = structures.headOption.asInstanceOf[UndefOr[$t]]"""
            val cacheCast =
                if (multiple) q"""materialized.asInstanceOf[UndefOr[js.Array[$t]]]"""
                else q"""materialized.headOption.asInstanceOf[UndefOr[$t]]"""
            q"""
                if ($ext.isEmpty) {
                    val cache = room.memory.selectDynamic($memName).asInstanceOf[UndefOr[StructureCache]]
                            .getOrElse {
                                val cache = new StructureCache()
                                room.memory.updateDynamic($memName)(cache)
                                cache
                            }
                    if (cache.maxAge > Game.time) {
                        val materialized = cache.ids
                            .flatMap(id => Game.getObjectById(id)
                                .asInstanceOf[UndefOr[$t]].toOption)
                        $ext = $cacheCast
                    } else {
                        val filter = ((s: Structure) => s.structureType == $structureType).asInstanceOf[js.Object => Boolean]
                        val structures = room.find(Find.Structures, FindOptions(filter)).asInstanceOf[js.Array[$t]]
                        room.memory.updateDynamic($memName)(new StructureCache(Game.time + Config.maxStructureCache, structures.map(_.id)))
                        $assignment
                    }
                }
                $ext.get
                """
        }
    }

}
