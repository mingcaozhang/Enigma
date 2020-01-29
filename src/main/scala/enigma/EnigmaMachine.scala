package enigma

import scala.annotation.tailrec

object EnigmaMachine {
  def apply(
    startRotor: Rotor,
    rotorSettings: RotorSettings = RotorSettings('A', 'A', 'A')
  ): EnigmaMachine = new EnigmaMachine(startRotor, rotorSettings)
}

class EnigmaMachine(
  startRotor: Rotor,
  rotorSettings: RotorSettings = RotorSettings('A', 'A', 'A')
) {
  setRotorSettings(rotorSettings)

  def startLoop(): Unit = {
    while (true) {}
  }

  def encrypt(s: String): String = s map encrypt
  def encrypt(char: Char): Char = {
    startRotor.increment()
    encryptRecursively(char, startRotor, reverse = false)
  }

  @tailrec
  private def encryptRecursively(char: Char,
                                 transformer: Transformer,
                                 reverse: Boolean): Char =
    if (transformer.prev.isEmpty && reverse)
      transformer.reverseTransform(char)
    else if (reverse)
      encryptRecursively(
        transformer.reverseTransform(char),
        transformer.prev.get,
        reverse = true
      )
    else if (transformer.next.isEmpty)
      encryptRecursively(
        transformer.transform(char),
        transformer.prev.get,
        reverse = true
      )
    else
      encryptRecursively(
        transformer.transform(char),
        transformer.next.get,
        reverse = false
      )

  private def resetRecursively(rotor: Rotor, settingsList: Seq[Char]): Unit = {
    rotor.setSetting(settingsList.head)
    rotor.next.foreach {
      case r: Rotor => resetRecursively(r, settingsList.tail)
      case _        => ()
    }
  }

  def setRotorSettings(settings: RotorSettings): Unit =
    resetRecursively(startRotor, settings.list)
}

case class RotorSettings(right: Char, center: Char, left: Char) {
  val list = Seq(right, center, left)
}
