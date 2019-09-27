package mahera.atom_layer_counter

const val QUANTITY_CHAR = ' '
const val TIME_STEP_CHAR = ':'
const val TIME_STEP_LINE = "Atoms. Timestep: "
const val POSITION_ELEMENTS = 4

enum class Axis{X, Y, Z}

enum class StringType{Position, Quantity, Step}

data class Atom(val type : String, val x : Float, val y : Float,
                val z : Float)

data class RawFrame(val atoms : List<Atom>, val step : Int = -1)

data class Layer(val layerDistance : Double, val counted : Int,
                 val type : String = "")

class StructuredFrame{
    val layers : MutableMap<String, MutableList<Layer>> = mutableMapOf()
    var step : Int = 1

    fun addLayer(layer : Layer){
        if(!layers.containsKey(layer.type)) layers[layer.type] = mutableListOf()
        layers[layer.type]!!.add(layer)
    }
}