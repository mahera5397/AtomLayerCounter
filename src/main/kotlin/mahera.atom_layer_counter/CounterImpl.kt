package mahera.atom_layer_counter

class CounterImpl : Counter{

    override fun count(rawFrames : List<RawFrame>, bundle : Bundle)
            : List<StructuredFrame> {
        val response = mutableListOf<StructuredFrame>()
        for(frame in rawFrames)
            response.add(processFrame(frame, bundle))
        return response
    }

    private fun processFrame(rawFrame : RawFrame, bundle : Bundle) : StructuredFrame{
        val sortedFrame = toFloatList(rawFrame, bundle.axis)
        return if (sortedFrame.isNotEmpty())
            countAtom(sortedFrame, bundle.layerDistance)
        else StructuredFrame()
    }

    private fun countAtom(sortedFrame: List<Float>, layerDistance : Float)
            : StructuredFrame {
        val distanceToQuantity = StructuredFrame()

        var previous = sortedFrame.first()
        var layerList = mutableListOf(previous)

        for (value in 1 until sortedFrame.size) {
            if (sortedFrame[value] - previous > layerDistance) {
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

    private fun toFloatList(rawFrame: RawFrame, axis : Axis): List<Float> {
        return rawFrame.atoms.asSequence()
            .map { atomMappingClosure(it, axis) }
            .sortedBy { it }
            .toList()
    }

    private fun atomMappingClosure(it : Atom, axis : Axis) =
        when (axis){
            Axis.X -> it.x
            Axis.Y -> it.y
            Axis.Z -> it.z
        }

//    private fun countAtomWithType(rawFrame : RawFrame) : StructuredFrame{
//        val byType = mutableMapOf<>()rawFrame.atoms.
//    }
}