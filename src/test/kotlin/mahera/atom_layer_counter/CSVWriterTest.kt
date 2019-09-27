package mahera.atom_layer_counter

import org.junit.Test
import java.io.File

const val OUTPUT_PATH = "src/test/resources/output_test"
class CSVWriterTest{

    private val writer : Writer = CSVWriter()

    @Test
    fun testWriter(){
        writer.writeResult(getStructuredFrames(), Bundle("", OUTPUT_PATH))

        val file = File(OUTPUT_PATH + WRITER_SUFFIX)
        assert(file.exists())
        assert(file.isFile)
        assert(file.length() > 0)
    }
}