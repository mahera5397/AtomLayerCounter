package mahera.atom_layer_counter

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CounterImplTest {

    private val counterImpl = CounterImpl()
    private val list = mutableListOf<StructuredFrame>()

    @Before
    fun before() = runBlockingTest{
        val channel = counterImpl.count(generateRawFrames(), Bundle("", ""))
        channel.consumeEach {
            list.add(it)
        }
    }

    @Test
    fun testCounter() {
        assertEquals(zArray.size, list[1].layers[""]!!.size)
        val distances = mutableSetOf<Double>()
        for (layer in list[2].layers[""]!!) {
            distances.add(layer.layerDistance)
        }
        assertEquals(xArray.map { it.toDouble() }.toSet(), distances)
        assertEquals(xArray.size * yArray.size, list[3].layers[""]!!.first().counted)
    }
}