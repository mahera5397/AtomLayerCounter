package mahera.atom_layer_counter

class CounterImpl(override val rawFrames : List<RawFrame>,
                  override val bundle : Bundle) : Counter{

    override fun count(): List<StructuredFrame> {
        val response = mutableListOf<StructuredFrame>()
        for(frame in rawFrames)
            response.add(processFrame(frame))
        return response
    }

    private fun processFrame(rawFrame : RawFrame) : StructuredFrame{
        val sortedFrame = toFloatList(rawFrame);
        return if (sortedFrame.isNotEmpty())
            countAtom(sortedFrame)
        else StructuredFrame()
    }

    private fun countAtom(sortedFrame: List<Float>): StructuredFrame {
        val distanceToQuantity = StructuredFrame()

        var previous = sortedFrame.first()
        var layerList = mutableListOf(previous)

        for (value in 1 until sortedFrame.size) {
            if (sortedFrame[value] - previous > bundle.layerDistance) {
                val layer = Layer(layerList.average(), layerList.size)
                distanceToQuantity.addLayer(layer)
                layerList = mutableListOf()
            }
            layerList.add(sortedFrame[value])
            previous = sortedFrame[value]
        }
        val layer = Layer(layerList.average(), layerList.size)
        distanceToQuantity.addLayer(layer)
        return distanceToQuantity
    }

    private fun toFloatList(rawFrame: RawFrame): List<Float> {
        return rawFrame.atoms.asSequence()
            .map { atomMappingClosure(it) }
            .sortedBy { it }
            .toList()
    }

    private fun atomMappingClosure(it : Atom) = when (bundle.axis){
        Axis.X -> it.x
        Axis.Y -> it.y
        Axis.Z -> it.z
    }

//    private fun countAtomWithType(rawFrame : RawFrame) : StructuredFrame{
//        val byType = mutableMapOf<>()rawFrame.atoms.
//    }
}