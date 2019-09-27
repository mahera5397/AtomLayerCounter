package mahera.atom_layer_counter

import java.io.BufferedWriter
import java.io.FileWriter

const val WRITER_SUFFIX = "_processed.csv"
class CSVWriter : Writer {
    override fun writeResult(model: List<StructuredFrame>, bundle: Bundle) {
        val writeOut = mutableListOf<String>()
        for (frame in model){
            writeOut.add("Step: ${frame.step};")
            for (type in frame.layers.keys){
                writeOut.add("Type: $type;")
                val distance = StringBuilder("Distances;")
                val quantity = StringBuilder("Quantity;")
                for (layer in frame.layers[type]!!){
                    quantity.append(layer.counted)
                        .append(';')
                    if (bundle.writeDistances) {
                        distance.append(layer.layerDistance)
                            .append(';')
                    }
                }
                if (bundle.writeDistances) writeOut.add(distance.toString())
                writeOut.add(quantity.toString())
            }
        }
        val outPath = bundle.outputPath + WRITER_SUFFIX
        BufferedWriter(FileWriter(outPath)).use {
            for (line in writeOut){
                it.write(line)
                it.newLine()
            }
        }
    }
}