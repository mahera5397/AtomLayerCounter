package mahera.atom_layer_counter

import org.junit.Assert.assertEquals
import org.junit.Test

const val CONFIG_INPUT_PATH = "src/test/resources/"
const val CONFIG_INPUT_QUANTITY = 5
const val NO_CONFIG_INPUT_PATH = "src/test/resources/noconfig/"
const val NO_CONFIG_INPUT_QUANTITY = 2
class ConfigReaderTest {

    private val configReader = ConfigReaderImpl()
    private val modBundle = customBundle()
    private val defaultBundle = Bundle("","")

    @Test
    fun testWithConfigs(){
        val result = configReader.readConfig(CONFIG_INPUT_PATH)
        assertEquals(result.size, CONFIG_INPUT_QUANTITY)
        assert(result.contains(modBundle))
    }

    @Test
    fun testWithXYZs(){
        val result = configReader.readConfig(NO_CONFIG_INPUT_PATH)
        assertEquals(result.size, NO_CONFIG_INPUT_QUANTITY)
        with(result.first()) {
            assertEquals(writeAdditionalInfo, defaultBundle.writeAdditionalInfo)
            assertEquals(layerDistance, defaultBundle.layerDistance)
            assertEquals(axis, defaultBundle.axis)
            assert(outputPath.contains(inputPath.substringBefore(XYZ_FILE)))
        }
    }
}