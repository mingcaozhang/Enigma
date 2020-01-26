package enigma.utils

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
