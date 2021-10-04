package playbook_analyser

import io.circe.yaml.parser
import io.circe.ParsingFailure
import io.circe.Json
import io.circe.DecodingFailure

object Protocol extends Enumeration {
  type Protocol = Value
  val Tcp = Value("tcp")
  val Udp = Value("udp")
}

import Protocol._

case class Port(number: Integer, prot: Protocol)

object Port {
  def apply(encoded: String): Port = {
    val fields = encoded.split("/")
    new Port(fields(0).toInt, Protocol.withName(fields(1).toLowerCase))
  }
}

class Playbook(content: Json) {
  def task_groups: List[Json] = content.findAllByKey("tasks").toList
  def all_tasks: List[Json] = task_groups.flatMap(task_list => task_list.asArray.get )
  def firewall_tasks: List[Json] = all_tasks.filter(p => !p.findAllByKey("firewalld").isEmpty)
  def open_ports: List[Either[DecodingFailure,Port]] = firewall_tasks.map(task => task.hcursor.downField("firewalld").downField("port").as[String].map(Port(_)))
}

object Playbook {
  def apply(content: String): Either[ParsingFailure, Playbook] = {
    parser.parse(content).map((parsed_content) => new Playbook(parsed_content))
  }
}
