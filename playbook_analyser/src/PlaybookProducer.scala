package playbook_analyser

import os.Path
import io.circe._, io.circe.parser._

object PlaybookProducer {
  def from(path: String): Seq[Playbook] = {
    os.walk(os.FilePath(path).resolveFrom(os.pwd))
      .filter(p => p.ext == "yml")
      .map(p => Playbook(scala.io.Source.fromFile(p.toString).mkString))
      .filter(_.isRight)
      .map(_.right.get)
  }
}
