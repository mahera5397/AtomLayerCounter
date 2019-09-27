package mahera.atom_layer_counter

interface Reader{
    fun read(bundle: Bundle) : List<RawFrame>
}

interface ConfigReader{
    fun readConfig(path : String) : List<Bundle>
}

interface Counter{
    fun count(rawFrames : List<RawFrame>, bundle : Bundle)
            : List<StructuredFrame>
}

interface Writer{
    fun writeResult(model : List<StructuredFrame>, bundle : Bundle)
}