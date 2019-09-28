package mahera.atom_layer_counter

import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import java.io.File

const val CUSTOM_BUNDLE_PATH = "src/test/resources/config2.json"

val xArray = floatArrayOf(1.0f, 2.0f, 3.0f, 4.0f, 5.0f)
val yArray = floatArrayOf(2.0f, 3.0f, 4.0f, 5.0f, 1.0f)
val zArray = floatArrayOf(3.0f, 4.0f, 5.0f, 1.0f, 2.0f)
val gson = Gson()

suspend fun getStructuredFrames() : Channel<StructuredFrame>{
    val respond = Channel<StructuredFrame>()
    repeat(20){
        respond.send(getStructuredFrame())
    }
    respond.close()
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

suspend fun generateRawFrames() : Channel<RawFrame>{
    val respond = Channel<RawFrame>()
    repeat(4){
        respond.send(RawFrame(generateAtoms()))
    }
    respond.close()
    return respond
}

private fun generateAtoms() : List<Atom>{
    val response = mutableListOf<Atom>()
    for (x in xArray){
        for (y in yArray){
            for (z in zArray)
            {
                response.add(Atom("Pt", x, y, z))
            }
        }
    }
    return response
}

fun customBundle() : Bundle{
    return File(CUSTOM_BUNDLE_PATH).bufferedReader()
        .use{
            gson.fromJson(it.readText(), Array<Bundle>::class.java)
                .first()
        }
}

fun<T> channelToListAsync(channel : Channel<T>) =
    CoroutineScope(Dispatchers.Unconfined).async{
        val respond = mutableListOf<T>()
        for (value in channel) respond.add(value)
        respond
    }