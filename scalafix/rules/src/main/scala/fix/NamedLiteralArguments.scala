package fix

import metaconfig.Configured
import scalafix.v1._

import scala.meta._

case class NamedLiteralArgumentsConfig(
    checkedLiterals: List[String] = List("Boolean"),
    buildersPrefix: List[String] = List("set", "with")
) {
  def isChecked(lit: Lit): Boolean = {
    val kind = lit.productPrefix.stripPrefix("Lit.")
    checkedLiterals.contains(kind)
  }
}

object NamedLiteralArgumentsConfig {
  val default = NamedLiteralArgumentsConfig()
  implicit val surface =
    metaconfig.generic.deriveSurface[NamedLiteralArgumentsConfig]
  implicit val decoder =
    metaconfig.generic.deriveDecoder(default)
}

class NamedLiteralArguments(config: NamedLiteralArgumentsConfig) extends SemanticRule("NamedLiteralArguments") {

  def this() = this(NamedLiteralArgumentsConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] =
    config.conf
      .getOrElse("NamedLiteralArguments")(this.config)
      .map(newConfig => new NamedLiteralArguments(newConfig))

  private[this] def isSetterOrBuilder(info: SymbolInformation): Boolean =
    config.buildersPrefix.exists { prefix =>
      info.displayName.startsWith(prefix)
    }

  override def fix(implicit doc: SemanticDocument): Patch =
    doc.tree
      .collect {
        case Term.Apply(fun, args) =>
          args.zipWithIndex.collect {
            case (t: Lit, i) if config.isChecked(t) =>
              fun.symbol.info match {
                case Some(info) =>
                  info.signature match {
                    case method: MethodSignature if method.parameterLists.nonEmpty =>
                      val parameter     = method.parameterLists.head(i)
                      val parameterName = parameter.displayName
                      if (method.parameterLists.head.size != 1 || (parameterName != info.displayName && !isSetterOrBuilder(info))) {
                        Patch.addLeft(t, s"$parameterName = ")
                      } else {
                        Patch.empty
                      }
                    case _ =>
                      // Do nothing, the symbol is not a method
                      Patch.empty
                  }
                case None =>
                  // Do nothing, we don't have information about this symbol.
                  Patch.empty
              }
          }
      }
      .flatten
      .asPatch
}
