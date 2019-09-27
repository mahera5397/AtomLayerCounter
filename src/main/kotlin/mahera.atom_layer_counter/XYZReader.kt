package mahera.atom_layer_counter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import mahera.atom_layer_counter.StringType.*
import java.io.File

class XYZReader : Reader {
    private var channel = Channel<RawFrame>(Channel.UNLIMITED)
    private var atomQuantity = -1

    @ExperimentalCoroutinesApi
    override suspend fun read(bundle: Bundle): Channel<RawFrame> {
        if (channel.isClosedForSend) channel = Channel()
        var currentAtoms = mutableListOf<Atom>()
        var currentStep = -1
        CoroutineScope(Dispatchers.IO).launch {
            readAsStrings(bundle).consumeEach {
                val type =
                    if (!it.contains(QUANTITY_CHAR)) Quantity
                    else if (it.contains(TIME_STEP_CHAR)) Step
                    else Position

                when (type) {
                    Position -> currentAtoms.add(convertStringToAtom(it))
                    Step -> currentStep = it.substringAfter(TIME_STEP_LINE).toInt()
                    Quantity -> {
                        if (currentAtoms.isNotEmpty()) throw CorruptedFileException(MISSING_ATOM)
                        atomQuantity = it.toInt()
                    }
                }
                if (currentAtoms.size == atomQuantity) {
                    println("sending through raw channel")
                    channel.send(RawFrame(currentAtoms, currentStep))
                    currentAtoms = mutableListOf()
                }
            }
                .run {
                    println("closing raw channel")
                    channel.close()
                }
        }
        return channel
    }

    @ExperimentalCoroutinesApi
    private suspend fun readAsStrings(bundle: Bundle): Channel<String> {
        val channel = Channel<String>(Channel.UNLIMITED)
        CoroutineScope(Dispatchers.IO).launch{
            File(bundle.inputPath).bufferedReader().use {
                var line: String? = it.readLine()
                if (line != null) {
                    do {
                        channel.send(line!!)
                        line = it.readLine()
                    } while (line != null)
                }
                channel.close()
            }
        }
        return channel
    }

    private fun convertStringToAtom(string : String) : Atom {
        val strings = string.split(' ')
        if (strings.size != POSITION_ELEMENTS) throw CorruptedFileException()
        return Atom(strings[0], strings[1].toFloat(),
            strings[2].toFloat(), strings[3].toFloat())
    }
}