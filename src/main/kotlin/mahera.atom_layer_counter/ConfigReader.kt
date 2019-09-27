package mahera.atom_layer_counter

import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.io.File

const val CONFIG_FILE = "config"
const val XYZ_FILE = ".xyz"
const val JSON_FILE = ".json"

class ConfigReaderImpl : ConfigReader {
    private var channel = Channel<Bundle>(capacity = Channel.UNLIMITED)
    companion object{
        private val gson = Gson()
    }

    @ExperimentalCoroutinesApi
    override suspend fun readConfigAsync(path : String): Deferred<Channel<Bundle>> =
        CoroutineScope(Dispatchers.IO).async{
            val dir = File(path)
            if (channel.isClosedForSend) channel = Channel()
            if (dir.isDirectory && dir.list() != null){
                val configs = mutableListOf<String>()
                val xyz = mutableListOf<String>()

                for (file in dir.list()!!){
                    if (file.contains(CONFIG_FILE, true) && file.endsWith(JSON_FILE)) configs.add(path + file)
                    else if (file.endsWith(XYZ_FILE)) xyz.add(path + file)
                }
                launch {
                    if (configs.isEmpty())
                        createDefaultBundles(xyz)
                    else createBundles(configs)
                }
            }
            println("before returning bundle channel")
//            channel.close()
            channel
        }

    private suspend fun createDefaultBundles(xyzFiles : List<String>){
        for (xyzFile in xyzFiles){
            val out = xyzFile.substringBefore(XYZ_FILE)
            val bundle = Bundle(xyzFile, out)
            println("sending bundle")
            channel.send(bundle)
        }
        println("closing bundle channel")
        channel.close()
    }

    private suspend fun createBundles(configs : List<String>){
        for (config in configs){
            File(config).bufferedReader()
                .use{
                    for (bundle in gson.fromJson(it.readText(), Array<Bundle>::class.java)){
                        println("sending bundle")
                        channel.send(bundle)
                    }
                }
        }
        println("closing bundle channel")
        channel.close()
    }
}