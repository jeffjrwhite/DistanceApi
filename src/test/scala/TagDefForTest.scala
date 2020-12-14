import org.scalatest.Tag

object DbTestTag extends Tag("com.none2clever.tags.DbTest")
object ApiTestTag extends Tag("com.none2clever.tags.ApiTest")
object LinuxTestTag extends Tag("com.none2clever.tags.LinuxTest")
object ToolsTestTag extends Tag("com.none2clever.tags.ToolsTest")
object GlobalSettings {
  val doAllTests = true
  val longTest = false
  val obsolete = false
}