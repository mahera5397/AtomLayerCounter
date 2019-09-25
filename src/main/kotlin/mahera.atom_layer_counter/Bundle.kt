package mahera.atom_layer_counter

const val MIN_LAYER_DISTANCE = 0.5f

class Bundle(val axis: Axis = Axis.Z, distance : Float = MIN_LAYER_DISTANCE,
             val inputDirectory : String, val outputDirectory : String){
    val layerDistance = if (distance < 0) MIN_LAYER_DISTANCE
                        else distance
}