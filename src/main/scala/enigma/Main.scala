package enigma

object Main extends App {
  val enigmaMachine = EnigmaMachine.Instance

  val encrypted =
    enigmaMachine.acceptRequest(EncryptionRequest("A", 'B', 'A', 'A'))

//  val encrypted = (0 to 100).map { _ =>
//    enigmaMachine.encrypt('A')
//  }.mkString
//
//  enigmaMachine.setRotorSettings(rotorSettings)
//  val decrypted =
//    "BDZGOWCXLTKSBTMCDLPBMFEBOXYHCXTGYJFLINHNXSHIUNTUQOFXPQPKOVHCBUBTZSZSOOSTGHEORODBBZZLXLCYZXIFGWFDZEEOT"
//      .map(c => enigmaMachine.encrypt(c))
//      .mkString
//
//  println(s"encrypted: $encrypted")
//  println(s"decrypted: $decrypted")
}
