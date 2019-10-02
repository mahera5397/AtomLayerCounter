package mahera.atom_layer_counter

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import java.io.BufferedWriter
import java.io.FileWriter
import kotlin.coroutines.coroutineContext

const val WRITER_SUFFIX = "_processed.csv"

@ExperimentalCoroutinesApi
class CSVWriter : Writer {
    private lateinit var bufWriter : BufferedWriter
    private var outPath = ""

    override suspend fun writeResult(structuredFrames: Channel<StructuredFrame>, bundle: Bundle){
        outPath = bundle.outputPath + WRITER_SUFFIX
        CoroutineScope(Dispatchers.IO + coroutineContext).launch {
            consumeChannel(structuredFrames, bundle)
        }
    }

    private suspend fun consumeChannel(structuredFrames: Channel<StructuredFrame>,
                                       bundle: Bundle) {
        bufWriter = getBufWriter(outPath)
        structuredFrames.consumeEach {
            val strings = prepareStrings(it, bundle.writeAdditionalInfo)
            for (string in strings) {
                bufWriter.write(string)
                bufWriter.newLine()
            }
        }.run { closeBufWriter() }
    }

    private suspend fun getBufWriter(outPath : String) : BufferedWriter =
        withContext(Dispatchers.IO){
            BufferedWriter(FileWriter(outPath))
        }

    private fun prepareStrings(frame: StructuredFrame, writeInfo : Boolean)
            : MutableList<String> {
        val writeOut = mutableListOf<String>()

        if (writeInfo) writeOut.add("Step: ${frame.step};")
        for (type in frame.layers.keys) {
            if (writeInfo) writeOut.add("Type: $type;")
            val distance = StringBuilder("Distances;")
            val quantity = StringBuilder("Quantity;")
            for (layer in frame.layers[type]!!) {
                quantity.append(layer.counted)
                    .append(';')
                if (writeInfo) {
                    distance.append(layer.layerDistance)
                        .append(';')
                }
            }
            if (writeInfo) writeOut.add(distance.toString())
            writeOut.add(quantity.toString())
        }
        return writeOut
    }

    private suspend fun closeBufWriter() =
        withContext(Dispatchers.IO){
            bufWriter.close()
        }
}