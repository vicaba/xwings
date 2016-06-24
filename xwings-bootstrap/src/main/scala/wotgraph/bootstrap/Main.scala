package wotgraph.bootstrap


object Main {

  def main(args: Array[String]) {

    import scala.io.StdIn._

    val program = readLine("What program do you want to run?")

    program match {
      case "bootstrap" => Bootstrap()
    }
  }

}
