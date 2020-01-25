import CharUtils.CharExtensions

class Rotor(sequence: Array[Char], nextRotor: Option[Rotor]) {
  private val Default_Connections
    : Array[Connection] = RotorConstants.Fixed_Sequence zip sequence map {
    case (fixed, rotor) => Connection(fixed, rotor)
  }
  private val Default_Position = 0

  private var connections: Array[Connection] = Default_Connections
  private var position: Int = Default_Position
  private var previousPosition: Int = Default_Position

  def reset(): Unit = {
    connections = Default_Connections
    position = Default_Position
    previousPosition = Default_Position
  }

  def increment(): Unit = {
    position = (position + 1) % Constants.ALPHA_COUNT
    connections = connections.map {
      case Connection(in, out) => Connection(in.prev, out.prev)
    }
    if (position == 0 && previousPosition == Constants.MAX_INDEX)
      nextRotor foreach (_.increment())
    previousPosition = (previousPosition + 1) % Constants.ALPHA_COUNT
  }

  def transform(c: Char): Char =
    connections
      .find(_.input == c)
      .map(_.output)
      .getOrElse(
        throw new IllegalArgumentException(s"No output mapped to input: $c")
      )

  def reverseTransform(c: Char): Char =
    connections
      .find(_.output == c)
      .map(_.input)
      .getOrElse(
        throw new IllegalArgumentException(s"No input mapped from output: $c")
      )

  def positionAsChar: Char = (position + 'A').toChar
}

object RotorConstants {
  val Default_Position = 0
  val Fixed_Sequence: Array[Char] =
    Array('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
      'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z')
  val Rotor_I_Sequence: Array[Char] =
    Array('E', 'K', 'M', 'F', 'L', 'G', 'D', 'Q', 'V', 'Z', 'N', 'T', 'O', 'W',
      'Y', 'H', 'X', 'U', 'S', 'P', 'A', 'I', 'B', 'R', 'C', 'J')
  val Rotor_II_Sequence: Array[Char] =
    Array('A', 'J', 'D', 'K', 'S', 'I', 'R', 'U', 'X', 'B', 'L', 'H', 'W', 'T',
      'M', 'C', 'Q', 'G', 'Z', 'N', 'P', 'Y', 'F', 'V', 'O', 'E')
  val Rotor_III_Sequence: Array[Char] =
    Array('B', 'D', 'F', 'H', 'J', 'L', 'C', 'P', 'R', 'T', 'X', 'V', 'Z', 'N',
      'Y', 'E', 'I', 'W', 'G', 'A', 'K', 'M', 'U', 'S', 'Q', 'O')
  val B_Reflector_Sequence: Array[Char] =
    Array('Y', 'R', 'U', 'H', 'Q', 'S', 'L', 'D', 'P', 'X', 'N', 'G', 'O', 'K',
      'M', 'I', 'E', 'B', 'F', 'Z', 'C', 'W', 'V', 'J', 'A', 'T')
}

object ConnectionUtils {
  def defaultConnections(sequence: Array[Char]): Array[Connection] = {
    RotorConstants.Fixed_Sequence zip sequence map {
      case (fixed, rotor) => Connection(fixed, rotor)
    }
  }
}
