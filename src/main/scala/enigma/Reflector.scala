package enigma

class Reflector(protected val sequence: Array[Char]) extends Transformer {
  val next: Option[Transformer] = None
  var prev: Option[Transformer] = None
}
