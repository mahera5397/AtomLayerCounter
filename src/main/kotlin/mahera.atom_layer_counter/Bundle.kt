package mahera.atom_layer_counter

class Bundle(
    val inputPath : String,
    var outputPath : String,
    val writeAdditionalInfo : Boolean = false,
    val axis: Axis = Axis.Z,
    distance : Double = LAYER_DISTANCE){

    val layerDistance = if (distance < 0.0) LAYER_DISTANCE
                        else distance

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Bundle

        if (inputPath != other.inputPath) return false
        if (outputPath != other.outputPath) return false
        if (writeAdditionalInfo != other.writeAdditionalInfo) return false
        if (axis != other.axis) return false
        if (layerDistance != other.layerDistance) return false

        return true
    }

    override fun hashCode(): Int {
        var result = inputPath.hashCode()
        result = 31 * result + outputPath.hashCode()
        result = 31 * result + writeAdditionalInfo.hashCode()
        result = 31 * result + axis.hashCode()
        result = 31 * result + layerDistance.hashCode()
        return result
    }
}