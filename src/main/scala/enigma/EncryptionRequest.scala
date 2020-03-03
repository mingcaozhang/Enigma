package enigma

final case class EncryptionRequest(text: String,
                                   right: Char,
                                   center: Char,
                                   left: Char)
