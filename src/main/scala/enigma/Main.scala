package enigma

import enigma.utils.RotorConstants._

object Main extends App {
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
  val rotorSettings: RotorSettings = RotorSettings('B', 'B', 'B')
  val enigmaMachine = new EnigmaMachine(startRotor, rotorSettings)

  val encrypted = (0 to 100).map { _ =>
    enigmaMachine.encrypt('A')
  }.mkString

  enigmaMachine.setRotorSettings(rotorSettings)
  val decrypted =
//    "BDZGOWCXLTKSBTMCDLPBMFEBOXYHCXTGYJFLINHNXSHIUNTUQOFXPQPKOVHCBUBTZSZSOOSTGHEORODBBZZLXLCYZXIFGWFDZEEOT"
    "IGQQKOMBOYOUVGDHTCORRIENHHDCOVQZBVBBFSPQQONXTEEWKHTCKNSTUMEOWLZJJVNGYIBYBDUQSQPEUGJRCXZWPFYIYYBWLJLQL"
      .map(c => enigmaMachine.encrypt(c))
      .mkString

  println(s"encrypted: $encrypted")
  println(s"decrypted: $decrypted")
}
