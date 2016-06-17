package wotgraph.toolkit.crypt

import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object Hasher {
  type PrebuiltHash = Array[Char] => String
}

class PBKDF2WithHmacSHA512 {

  lazy val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")

  /**
    * @see  <a href="https://www.owasp.org/index.php/Hashing_Java">https://www.owasp.org/index.php/Hashing_Java</a>
    * @param password
    * @param salt
    * @param iterations
    * @param keyLength
    */
  def hash(
                     password: Array[Char],
                     salt: Array[Byte] = "6n90E".getBytes,
                     iterations: Int = 2,
                     keyLength: Int = 256): Array[Byte] = {

    val spec = new PBEKeySpec(password, salt, iterations, keyLength)

    val key = secretKeyFactory.generateSecret(spec)

    key.getEncoded
  }

}