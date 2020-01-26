package enigma.utils

object MappingUtils {
  def defaultMappings(sequence: Array[Char]): Array[Mapping] = {
    RotorConstants.Fixed_Sequence zip sequence map {
      case (fixed, variable) => Mapping(fixed, variable)
    }
  }
}

case class Mapping(input: Char, output: Char)
