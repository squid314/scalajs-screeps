package com.screeps.native

import com.screeps.native.Constants._

import scala.scalajs.js
import scala.scalajs.js.|

/** TODO Blerg... */
sealed trait FindOptions

/** TODO Blerg... */
object FindOptions {
    type Filter[T] = js.Function2[T, Int, Boolean] | js.Function1[T, Boolean] | String | js.Dynamic

    final case class F2FindOptions[T <: js.Object](filter: js.Function2[T, Int, Boolean]) extends FindOptions
    final case class F1FindOptions[T <: js.Object](filter: js.Function1[T, Boolean]) extends FindOptions
    final case class SFindOptions(filter: String) extends FindOptions
    final case class OFindOptions(filter: js.Object) extends FindOptions

    def apply[T <: js.Object](f: js.Function2[T, Int, Boolean]): FindOptions = F2FindOptions(f)

    def apply[T <: js.Object](f: js.Function1[T, Boolean]): FindOptions = F1FindOptions(f)

    def apply[T <: js.Object](f: String): FindOptions = SFindOptions(f)

    def apply[T <: js.Object](f: js.Object): FindOptions = OFindOptions(f)

    import language.implicitConversions

    implicit def toJsBecauseHooray(f: FindOptions): js.Object @@ FindOptions =
        tag[FindOptions](f match {
            case F2FindOptions(filter) => js.Dynamic.literal(filter = filter)
            case F1FindOptions(filter) => js.Dynamic.literal(filter = filter)
            case SFindOptions(filter) => js.Dynamic.literal(filter = filter)
            case OFindOptions(filter) => filter
        })
}
