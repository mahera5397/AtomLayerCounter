package mahera.atom_layer_counter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

const val MAX_DELTA = 0.01
const val MIN_IN_LAYER = 4
const val LAYER_DISTANCE = 1.0

@ExperimentalCoroutinesApi
class CounterImpl : Counter{

    private var averageOfLayers = mutableListOf<Double>()
    private var channel = Channel<StructuredFrame>(Channel.UNLIMITED)

    override suspend fun count(rawFrames : Channel<RawFrame>, bundle : Bundle)
            : Channel<StructuredFrame> {
        if (channel.isClosedForSend) channel = Channel(Channel.UNLIMITED)
        CoroutineScope(Dispatchers.Unconfined).launch {
            sendStructuredFrames(rawFrames, bundle)
        }
        return channel
    }

    private suspend fun sendStructuredFrames(rawFrames: Channel<RawFrame>, bundle: Bundle) {
        rawFrames.consumeEach {
            channel.send(processFrame(it, bundle))
        }.run { channel.close() }
    }

    private fun processFrame(rawFrame : RawFrame, bundle : Bundle) : StructuredFrame{
        return if (rawFrame.atoms.isNotEmpty()){
            val sortedFrame = toFloatList(rawFrame, bundle.axis)
            val respond = defineLayersAndCount(sortedFrame)
            respond.step = rawFrame.step
            respond
        }
        else StructuredFrame()
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

    private fun defineLayersAndCount(sortedFrame: List<Float>) :StructuredFrame{
        val definedLayers = defineLayers(sortedFrame)
        mergeCloseLayers(definedLayers)
        val mapOfLayers = countBySmallestDelta(sortedFrame)
        return convertMapToStructuredFrame(mapOfLayers)
    }

    private fun defineLayers(sortedFrame: List<Float>): MutableList<MutableList<Float>> {
        val definedLayers = mutableListOf<MutableList<Float>>()
        var currentLayer = mutableListOf<Float>()

        for (position in sortedFrame) {
            if (currentLayer.isEmpty()) {
                currentLayer.add(position)
                continue
            }
            if (position - currentLayer.last() > MAX_DELTA) {
                if (currentLayer.size >= MIN_IN_LAYER) {
                    definedLayers.add(currentLayer)
                }
                currentLayer = mutableListOf()
                currentLayer.add(position)
            } else {
                currentLayer.add(position)
            }
        }
        if (currentLayer.size >= MIN_IN_LAYER) definedLayers.add(currentLayer)
        return definedLayers
    }

    private fun mergeCloseLayers(definedLayers: MutableList<MutableList<Float>>) {
        if (definedLayers.size > 1) {
            var toMerge = checkForMerging(definedLayers)
            while (toMerge.isNotEmpty()) {
                val firstLayerIndex = toMerge.first().first
                val secondLayerIndex = toMerge.first().second
                val firstLayer = definedLayers[firstLayerIndex]
                val secondLayer = definedLayers[secondLayerIndex]
                definedLayers.remove(secondLayer)
                firstLayer.addAll(secondLayer)
                toMerge = checkForMerging(definedLayers)
            }
        }
    }

    private fun checkForMerging(definedLayers: MutableList<MutableList<Float>>) : List<Pair<Int, Int>>{
        averageOfLayers = mutableListOf()
        val toMerge = mutableListOf<Pair<Int, Int>>()
        var cachedAverage = Double.MIN_VALUE
        for (index in 0 until definedLayers.size-1) {
            val currentAverage = if (cachedAverage == Double.MIN_VALUE) {
                val current = definedLayers[index].average()
                averageOfLayers.add(current)
                current
            }
            else cachedAverage
            val nextAverage = definedLayers[index + 1].average()
            averageOfLayers.add(nextAverage)
            if (nextAverage - currentAverage < LAYER_DISTANCE) {
                toMerge.add(Pair(index, index + 1))
                break
            }
            cachedAverage = nextAverage
        }
        return toMerge
    }

    private fun countBySmallestDelta(sortedFrame: List<Float>): MutableMap<Double, Int> {
        val mapOfLayers = mutableMapOf<Double, Int>()
        for (average in averageOfLayers) mapOfLayers[average] = 0
        var searched = Double.MIN_VALUE
        var currentDelta = Double.MAX_VALUE
        for (atom in sortedFrame) {
            for (key in mapOfLayers.keys) {
                if ((key - atom).absoluteValue < currentDelta) {
                    searched = key
                    currentDelta = (key - atom).absoluteValue
                }
            }
            if (searched != Double.MIN_VALUE) mapOfLayers[searched] = mapOfLayers[searched]!! + 1
            currentDelta = Double.MAX_VALUE
            searched = Double.MIN_VALUE
        }
        return mapOfLayers
    }

    private fun convertMapToStructuredFrame(mapOfLayers: MutableMap<Double, Int>): StructuredFrame {
        val respond = StructuredFrame()
        for (key in mapOfLayers.keys) {
            respond.addLayer(Layer(key, mapOfLayers[key]!!))
        }
        return respond
    }
}