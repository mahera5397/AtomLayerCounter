package mahera.atom_layer_counter

data class Frame(val atoms : List<Atom>)

data class Atom(val type : String, val x : Float, val y : Float,
                val z : Float)

enum class Axis{X, Y, Z}