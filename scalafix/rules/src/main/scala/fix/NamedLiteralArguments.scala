package fix

import metaconfig.Configured
import scalafix.v1._
import scala.meta._

class NamedLiteralArguments(config: LiteralArgumentsConfig)
    extends SemanticRule("NamedLiteralArguments") {

  def this() = this(LiteralArgumentsConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf
      .getOrElse("NamedLiteralArguments")(this.config)
      .map(newConfig => new NamedLiteralArguments(newConfig))
  }

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree
      .collect {
        case Term.Apply(fun, args) =>
          args.zipWithIndex.collect {
            case (t: Lit, i) if config.isDisabled(t) =>
              fun.symbol.info match {
                case Some(info) =>
                  info.signature match {
                    case method: MethodSignature
                        if method.parameterLists.nonEmpty =>
                      val parameter = method.parameterLists.head(i)
                      val parameterName = parameter.displayName
                      if (method.parameterLists.head.size != 1 || (parameterName != info.displayName && !info.displayName
                            .startsWith("set"))) {
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
}
