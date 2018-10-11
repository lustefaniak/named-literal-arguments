package fix

import scala.meta.Lit

case class LiteralArgumentsConfig(
    disabledLiterals: List[String] = List("Boolean")
) {
  def isDisabled(lit: Lit): Boolean = {
    val kind = lit.productPrefix.stripPrefix("Lit.")
    disabledLiterals.contains(kind)
  }
}

object LiteralArgumentsConfig {
  val default = LiteralArgumentsConfig()
  implicit val surface =
    metaconfig.generic.deriveSurface[LiteralArgumentsConfig]
  implicit val decoder =
    metaconfig.generic.deriveDecoder(default)
}
