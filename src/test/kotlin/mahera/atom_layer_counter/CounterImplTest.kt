package mahera.atom_layer_counter

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CounterImplTest {
    private val xArray = floatArrayOf(1.0f, 2.0f, 3.0f, 4.0f, 5.0f)
    private val yArray = floatArrayOf(2.0f, 3.0f, 4.0f, 5.0f, 1.0f)
    private val zArray = floatArrayOf(3.0f, 4.0f, 5.0f, 1.0f, 2.0f)

    lateinit var counterImpl: CounterImpl

    @Before
    fun before(){
        counterImpl = CounterImpl(generateFrames(), Bundle("",""))
    }

    @Test
    fun testCounter(){
        val result = counterImpl.count()
        assertEquals(zArray.size, result[1].layers[""]!!.size)
//        assertEquals(xArray.map { it.toDouble() }.toSet(), result[2].lakeys)
//        assertEquals(25, result[3][2.0])
    }

    private fun generateFrames() : List<RawFrame>{
        val frames = mutableListOf<RawFrame>()
        repeat(4){
            frames.add(RawFrame(generateAtoms()))
        }
        return frames
    }

    private fun generateAtoms() : List<Atom>{
        val response = mutableListOf<Atom>()
        for (x in xArray){
            for (y in yArray){
                for (z in zArray)
                {
                    response.add(Atom("Pt", x, y, z))
                }
            }
        }
        return response
    }
}