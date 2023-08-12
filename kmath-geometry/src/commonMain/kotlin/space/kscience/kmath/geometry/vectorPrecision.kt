/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry


/**
 * Vector equality within given [precision] (using [GeometrySpace.norm] provided by the space
 */
public fun <V : Any, D : Comparable<D>> V.equalsVector(
    space: GeometrySpace<V, D>,
    other: V,
    precision: D = space.defaultPrecision,
): Boolean = with(space) {
    norm(this@equalsVector - other) < precision
}

/**
 * Vector equality using Euclidian L2 norm and given [precision]
 */
public fun Float64Vector2D.equalsVector(
    other: Float64Vector2D,
    precision: Double = Euclidean3DSpace.defaultPrecision,
): Boolean = equalsVector(Float64Space2D, other, precision)

/**
 * Vector equality using Euclidean L2 norm and given [precision]
 */
public fun Float64Vector3D.equalsVector(
    other: Float64Vector3D,
    precision: Double = Euclidean3DSpace.defaultPrecision,
): Boolean = equalsVector(Float64Space3D, other, precision)

/**
 * Line equality using [GeometrySpace.norm] provided by the [space] and given [precision]
 */
public fun <V : Any, D : Comparable<D>> LineSegment<V>.equalsLine(
    space: GeometrySpace<V, D>,
    other: LineSegment<V>,
    precision: D = space.defaultPrecision,
): Boolean = begin.equalsVector(space, other.begin, precision) && end.equalsVector(space, other.end, precision)