package mahera.atom_layer_counter

import org.junit.Assert.assertEquals
import org.junit.Test

const val INPUT_PATH = "src/test/resources/input_test.xyz"
const val FRAMES = 3
const val ATOMS = 46
class XYZReaderTest {

    private val reader = XYZReader()
    private val bundle = Bundle(INPUT_PATH, "")

    @Test
    fun test(){
        val result = reader.read(bundle)
        assertEquals(result.size, FRAMES)
        assertEquals(result.first().atoms.size, ATOMS)
    }
}