package mahera.atom_layer_counter

import mahera.atom_layer_counter.StringType.*
import java.io.File

class XYZReader : Reader {

    override fun read(bundle: Bundle): List<RawFrame> {
        val strings = readAsStrings(bundle)
        val model = mutableListOf<RawFrame>()

        var currentAtoms = mutableListOf<Atom>()
        var currentStep = -1
        var quantity = 0

        for (string in strings){
            val type =
                if (!string.contains(QUANTITY_CHAR)) Quantity
                else if (string.contains(TIME_STEP_CHAR)) Step
                else Position

            when (type){
                Quantity -> {
                    quantity = string.toInt()
                    if(currentAtoms.size == quantity){
                        model.add(RawFrame(currentAtoms, currentStep))
                        currentAtoms = mutableListOf()
                    }
                    else if (currentAtoms.isNotEmpty()) throw CorruptedFileException(MISSING_ATOM)
                }
                Step -> currentStep = string.substringAfter(TIME_STEP_LINE).toInt()
                Position -> currentAtoms.add(convertStringToAtom(string))
            }
        }
        if (currentAtoms.size == quantity) model.add(RawFrame(currentAtoms, currentStep))
        else throw CorruptedFileException(MISSING_ATOM)
        return model
    }

    private fun readAsStrings(bundle: Bundle): MutableList<String> {
        val strings = mutableListOf<String>()
        File(bundle.inputPath).bufferedReader().use {
            var line: String? = it.readLine()
            if (line != null) {
                do {
                    strings.add(line!!)
                    line = it.readLine()
                } while (line != null)
            }
        }
        return strings
    }

    private fun convertStringToAtom(string : String) : Atom {
        val strings = string.split(' ')
        if (strings.size != POSITION_ELEMENTS) throw CorruptedFileException()
        return Atom(strings[0], strings[1].toFloat(),
            strings[2].toFloat(), strings[3].toFloat())
    }
}