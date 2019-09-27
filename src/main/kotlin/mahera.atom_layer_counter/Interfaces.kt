package mahera.atom_layer_counter

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.channels.Channel

interface Reader{
    suspend fun read(bundle: Bundle) : Channel<RawFrame>
}

interface ConfigReader{
    suspend fun readConfigAsync(path : String) : Deferred<Channel<Bundle>>
}

interface Counter{
    suspend fun count(rawFrames : Channel<RawFrame>, bundle : Bundle)
            : Channel<StructuredFrame>
}

interface Writer{
    suspend fun writeResult(model : Channel<StructuredFrame>, bundle : Bundle)
}