package mahera.atom_layer_counter

const val DEFAULT_MESSAGE = "error while processing xyz file." +
        "Check if it not corrupted"
const val MISSING_ATOM = "Quantity of atoms has been changed"

class CorruptedFileException(message : String = DEFAULT_MESSAGE)
    : Exception(message) {
}