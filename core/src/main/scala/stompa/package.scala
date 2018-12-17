package object stompa {

  case class StompConfig(host: String, port: Int, username: String, password: String)

  case class Topic(value: String)
}
