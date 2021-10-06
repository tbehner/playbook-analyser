package playbook_analyser
import io.circe._, io.circe.parser._
import io.circe.{Json,ParsingFailure}
import sys.process._
import java.io.File
import scala.io.Source

object Trivy {
  def get_report(image_name: String): Either[ParsingFailure,Json] = {
    val dest = File.createTempFile("trivy-",".json")
    s"trivy image --format json --output ${dest.toPath().toString()} $image_name".!
    val parsed_output = parse(Source.fromFile(dest).getLines().mkString)
    parsed_output
  }
}

class CveReportAnalyser(report: Json) {
  def has_critical(): Boolean = {
    report.findAllByKey("Severity").find(p => p.asString.get == "CRITICAL").isDefined
  }
}

object CveReportAnalyser {
  def apply(report: Json): CveReportAnalyser = new CveReportAnalyser(report)
}
