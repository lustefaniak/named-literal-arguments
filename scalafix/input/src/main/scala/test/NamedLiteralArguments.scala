/*
rule = NamedLiteralArguments
NamedLiteralArguments.disabledLiterals = [
  Int
  Boolean
]
 */
package test

object NamedLiteralArguments {
  def complete(isSuccess: Boolean): Unit = ()
  def finish(n: Int, isError: Boolean): Unit = ()
  def setFoo(bar: Boolean): Unit = {}
  def version(version: Int): Unit = {}
  def setBar(foo: Boolean, bar: Boolean): Unit = {}
  def print(str:String):Unit = {}
  complete(true)
  complete(isSuccess = true)
  complete(false)
  finish(2, true)
  setFoo(false)
  version(123)
  setBar(false, true)
  print("foo")
}
