name := "Dex-server"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
    "org.apache.spark" % "spark-core_2.11" % "2.0.0" % "provided",
    "org.apache.spark" % "spark-mllib_2.11" % "2.0.0",
    "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.0.4",
    "ch.qos.logback" % "logback-classic" % "1.1.2"
)

jarName in assembly := "myJob.jar"

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)


assemblyMergeStrategy in assembly := {
    case PathList(ps @ _*) if ps.last endsWith ".class" => MergeStrategy.first
    case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
    case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
    case "application.conf"                            => MergeStrategy.concat
    case "unwanted.txt"                                => MergeStrategy.discard
    case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
}

    