package playbook_analyser

import utest._
import scala.io.Source

object HelloTests extends TestSuite{
  def assertNumberOfPorts(content: String, ports: Integer) = {
    val result = Playbook(content)
    assert(result.isRight)
    result.foreach((p: Playbook) => {assert(p.open_ports == ports)})
  }

  val tests = Tests{

    test("can create Playbook") {
      val content = Source.fromURL(getClass.getResource("/no_ports.yml"))
      Playbook(content.mkString).foreach( playbook =>  {
        assert(playbook.task_groups.length == 2)
      })
    }

    test("find all tasks") {
      val content = Source.fromURL(getClass.getResource("/no_ports.yml"))
      Playbook(content.mkString).foreach( playbook =>  {
        assert(playbook.all_tasks.length == 4)
      })
    }

    test("find all firewall tasks when none is defined") {
      val content = Source.fromURL(getClass.getResource("/no_ports.yml"))
      Playbook(content.mkString).foreach( playbook =>  {
        assert(playbook.firewall_tasks.length == 0)
      })
    }

    test("find all firewall tasks when one is defined") {
      val content = Source.fromURL(getClass.getResource("/single_port.yml"))
      Playbook(content.mkString).foreach( playbook =>  {
        val ports = playbook.open_ports
        assert(ports.length == 1)
        assert(ports(0).map(p => p.number == 80 && p.prot == Protocol.Tcp).getOrElse(false))
      })
    }

    test("parse port from formatted port") {
      val p = Port("80/tcp")
      assert(p.number == 80 && p.prot == Protocol.Tcp)
    }

  }
}
