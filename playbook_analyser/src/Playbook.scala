package playbook_analyser

import io.circe.yaml.parser
import io.circe.ParsingFailure
import io.circe.Json
import io.circe.DecodingFailure
import sys.process._

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

  def container_images(provider: String): List[String] = {
    content.findAllByKey(provider)
      .flatMap(task => task.findAllByKey("image"))
      .map(_.as[String])
      .filter(_.isRight)
      .map(_.right.get)
  }

  def images: List[String] = List("docker_container", "podman_container", "community.kubernetes.k8s")
    .foldLeft(List[String]()) { (all, provider_name) => all ::: container_images(provider_name) }

  def firewall_tasks: List[Json] = content.findAllByKey("firewalld")

  def open_ports: List[Either[DecodingFailure,Port]] = firewall_tasks.map(task => task.hcursor.downField("port").as[String].map(Port(_)))
}

object Playbook {
  def apply(content: String): Either[ParsingFailure, Playbook] = {
    parser.parse(content).map((parsed_content) => new Playbook(parsed_content))
  }
}

