package enigma

import enigma.utils.{Mapping, MappingUtils}

trait Transformer {
  protected def sequence: Array[Char]
  protected val defaultMappings: Array[Mapping] =
    MappingUtils.defaultMappings(sequence)

  private[enigma] def next: Option[Transformer]
  private[enigma] def prev: Option[Transformer]

  private[enigma] def transform(
    c: Char,
    mappingsToUse: Array[Mapping] = defaultMappings
  ): Char =
    mappingsToUse
      .find(_.input == c)
      .map(_.output)
      .getOrElse(
        throw new IllegalArgumentException(s"No output mapped for: $c")
      )

  private[enigma] def reverseTransform(c: Char,
                                       mappingsToUse: Array[Mapping] =
                                         defaultMappings): Char =
    mappingsToUse
      .find(_.output == c)
      .map(_.input)
      .getOrElse(
        throw new IllegalArgumentException(s"No input mapped from: $c")
      )
}

object Transformers {

  /**
    * This method is used to wire up the previous/next transformers, as they are initialized with prev/next = None.
    * @param transformers Must be ordered such that the first element is the entry rotor and the last elemenet is the reflector.
    * @return Returns the head of input.
    */
  def wireUp(transformers: List[Transformer]): Transformer = {
    require(transformers.size == 4)
    ((None :: transformers.map(Some(_))) :+ None) sliding 3 foreach {
      case List(prev, Some(rotor: Rotor), next) =>
        rotor.prev = prev
        rotor.next = next
      case List(prev, Some(reflector: Reflector), _) =>
        reflector.prev = prev
    }
    transformers.head
  }
}
