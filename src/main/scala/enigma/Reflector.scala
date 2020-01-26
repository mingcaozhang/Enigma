package enigma

object Reflector {
  def apply(sequence: Array[Char]): Reflector = new Reflector(sequence)
}

class Reflector(protected val sequence: Array[Char]) extends Transformer {
  private[enigma] val next: Option[Transformer] = None
  private[enigma] var prev: Option[Transformer] = None
}
