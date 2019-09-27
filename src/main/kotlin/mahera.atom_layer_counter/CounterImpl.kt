package mahera.atom_layer_counter

import kotlin.math.absoluteValue

const val MAX_DELTA = 0.01
const val MIN_IN_LAYER = 4
const val LAYER_DISTANCE = 1.0

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
            countAtom(sortedFrame)
        else StructuredFrame()
    }

    private fun countAtom(sortedFrame: List<Float>) :StructuredFrame{
        val definedLayers = mutableListOf<MutableList<Float>>()
        val controversial = mutableListOf<Float>()
        var currentLayer = mutableListOf<Float>()

        for (position in sortedFrame){
            if (currentLayer.isEmpty()){
                currentLayer.add(position)
                continue
            }
            if(position - currentLayer.last() > MAX_DELTA){
                if(currentLayer.size >= MIN_IN_LAYER){
                    definedLayers.add(currentLayer)
                }
                else{
                    controversial.addAll(currentLayer)
                }
                currentLayer = mutableListOf()
                currentLayer.add(position)
            }
            else{
                currentLayer.add(position)
            }
        }
        if(currentLayer.size >= MIN_IN_LAYER) definedLayers.add(currentLayer)
        else controversial.addAll(currentLayer)

        if (definedLayers.size > 1) {
            var merge= checkForMergin(definedLayers)
            while (merge.isNotEmpty()){
                val first = definedLayers[merge.first().first]
                val second = definedLayers[merge.first().second]
                definedLayers.remove(first)
                definedLayers.remove(second)
                first.addAll(second)
                definedLayers.add(merge.first().first, first)
                merge = checkForMergin(definedLayers)
            }
        }
        val mapOfLayers = mutableMapOf<Double, Int>()
        for (layer in definedLayers){
            mapOfLayers[layer.average()] = 0
        }
        var searched = -1.0
        var currentDelta = Double.MAX_VALUE
        for (atom in sortedFrame){
            for (key in mapOfLayers.keys){
                if ((key - atom).absoluteValue < currentDelta){
                    searched = key
                    currentDelta = (key - atom).absoluteValue
                }
            }
            if (searched != -1.0) mapOfLayers[searched] = mapOfLayers[searched]!!+1
            currentDelta = Double.MAX_VALUE
            searched = -1.0
        }

        val respond = StructuredFrame()
        for (key in mapOfLayers.keys){
            respond.addLayer(Layer(key, mapOfLayers[key]!!))
        }
        return respond
    }

    private fun checkForMergin(definedLayers: MutableList<MutableList<Float>>) : List<Pair<Int, Int>>{
        val toMerge = mutableListOf<Pair<Int, Int>>()
        for (index in 0 until definedLayers.size-1) {
            if (definedLayers[index + 1].average() - definedLayers[index].average() < LAYER_DISTANCE)
                toMerge.add(Pair(index, index + 1))
        }
        return toMerge
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
}