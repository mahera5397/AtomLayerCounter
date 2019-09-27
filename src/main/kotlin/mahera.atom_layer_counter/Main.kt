package mahera.atom_layer_counter

val path = System.getProperty("user.dir") + '/'

fun main(){

    val configReader : ConfigReader = ConfigReaderImpl()
    val reader : Reader = XYZReader()
    val counter : Counter = CounterImpl()
    val writer : Writer = CSVWriter()

    val bundles = configReader.readConfig(path)

    for (bundle in bundles){
        val raw = reader.read(bundle)
        val counted = counter.count(raw, bundle)
        writer.writeResult(counted, bundle)
    }
}