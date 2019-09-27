package mahera.atom_layer_counter

const val MIN_LAYER_DISTANCE = 0.2f

class Bundle(
    val inputPath : String,
    val outputPath : String,
    val writeDistances : Boolean = true,
    val axis: Axis = Axis.Z,
    distance : Float = MIN_LAYER_DISTANCE){

    val layerDistance = if (distance < 0) MIN_LAYER_DISTANCE
                        else distance

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Bundle

        if (inputPath != other.inputPath) return false
        if (outputPath != other.outputPath) return false
        if (writeDistances != other.writeDistances) return false
        if (axis != other.axis) return false
        if (layerDistance != other.layerDistance) return false

        return true
    }

    override fun hashCode(): Int {
        var result = inputPath.hashCode()
        result = 31 * result + outputPath.hashCode()
        result = 31 * result + writeDistances.hashCode()
        result = 31 * result + axis.hashCode()
        result = 31 * result + layerDistance.hashCode()
        return result
    }
}