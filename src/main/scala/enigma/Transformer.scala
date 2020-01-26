package enigma

import enigma.utils.{Mapping, MappingUtils}

trait Transformer {
  protected def sequence: Array[Char]
  protected val defaultMappings: Array[Mapping] =
    MappingUtils.defaultMappings(sequence)

  def next: Option[Transformer]
  def prev: Option[Transformer]

  def transform(c: Char,
                mappingsToUse: Array[Mapping] = defaultMappings): Char =
    mappingsToUse
      .find(_.input == c)
      .map(_.output)
      .getOrElse(
        throw new IllegalArgumentException(s"No output mapped for: $c")
      )
  def reverseTransform(c: Char,
                       mappingsToUse: Array[Mapping] = defaultMappings): Char =
    mappingsToUse
      .find(_.output == c)
      .map(_.input)
      .getOrElse(
        throw new IllegalArgumentException(s"No input mapped from: $c")
      )
}

object Transformers {
  def wireUp(transformers: List[Transformer]): Transformer = {
    val ss = ((None :: transformers.map(Some(_))) :+ None) sliding 3
    ss foreach {
      case List(prev, Some(rotor: Rotor), next) =>
        rotor.prev = prev
        rotor.next = next
      case List(prev, Some(reflector: Reflector), _) =>
        reflector.prev = prev
    }
    transformers.head
  }
}
