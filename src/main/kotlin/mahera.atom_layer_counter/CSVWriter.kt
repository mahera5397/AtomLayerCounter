package mahera.atom_layer_counter

import java.io.BufferedWriter
import java.io.FileWriter

const val WRITER_SUFFIX = "_processed.csv"
class CSVWriter : Writer {

    override fun writeResult(model: List<StructuredFrame>, bundle: Bundle) {
        val writeOut = prepareStrings(model, bundle)
        writeOut(bundle, writeOut)
    }

    private fun prepareStrings(model: List<StructuredFrame>, bundle: Bundle)
            : MutableList<String> {
        val writeOut = mutableListOf<String>()
        for (frame in model) {
            if (bundle.writeAdditionalInfo) writeOut.add("Step: ${frame.step};")
            for (type in frame.layers.keys) {
                if (bundle.writeAdditionalInfo) writeOut.add("Type: $type;")
                val distance = StringBuilder("Distances;")
                val quantity = StringBuilder("Quantity;")
                for (layer in frame.layers[type]!!) {
                    quantity.append(layer.counted)
                        .append(';')
                    if (bundle.writeAdditionalInfo) {
                        distance.append(layer.layerDistance)
                            .append(';')
                    }
                }
                if (bundle.writeAdditionalInfo) writeOut.add(distance.toString())
                writeOut.add(quantity.toString())
            }
        }
        return writeOut
    }

    private fun writeOut(bundle: Bundle, writeOut: MutableList<String>) {
        val outPath = bundle.outputPath + WRITER_SUFFIX
        BufferedWriter(FileWriter(outPath)).use {
            for (line in writeOut) {
                it.write(line)
                it.newLine()
            }
        }
    }
}