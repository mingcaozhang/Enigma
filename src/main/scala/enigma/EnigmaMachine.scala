package enigma

import scala.annotation.tailrec

class EnigmaMachine(
  leftRotor: Rotor,
  centerRotor: Rotor,
  rightRotor: Rotor,
  startRotor: Transformer,
  reflector: Reflector,
  rotorSettings: RotorSettings = RotorSettings('A', 'A', 'A')
) {
  setRotorSettings(rotorSettings)
  reflector.prev = Some(leftRotor)
  leftRotor.prev = Some(centerRotor)
  centerRotor.prev = Some(rightRotor)
  rightRotor.next = Some(centerRotor)
  centerRotor.next = Some(leftRotor)
  leftRotor.next = Some(reflector)

  def encrypt(char: Char): Char = {
    startRotor.asInstanceOf[Rotor].increment()
    encryptRecursively(char, startRotor, false)
  }

  @tailrec private def encryptRecursively(char: Char,
                                          transformer: Transformer,
                                          reverse: Boolean): Char = {
    if (transformer.prev.isEmpty && reverse)
      transformer.reverseTransform(char)
    else if (transformer.next.isEmpty && !reverse)
      encryptRecursively(
        transformer.transform(char),
        transformer.prev.get,
        true
      )
    else if (reverse)
      encryptRecursively(
        transformer.reverseTransform(char),
        transformer.prev.get,
        true
      )
    else
      encryptRecursively(
        transformer.transform(char),
        transformer.next.get,
        false
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

case class RotorSettings(left: Char, center: Char, right: Char)
