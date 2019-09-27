package mahera.atom_layer_counter

import org.junit.Assert.assertEquals
import org.junit.Test

class CounterImplTest {

    private val counterImpl = CounterImpl()

    @Test
    fun testCounter(){
        val result = counterImpl.count(generateFrames(), Bundle("",""))
        assertEquals(zArray.size, result[1].layers[""]!!.size)
//        assertEquals(xArray.map { it.toDouble() }.toSet(), result[2].lakeys)
//        assertEquals(25, result[3][2.0])
    }
}