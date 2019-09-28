package mahera.atom_layer_counter

import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.io.File

const val CONFIG_FILE = "config"
const val XYZ_FILE = ".xyz"
const val JSON_FILE = ".json"

@ExperimentalCoroutinesApi
class ConfigReaderImpl : ConfigReader {
    private var channel = Channel<Bundle>()
    companion object{
        private val gson = Gson()
    }

    override suspend fun readConfig(path : String): Channel<Bundle>{
        if (channel.isClosedForSend) channel = Channel()
        CoroutineScope(Dispatchers.IO).launch {
            scanDirectoryAndSendBundles(path)
        }
        return channel
    }

    private suspend fun scanDirectoryAndSendBundles(path: String) {
        val (configs, defaultConfigs) = scanDirectory(path)
        if (!channel.isClosedForSend) {
            if (configs.isEmpty()) sendDefaultBundles(defaultConfigs)
            else sendBundles(configs)
        }
    }

    private fun scanDirectory(path: String): Pair<List<String>, List<String>> {
        val dir = File(path)
        val configs = mutableListOf<String>()
        val defaultConfigs = mutableListOf<String>()

        if (dir.isDirectory && dir.list() != null) {
            for (file in dir.list()!!) {
                if (file.contains(CONFIG_FILE, true) && file.endsWith(JSON_FILE)) configs.add(path + file)
                else if (file.endsWith(XYZ_FILE)) defaultConfigs.add(path + file)
            }
        }
        else channel.close()
        return Pair(configs, defaultConfigs)
    }

    private suspend fun sendDefaultBundles(xyzFiles : List<String>){
        for (xyzFile in xyzFiles){
            val out = xyzFile.substringBefore(XYZ_FILE)
            val bundle = Bundle(xyzFile, out)
            channel.send(bundle)
        }
        channel.close()
    }

    private suspend fun sendBundles(configs : List<String>){
        for (config in configs){
            File(config).bufferedReader()
                .use{
                    for (bundle in gson.fromJson(it.readText(), Array<Bundle>::class.java)){
                        channel.send(bundle)
                    }
                }
        }
        channel.close()
    }
}