package constants

object DBHistoryConstants extends Enumeration {
  val Creation = Value("CREATE")
  val Update = Value("UPDATE")
  val Delete = Value("DELETE")
}