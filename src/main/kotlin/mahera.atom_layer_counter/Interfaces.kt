package mahera.atom_layer_counter

interface Writer{
    fun writeResult(model : List<StructuredFrame>, bundle : Bundle)
}

interface Reader{
    fun read(bundle: Bundle) : List<RawFrame>
}

interface Counter{
    val rawFrames : List<RawFrame>
    val bundle : Bundle
    fun count() : List<StructuredFrame>
}