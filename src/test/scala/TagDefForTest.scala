import org.scalatest.Tag

object DbTestTag extends Tag("com.yoox.tags.DbTest")
object ApiTestTag extends Tag("com.yoox.tags.ApiTest")
object LinuxTestTag extends Tag("com.yoox.tags.LinuxTest")
object ToolsTestTag extends Tag("com.yoox.tags.ToolsTest")
object GlobalSettings {
  val doAllTests = true
  val integTest = false // more or less than 45 seconds
  val longTest = false // more or less than 45 seconds
  val runOracleTests = false // more or less than 45 seconds
  val obsolete = false // more or less than 45 seconds
}