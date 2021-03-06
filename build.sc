import mill._, scalalib._

object playbook_analyser extends ScalaModule{
  def scalaVersion = "2.13.3"

  def ivyDeps = Agg(
    ivy"io.circe::circe-parser::0.14.1",
    ivy"io.circe::circe-yaml::0.14.1",
    ivy"com.lihaoyi::os-lib:0.7.8",
    ivy"com.lihaoyi::mainargs:0.2.1"
    )

  object test extends Tests {
    def ivyDeps = Agg(ivy"com.lihaoyi::utest::0.7.10")
    def testFramework = "utest.runner.Framework"
  }
}
