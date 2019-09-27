package mahera.atom_layer_counter

import org.junit.Assert.assertEquals
import org.junit.Test

class CounterImplTest {

    private val counterImpl = CounterImpl()

    @Test
    fun testCounter(){
        val result = counterImpl.count(generateFrames(), Bundle("",""))
        assertEquals(zArray.size, result[1].layers[""]!!.size)
        val distances = mutableSetOf<Double>()
        for (layer in result[2].layers[""]!!){
            distances.add(layer.layerDistance)
        }
        assertEquals(xArray.map { it.toDouble() }.toSet(), distances)
        assertEquals(25, result[3].layers[""]!!.first().counted)
    }
}