import mahera.atom_layer_counter.Atom
import mahera.atom_layer_counter.Counter
import mahera.atom_layer_counter.Frame
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CounterTest {
    private val xArray = floatArrayOf(1.0f, 2.0f, 3.0f, 4.0f, 5.0f)
    private val yArray = floatArrayOf(2.0f, 3.0f, 4.0f, 5.0f, 1.0f)
    private val zArray = floatArrayOf(3.0f, 4.0f, 5.0f, 1.0f, 2.0f)

    lateinit var counter: Counter

    @Before
    fun before(){
        counter = Counter(generateFrames())
    }

    @Test
    fun testCounter(){
        val result = counter.processFrames()
        assertEquals(zArray.size, result[1].keys.size)
        assertEquals(xArray.map { it.toDouble() }.toSet(), result[2].keys)
        assertEquals(25, result[3][2.0])
    }

    private fun generateFrames() : List<Frame>{
        val frames = mutableListOf<Frame>()
        repeat(4){
            frames.add(Frame(generateAtoms()))
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