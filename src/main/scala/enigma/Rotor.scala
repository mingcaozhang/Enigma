package enigma

import enigma.utils.CharUtils.CharExtensions
import enigma.utils.{Constants, Mapping}

object Rotor {
  def apply(sequence: Array[Char]) = new Rotor(sequence)
}

class Rotor(protected val sequence: Array[Char]) extends Transformer {
  private var rotatingMappings: Array[Mapping] = defaultMappings
  private var position: Int = Constants.DEFAULT_POSITION
  private var previousPosition: Int = Constants.DEFAULT_POSITION

  private[enigma] var next: Option[Transformer] = None
  private[enigma] var prev: Option[Transformer] = None

  def setSetting(setting: Char): Unit = {
    val offset = setting - 'A'
    rotatingMappings = defaultMappings
    position = Constants.DEFAULT_POSITION
    previousPosition = Constants.DEFAULT_POSITION
    if (offset > 0) for (_ <- 0 until offset) increment
  }

  def increment(): Unit = {
    position = (position + 1) % Constants.ALPHA_COUNT
    rotatingMappings = rotatingMappings.map {
      case Mapping(in, out) => Mapping(in.prev, out.prev)
    }
    if (position == 0 && previousPosition == Constants.MAX_INDEX)
      next foreach { case rotor: Rotor => rotor.increment() }
    previousPosition = (previousPosition + 1) % Constants.ALPHA_COUNT
  }

  private[enigma] override def transform(c: Char,
                                         mappingsToUse: Array[Mapping]): Char =
    super.transform(c, rotatingMappings)
  private[enigma] override def reverseTransform(
    c: Char,
    mappingsToUse: Array[Mapping]
  ): Char =
    super.reverseTransform(c, rotatingMappings)
}
