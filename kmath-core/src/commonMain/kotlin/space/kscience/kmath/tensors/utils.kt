package space.kscience.kmath.tensors

import space.kscience.kmath.structures.*
import kotlin.math.max


internal inline fun broadcastShapes(vararg shapes: IntArray): IntArray {
    var totalDim = 0
    for (shape in shapes) {
        totalDim = max(totalDim, shape.size)
    }

    val totalShape = IntArray(totalDim) { 0 }
    for (shape in shapes) {
        for (i in shape.indices) {
            val curDim = shape[i]
            val offset = totalDim - shape.size
            totalShape[i + offset] = max(totalShape[i + offset], curDim)
        }
    }

    for (shape in shapes) {
        for (i in shape.indices) {
            val curDim = shape[i]
            val offset = totalDim - shape.size
            if (curDim != 1 && totalShape[i + offset] != curDim) {
                throw RuntimeException("Shapes are not compatible and cannot be broadcast")
            }
        }
    }

    return totalShape
}

internal inline fun broadcastTo(tensor: DoubleTensor, newShape: IntArray): DoubleTensor {
    if (tensor.shape.size > newShape.size) {
        throw RuntimeException("Tensor is not compatible with the new shape")
    }

    val n = newShape.reduce { acc, i -> acc * i }
    val resTensor = DoubleTensor(newShape, DoubleArray(n))

    for (i in tensor.shape.indices) {
        val curDim = tensor.shape[i]
        val offset = newShape.size - tensor.shape.size
        if (curDim != 1 && newShape[i + offset] != curDim) {
            throw RuntimeException("Tensor is not compatible with the new shape and cannot be broadcast")
        }
    }

    for (linearIndex in 0 until n) {
        val totalMultiIndex = resTensor.strides.index(linearIndex)
        val curMultiIndex = tensor.shape.copyOf()

        val offset = totalMultiIndex.size - curMultiIndex.size

        for (i in curMultiIndex.indices) {
            if (curMultiIndex[i] != 1) {
                curMultiIndex[i] = totalMultiIndex[i + offset]
            } else {
                curMultiIndex[i] = 0
            }
        }

        val curLinearIndex = tensor.strides.offset(curMultiIndex)
        resTensor.buffer.array()[linearIndex] =
            tensor.buffer.array()[tensor.bufferStart + curLinearIndex]
    }
    return resTensor
}

internal inline fun broadcastTensors(vararg tensors: DoubleTensor): List<DoubleTensor> {
    val totalShape = broadcastShapes(*(tensors.map { it.shape }).toTypedArray())
    val n = totalShape.reduce { acc, i -> acc * i }

    val res = ArrayList<DoubleTensor>(0)
    for (tensor in tensors) {
        val resTensor = DoubleTensor(totalShape, DoubleArray(n))

        for (linearIndex in 0 until n) {
            val totalMultiIndex = resTensor.strides.index(linearIndex)
            val curMultiIndex = tensor.shape.copyOf()

            val offset = totalMultiIndex.size - curMultiIndex.size

            for (i in curMultiIndex.indices) {
                if (curMultiIndex[i] != 1) {
                    curMultiIndex[i] = totalMultiIndex[i + offset]
                } else {
                    curMultiIndex[i] = 0
                }
            }

            val curLinearIndex = tensor.strides.offset(curMultiIndex)
            resTensor.buffer.array()[linearIndex] =
                tensor.buffer.array()[tensor.bufferStart + curLinearIndex]
        }
        res.add(resTensor)
    }

    return res
}

internal inline fun <T, TensorType : TensorStructure<T>,
        TorchTensorAlgebraType : TensorAlgebra<T, TensorType>>
        TorchTensorAlgebraType.checkDot(a: TensorType, b: TensorType): Unit {
    val sa = a.shape
    val sb = b.shape
    val na = sa.size
    val nb = sb.size
    var status: Boolean
    if (nb == 1) {
        status = sa.last() == sb[0]
    } else {
        status = sa.last() == sb[nb - 2]
        if ((na > 2) and (nb > 2)) {
            status = status and
                    (sa.take(nb - 2).toIntArray() contentEquals sb.take(nb - 2).toIntArray())
        }
    }
    check(status) { "Incompatible shapes $sa and $sb for dot product" }
}

internal inline fun <T, TensorType : TensorStructure<T>,
        TorchTensorAlgebraType : TensorAlgebra<T, TensorType>>
        TorchTensorAlgebraType.checkTranspose(dim: Int, i: Int, j: Int): Unit =
    check((i < dim) and (j < dim)) {
        "Cannot transpose $i to $j for a tensor of dim $dim"
    }

internal inline fun <T, TensorType : TensorStructure<T>,
        TorchTensorAlgebraType : TensorAlgebra<T, TensorType>>
        TorchTensorAlgebraType.checkView(a: TensorType, shape: IntArray): Unit =
    check(a.shape.reduce(Int::times) == shape.reduce(Int::times))

/**
 * Returns a reference to [IntArray] containing all of the elements of this [Buffer].
 */
internal fun Buffer<Int>.array(): IntArray = when(this) {
    is IntBuffer -> array
    else -> throw RuntimeException("Failed to cast Buffer to IntArray")
}

/**
 * Returns a reference to [LongArray] containing all of the elements of this [Buffer].
 */
internal fun Buffer<Long>.array(): LongArray = when(this) {
    is LongBuffer -> array
    else -> throw RuntimeException("Failed to cast Buffer to LongArray")
}

/**
 * Returns a reference to [FloatArray] containing all of the elements of this [Buffer].
 */
internal fun Buffer<Float>.array(): FloatArray = when(this) {
    is FloatBuffer -> array
    else -> throw RuntimeException("Failed to cast Buffer to FloatArray")
}

/**
 * Returns a reference to [DoubleArray] containing all of the elements of this [Buffer].
 */
internal fun Buffer<Double>.array(): DoubleArray = when(this) {
    is RealBuffer -> array
    else -> throw RuntimeException("Failed to cast Buffer to DoubleArray")
}
