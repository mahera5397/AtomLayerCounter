package mahera.atom_layer_counter

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

val PATH = System.getProperty("user.dir") + '/'

@ExperimentalCoroutinesApi
fun main() {
    val time = measureTimeMillis {
        runBlocking {
            val configReader: ConfigReader = ConfigReaderImpl()
            val bundleChannel = configReader.readConfig(PATH)
            bundleChannel.consumeEach {
                val reader: Reader = XYZReader()
                val counter: Counter = CounterImpl()
                val writer: Writer = CSVWriter()

                val rawChannel = reader.read(it)
                val countedChannel = counter.count(rawChannel, it)
                writer.writeResult(countedChannel, it)
            }
        }
    }
    println("runtime is $time millisecond")
}