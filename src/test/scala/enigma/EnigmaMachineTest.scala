package enigma

import enigma.utils.RotorConstants.{
  B_Reflector_Sequence,
  Rotor_III_Sequence,
  Rotor_II_Sequence,
  Rotor_I_Sequence
}
import org.scalatest.FlatSpec
class EnigmaMachineTest extends FlatSpec {
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
  val enigmaMachine = EnigmaMachine(startRotor)

  "An Enigma Machine" should "encrypt inputs deterministically given some rotor settings" in {
    val rotorSettings: RotorSettings = RotorSettings('B', 'B', 'B')
    enigmaMachine.setRotorSettings(rotorSettings)
    assert(enigmaMachine.encrypt("AAAAA") == "IGQQK")
  }

  it should "decrypt outputs given the rotor settings for the input" in {
    val rotorSettings: RotorSettings = RotorSettings('B', 'B', 'B')
    enigmaMachine.setRotorSettings(rotorSettings)
    assert(enigmaMachine.encrypt("IGQQK") == "AAAAA")
  }
}
