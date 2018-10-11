package fix

import metaconfig.Configured
import scala.meta._
import scalafix.v1._

case class LiteralArgument(literal: Lit) extends Diagnostic {
  override def position: Position = literal.pos
  override def message: String =
    s"Use named arguments for literals such as 'parameterName = $literal'"
}

class NoLiteralArguments(config: LiteralArgumentsConfig)
    extends SyntacticRule("NoLiteralArguments") {
  def this() = this(LiteralArgumentsConfig.default)
  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf
      .getOrElse("NoLiteralArguments")(this.config)
      .map(newConfig => new NoLiteralArguments(newConfig))
  }
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree
      .collect {
        case Term.Apply(_, args) =>
          args.collect {
            case t: Lit if config.isDisabled(t) =>
              Patch.lint(LiteralArgument(t))
          }
      }
      .flatten
      .asPatch
  }
}
