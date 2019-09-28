package mahera.atom_layer_counter

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test

const val CONFIG_INPUT_PATH = "src/test/resources/"
const val CONFIG_INPUT_QUANTITY = 5
const val NO_CONFIG_INPUT_PATH = "src/test/resources/noconfig/"
const val NO_CONFIG_INPUT_QUANTITY = 2

@ExperimentalCoroutinesApi
class ConfigReaderTest {

    private val configReader = ConfigReaderImpl()
    private val modBundle = customBundle()
    private val defaultBundle = Bundle("","")

    @Test
    fun testWithConfigs() = runBlockingTest{
        val channel = configReader.readConfig(CONFIG_INPUT_PATH)
        channel.invokeOnClose {
            launch(coroutineContext) {
                val list = channelToListAsync(channel).await()

                assertEquals(list.size, CONFIG_INPUT_QUANTITY)
                assert(list.contains(modBundle))
            }
        }
    }

    @Test
    fun testWithXYZs() = runBlockingTest {
        val channel = configReader.readConfig(NO_CONFIG_INPUT_PATH)
        channel.invokeOnClose {
            launch(coroutineContext) {
                val list = channelToListAsync(channel).await()

                assertEquals(list.size, NO_CONFIG_INPUT_QUANTITY)
                with(channel.iterator().next()) {
                    assertEquals(writeAdditionalInfo, defaultBundle.writeAdditionalInfo)
                    assertEquals(layerDistance, defaultBundle.layerDistance)
                    assertEquals(axis, defaultBundle.axis)
                    assert(outputPath.contains(inputPath.substringBefore(XYZ_FILE)))
                }
            }
        }
    }
}