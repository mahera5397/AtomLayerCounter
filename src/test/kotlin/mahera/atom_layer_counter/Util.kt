package mahera.atom_layer_counter

import com.google.gson.Gson
import java.io.File

const val CUSTOM_BUNDLE_PATH = "src/test/resources/config2.json"

val xArray = floatArrayOf(1.0f, 2.0f, 3.0f, 4.0f, 5.0f)
val yArray = floatArrayOf(2.0f, 3.0f, 4.0f, 5.0f, 1.0f)
val zArray = floatArrayOf(3.0f, 4.0f, 5.0f, 1.0f, 2.0f)
val gson = Gson()

fun getStructuredFrames() : List<StructuredFrame>{
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

fun generateFrames() : List<RawFrame>{
    val frames = mutableListOf<RawFrame>()
    repeat(4){
        frames.add(RawFrame(generateAtoms()))
    }
    return frames
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