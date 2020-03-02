package enigma

import scala.annotation.tailrec
import enigma.utils.RotorConstants._

object EnigmaMachine {
  val Instance = {
    val transformers =
      List(
        Rotor(Rotor_III_Sequence),
        Rotor(Rotor_II_Sequence),
        Rotor(Rotor_I_Sequence),
        Reflector(B_Reflector_Sequence)
      )
    val startRotor: Rotor = Transformers.wireUp(transformers) match {
      case r: Rotor => r
      case other    => throw new IllegalStateException(s"Expected rotor, got $other")
    }
    EnigmaMachine(startRotor)
  }

  private def apply(
    startRotor: Rotor,
    rotorSettings: RotorSettings = RotorSettings('A', 'A', 'A')
  ): EnigmaMachine = new EnigmaMachine(startRotor, rotorSettings)
}

class EnigmaMachine private(
  startRotor: Rotor,
  rotorSettings: RotorSettings = RotorSettings('A', 'A', 'A')
) {
  setRotorSettings(rotorSettings)

  def acceptRequest(request: EncryptionRequest): String = {
    setRotorSettings(RotorSettings(request.right, request.center, request.left))
    encrypt(request.text.toUpperCase)
  }

  private def encrypt(s: String): String = s map encrypt
  private def encrypt(char: Char): Char = {
    if (char.isWhitespace) {
      char
    }
    else {
      startRotor.increment()
      encryptRecursively(char, startRotor, reverse = false)
    }
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

  private def setRotorSettings(settings: RotorSettings): Unit =
    resetRecursively(startRotor, settings.list)
}

case class RotorSettings(right: Char, center: Char, left: Char) {
  val list = Seq(right, center, left)
}
