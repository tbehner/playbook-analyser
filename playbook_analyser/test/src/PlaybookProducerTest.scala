
package playbook_analyser

import utest._
import scala.io.Source
import scala.sys.process._


object PlaybookProducerTests extends TestSuite{
  val number_of_playbooks: Int = ("fd yml" #| "wc -l").!!.strip.toInt
  def test_path(p: os.BasePath) = {
      val playbook_sequence: Seq[Playbook] = PlaybookProducer.from(p.toString)
      assert(playbook_sequence.length == number_of_playbooks )
  }

  val tests = Tests{
    test("finds all playbooks in test directory") {
      test_path(os.pwd / "playbook_analyser" / "test" / "resources")
    }

    test("finds all playbooks in relative test directory") {
      test_path(os.RelPath("playbook_analyser") / "test" / "resources")
    }

    test("finds all playbooks in relative test directory with ..") {
        test_path(os.RelPath("..") / "playbook-analyser" / "playbook_analyser" / "test" / "resources")
    }



  }

}
