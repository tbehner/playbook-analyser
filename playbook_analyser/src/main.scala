package playbook_analyser

import mainargs.{main, arg, ParserForMethods, Flag}

import io.circe.ParsingFailure,io.circe.Json

object Main{
  @main
  def run(@arg(short = 'p', doc = "path to the ansible playbook to analyse") path: String) = {
    PlaybookProducer.from(path)
      .flatMap(p => p.images)
      .foreach(image_name => {
        val report = Trivy.get_report(image_name)
        if (report.isRight) {
          val analyser = CveReportAnalyser(report.right.get)
          if (analyser.has_critical()) {
            println(image_name)
          }
        }
      })
  }
  def main(args: Array[String]): Unit = ParserForMethods(this).runOrExit(args)
}
