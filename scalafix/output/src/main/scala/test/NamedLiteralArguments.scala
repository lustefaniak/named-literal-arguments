package test

object NamedLiteralArguments {
  def complete(isSuccess: Boolean): Unit       = ()
  def finish(n: Int, isError: Boolean): Unit   = ()
  def setFoo(bar: Boolean): Unit               = {}
  def version(version: Int): Unit              = {}
  def setBar(foo: Boolean, bar: Boolean): Unit = {}
  def print(str: String): Unit                 = {}
  def withReadOnly(readOnly: Boolean): Unit    = {}
  complete(isSuccess = true)
  complete(isSuccess = true)
  complete(isSuccess = false)
  finish(n = 2, isError = true)
  setFoo(false)
  version(123)
  setBar(foo = false, bar = true)
  print("foo")
  withReadOnly(false)

}
