package mahera.atom_layer_counter

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.runBlocking

val path = System.getProperty("user.dir") + '/'

@ExperimentalCoroutinesApi
fun main() = runBlocking {
    println("starting...")
        val configReader : ConfigReader = ConfigReaderImpl()
        val bundleChannel = configReader.readConfigAsync(path)
            bundleChannel.await().consumeEach {
                println("consuming bundle")
                val reader: Reader = XYZReader()
                val counter: Counter = CounterImpl()
                val writer: Writer = CSVWriter()

                val rawChannel = reader.read(it)
                val countedChannel = counter.count(rawChannel, it)
                writer.writeResult(countedChannel, it)
            }
        println("bye")
}