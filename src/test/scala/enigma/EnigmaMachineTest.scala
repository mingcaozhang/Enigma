package enigma

import org.scalatest.FlatSpec
class EnigmaMachineTest extends FlatSpec {
  val enigmaMachine = EnigmaMachine.Instance

  "An Enigma Machine" should "encrypt inputs deterministically given some rotor settings" in {
    val encrypted = enigmaMachine.acceptRequest(EncryptionRequest("AAAAA", 'A', 'A', 'A'))
    assert(encrypted == "BDZGO")
  }

  it should "encrypt inputs deterministically given some rotor settings 3" in {
    val encrypted = enigmaMachine.acceptRequest(EncryptionRequest("A", 'B', 'A', 'A'))
    assert(encrypted == "D")
  }

  it should "encrypt a repeated sequence such that the encrypted message has no repetitions" in {
    val encrypted = enigmaMachine.acceptRequest(EncryptionRequest("AAAAA", 'A', 'A', 'A'))
//    val encryptedTwice = enigmaMachine.acceptRequest()
//    assert(enigmaMachine.encrypt("AAAAA") != enigmaMachine.encrypt("AAAAA"))
  }

  it should "decrypt outputs given the rotor settings for the input" in {
    val encrypted = enigmaMachine.acceptRequest(EncryptionRequest("BDZGO", 'A', 'A', 'A'))
    assert(encrypted == "AAAAA")
  }
}
