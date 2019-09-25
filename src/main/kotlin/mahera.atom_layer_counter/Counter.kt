package mahera.atom_layer_counter

class Counter(private val frames : List<Frame>,
              private val bundle : Bundle = Bundle()) {

    fun processFrames() : List<Map<Double, Int>>{
        val response = mutableListOf<Map<Double, Int>>()
        for(frame in frames)
            response.add(processFrame(frame))
        return response
    }

    private fun processFrame(frame : Frame) : Map<Double, Int>{
        val sortedFrame = toFloatList(frame);
        return if (sortedFrame.isNotEmpty())
            countAtom(sortedFrame)
        else emptyMap()
    }

    private fun countAtom(sortedFrame: List<Float>): MutableMap<Double, Int> {
        val distanceToQuantity = mutableMapOf<Double, Int>()

        var previous = sortedFrame.first()
        var layerList = mutableListOf(previous)

        for (value in 1 until sortedFrame.size) {
            if (sortedFrame[value] - previous > bundle.layerDistance) {
                distanceToQuantity[layerList.average()] = layerList.size
                layerList = mutableListOf()
            }
            layerList.add(sortedFrame[value])
            previous = sortedFrame[value]
        }
        distanceToQuantity[layerList.average()] = layerList.size
        return distanceToQuantity
    }

    private fun toFloatList(frame: Frame): List<Float> {
        return frame.atoms.asSequence()
            .map { atomMappingClosure(it) }
            .sortedBy { it }
            .toList()
    }

    private fun atomMappingClosure(it : Atom) = when (bundle.axis){
        Axis.X -> it.x
        Axis.Y -> it.y
        Axis.Z -> it.z
    }
}