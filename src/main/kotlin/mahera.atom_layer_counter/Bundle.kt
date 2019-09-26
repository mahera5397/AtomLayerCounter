package mahera.atom_layer_counter

const val MIN_LAYER_DISTANCE = 0.5f

class Bundle(
    val inputPath : String,
    val outputPath : String,
    val writeDistances : Boolean = true,
    val axis: Axis = Axis.Z,
    distance : Float = MIN_LAYER_DISTANCE){

    val layerDistance = if (distance < 0) MIN_LAYER_DISTANCE
                        else distance
}