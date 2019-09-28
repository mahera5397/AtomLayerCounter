package mahera.atom_layer_counter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import mahera.atom_layer_counter.StringType.*
import java.io.File

@ExperimentalCoroutinesApi
class XYZReader : Reader {
    private var channel = Channel<RawFrame>(Channel.UNLIMITED)
    private var currentAtoms = mutableListOf<Atom>()
    private var currentStep = -1
    private var atomQuantity = -1

    override suspend fun read(bundle: Bundle): Channel<RawFrame> {
        if (channel.isClosedForSend) channel = Channel(Channel.UNLIMITED)
        CoroutineScope(Dispatchers.Unconfined).launch {
            sendRawFrames(bundle)
        }
        return channel
    }

    private suspend fun sendRawFrames(bundle: Bundle) {
        readAsStrings(bundle).consumeEach {
            when (getStringType(it)) {
                Position -> currentAtoms.add(convertStringToAtom(it))
                Step -> currentStep = it.substringAfter(TIME_STEP_LINE).toInt()
                Quantity -> {
                    if (currentAtoms.isNotEmpty()) throw CorruptedFileException(MISSING_ATOM)
                    atomQuantity = it.toInt()
                }
            }
            checkIfFrameIsComplete()
        }.run { channel.close() }
    }

    private suspend fun readAsStrings(bundle: Bundle): Channel<String> {
        val channel = Channel<String>(Channel.UNLIMITED)
        CoroutineScope(Dispatchers.IO).launch{
            sendThroughStringChannel(bundle, channel)
        }
        return channel
    }

    private suspend fun sendThroughStringChannel(bundle: Bundle, channel: Channel<String>) {
        File(bundle.inputPath).bufferedReader()
            .use {
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

    private fun getStringType(it: String): StringType {
        return if (!it.contains(QUANTITY_CHAR)) Quantity
        else if (it.contains(TIME_STEP_CHAR)) Step
        else Position
    }

    private suspend fun checkIfFrameIsComplete() {
        if (currentAtoms.size == atomQuantity) {
            channel.send(RawFrame(currentAtoms, currentStep))
            currentAtoms = mutableListOf()
        }
    }

    private fun convertStringToAtom(string : String) : Atom {
        val strings = string.split(' ')
        if (strings.size != POSITION_ELEMENTS) throw CorruptedFileException()
        return Atom(strings[0], strings[1].toFloat(),
            strings[2].toFloat(), strings[3].toFloat())
    }
}