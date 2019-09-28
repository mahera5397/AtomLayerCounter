package mahera.atom_layer_counter

import kotlinx.coroutines.channels.Channel

interface Reader{
    suspend fun read(bundle: Bundle) : Channel<RawFrame>
}

interface ConfigReader{
    suspend fun readConfig(path : String) : Channel<Bundle>
}

interface Counter{
    suspend fun count(rawFrames : Channel<RawFrame>, bundle : Bundle)
            : Channel<StructuredFrame>
}

interface Writer{
    suspend fun writeResult(structuredFrames : Channel<StructuredFrame>, bundle : Bundle)
}