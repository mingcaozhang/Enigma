object EnigmaMachine extends App {

  val leftRotor = new Rotor(RotorConstants.Rotor_I_Sequence, None)
  val centerRotor = new Rotor(RotorConstants.Rotor_II_Sequence, Some(leftRotor))
  val rightRotor =
    new Rotor(RotorConstants.Rotor_III_Sequence, Some(centerRotor))

  val reflector = new Reflector(RotorConstants.B_Reflector_Sequence)

  val enigmaMachine =
    new EnigmaMachine(leftRotor, centerRotor, rightRotor, reflector)

  val encrypted = (0 to 100).map { _ =>
    enigmaMachine.encrypt('A')
  }.mkString

  enigmaMachine.setRotorSettings(RotorSettings('A', 'A', 'A'))
  val decrypted =
    "BDZGOWCXLTKSBTMCDLPBMFEBOXYHCXTGYJFLINHNXSHIUNTUQOFXPQPKOVHCBUBTZSZSOOSTGHEORODBBZZLXLCYZXIFGWFDZEEOT"
      .map(c => enigmaMachine.encrypt(c))
      .mkString

  println(s"encrypted: $encrypted")
  println(s"decrypted: $decrypted")
}
class EnigmaMachine(
  leftRotor: Rotor,
  centerRotor: Rotor,
  rightRotor: Rotor,
  reflector: Reflector,
  rotorSettings: RotorSettings = RotorSettings('A', 'A', 'A')
) {
  setRotorSettings(rotorSettings)

  def encrypt(char: Char): Char = {
    rightRotor.increment()
    rightRotor reverseTransform (
      centerRotor reverseTransform (
        leftRotor reverseTransform (
          reflector reflect (
            leftRotor transform (
              centerRotor transform (
                rightRotor transform char.toUpper
              )
            )
          )
        )
      )
    )
  }

  def setRotorSettings(settings: RotorSettings): Unit = {
    val (left, center, right) = (settings.left, settings.center, settings.right)
    leftRotor.reset()
    centerRotor.reset()
    rightRotor.reset()
    val leftRange = left - 'A'
    val centerRange = center - 'A'
    val rightRange = right - 'A'
    if (leftRange > 0) (0 to leftRange) foreach (_ => leftRotor.increment())
    if (centerRange > 0)
      (0 to centerRange) foreach (_ => centerRotor.increment())
    if (rightRange > 0) (0 to rightRange) foreach (_ => rightRotor.increment())
  }
}

object Constants {
  val ALPHA_COUNT: Int = 26
  val MAX_INDEX: Int = ALPHA_COUNT - 1

}

class Reflector(sequence: Array[Char]) {
  val reflectionTable
    : Array[Connection] = RotorConstants.Fixed_Sequence zip sequence map {
    case (in, out) => Connection(in, out)
  }

  def reflect(c: Char): Char =
    reflectionTable
      .find(_.input == c)
      .map(_.output)
      .getOrElse(
        throw new IllegalArgumentException(s"No output mapped for: $c")
      )
}
case class Connection(input: Char, output: Char)
case class RotorSettings(left: Char, center: Char, right: Char)

object CharUtils {
  implicit class CharExtensions(val c: Char) {
    def prev: Char = {
      val start = 'A'
      val check = c - start - 1
      val prev = if (check == -1) start + 25 else c - 1
      prev.toChar
    }
  }
}
