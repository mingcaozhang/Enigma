package enigma

import enigma.utils.RotorConstants

object Main extends App {

  val reflector: Reflector = new Reflector(RotorConstants.B_Reflector_Sequence)
  val leftRotor: Rotor = new Rotor(RotorConstants.Rotor_I_Sequence)
  val centerRotor: Rotor = new Rotor(RotorConstants.Rotor_II_Sequence)
  val rightRotor: Rotor = new Rotor(RotorConstants.Rotor_III_Sequence)

  val transformers: List[Transformer] =
    List(rightRotor, centerRotor, leftRotor, reflector)

  Transformers.wireUp(transformers)

  val enigmaMachine =
    new EnigmaMachine(leftRotor, centerRotor, rightRotor, rightRotor, reflector)

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
