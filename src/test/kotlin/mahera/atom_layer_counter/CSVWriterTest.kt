package mahera.atom_layer_counter

import org.junit.Before
import org.junit.Test
import java.io.File

const val OUTPUT_PATH = "src/test/resources/output_test.csv"
class CSVWriterTest{

    lateinit var writer : Writer
    @Before
    fun before(){
        writer = CSVWriter()
    }

    @Test
    fun testWriter(){
        writer.writeResult(getStructuredFrames(), Bundle("", OUTPUT_PATH))

        val file = File(OUTPUT_PATH)
        assert(file.exists())
        assert(file.isFile)
        assert(file.length() > 0)
    }

    private fun getStructuredFrames() : List<StructuredFrame>{
        val respond = mutableListOf<StructuredFrame>()
        repeat(20){
            respond.add(getStructuredFrame())
        }
        return respond
    }

    private fun getStructuredFrame() : StructuredFrame {
        val respond = StructuredFrame()
        for (i in 0 until 20){
            val layer = Layer(i.toDouble(), 100 - i)
            respond.addLayer(layer)
        }
        return respond
    }
}