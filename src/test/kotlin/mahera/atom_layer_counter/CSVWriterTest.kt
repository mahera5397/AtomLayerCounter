package mahera.atom_layer_counter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.junit.Before
import org.junit.Test
import java.io.File

const val OUTPUT_PATH = "src/test/resources/output_test"

@ExperimentalCoroutinesApi
class CSVWriterTest{

    private val writer : Writer = CSVWriter()

    @Before
    fun write(){
        CoroutineScope(Dispatchers.IO).launch {
            writer.writeResult(getStructuredFrames(), Bundle("", OUTPUT_PATH))
        }
    }

    @Test
    fun testWriter() {
        val file = File(OUTPUT_PATH + WRITER_SUFFIX)
        assert(file.exists())
        assert(file.isFile)
        assert(file.length() > 0)
    }
}