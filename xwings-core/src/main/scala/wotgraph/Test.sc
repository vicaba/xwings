trait ATrait[A <: ATrait[_]] {
  val self: A = this.self
}

case class Impl(str: String = "hola") extends ATrait[Impl]

println(Impl().self)

