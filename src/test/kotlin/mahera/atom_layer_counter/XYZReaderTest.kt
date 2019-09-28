package mahera.atom_layer_counter

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test

const val INPUT_PATH = "src/test/resources/input_test.xyz"
const val FRAMES = 3
const val ATOMS = 46

@ExperimentalCoroutinesApi
class XYZReaderTest {

    private val reader = XYZReader()
    private val bundle = Bundle(INPUT_PATH, "")

    @Test
    fun test() = runBlockingTest{
        val channel = reader.read(bundle)
        channel.invokeOnClose {
            launch(coroutineContext) {
                val list = channelToListAsync(channel).await()

                assertEquals(list.size, FRAMES)
                assertEquals(list.first().atoms.size, ATOMS)
            }
        }
    }
}