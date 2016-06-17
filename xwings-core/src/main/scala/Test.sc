import com.google.common.hash.Hasher
import org.apache.commons.codec.binary.Hex
import wotgraph.toolkit.crypt.PBKDF2WithHmacSHA512

val hasher = new PBKDF2WithHmacSHA512

Hex.encodeHexString(hasher.hash("v".toCharArray, "2m0E8".getBytes, 2, 512))


