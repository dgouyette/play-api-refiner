def handle(a: Any): Unit = a match {
  case vs: List[String] => println("strings: " + vs.map(_.size).sum)
  case vs: List[Int]    => println("ints: " + vs.sum)
  case _ =>
}

handle(List("hello", "world")) // output: "strings: 10"
handle(List(1, 2, 3))          // ClassCastException... oh no!