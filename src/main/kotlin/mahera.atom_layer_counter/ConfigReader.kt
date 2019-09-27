package mahera.atom_layer_counter

import com.google.gson.Gson
import java.io.File

const val CONFIG_FILE = "config"
const val XYZ_FILE = ".xyz"
const val JSON_FILE = ".json"

class ConfigReaderImpl : ConfigReader {
    companion object{
        private val gson = Gson()
    }

    override fun readConfig(path : String): List<Bundle> {
        val dir = File(path)
        return if (dir.isDirectory && dir.list() != null){
            val configs = mutableListOf<String>()
            val xyz = mutableListOf<String>()

            for (file in dir.list()!!){
                if (file.contains(CONFIG_FILE, true) && file.endsWith(JSON_FILE)) configs.add(path + file)
                else if (file.endsWith(XYZ_FILE)) xyz.add(path + file)
            }
            if (configs.isEmpty()) createDefaultBundles(xyz)
            else createBundles(configs)
        }
        else emptyList()
    }

    private fun createDefaultBundles(xyzFiles : List<String>) : List<Bundle>{
        val respond = mutableListOf<Bundle>()
        for (xyzFile in xyzFiles){
            val out = xyzFile.substringBefore(XYZ_FILE)
            val bundle = Bundle(xyzFile, out)
            respond.add(bundle)
        }
        return respond
    }

    private fun createBundles(configs : List<String>) : List<Bundle>{
        val respond = mutableListOf<Bundle>()
        for (config in configs){
            File(config).bufferedReader()
                .use{
                    respond.addAll(
                        gson.fromJson(it.readText(), Array<Bundle>::class.java))
                }
        }
        return respond
    }
}