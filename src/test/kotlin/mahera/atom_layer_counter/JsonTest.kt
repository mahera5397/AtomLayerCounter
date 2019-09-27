package mahera.atom_layer_counter

import com.google.gson.Gson
import org.junit.Test
import java.io.File

class JsonTest {

    //just to have json config example
    @Test
    fun writeJson(){
        val bundle = Bundle("some path", "another path")
        val listOfBundle = mutableListOf<Bundle>()
        repeat(5){
            listOfBundle.add(bundle)
        }
        val gson = Gson()
        val jsonString = gson.toJson(listOfBundle)
        val path = System.getProperty("user.dir")
        File("$path/bundle.json")
            .writeText(jsonString)
    }
}