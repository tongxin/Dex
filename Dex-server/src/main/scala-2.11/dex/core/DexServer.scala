package dex.core

import java.io.{BufferedReader, PrintStream}
import java.net.Socket

import dex.base.SelectorServer
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by laiqingquan on 16/10/25.
  */
object DexServer extends App {
    case class User(conn:Socket, is:BufferedReader, ps:PrintStream)

    //    class HandleActor(port: ServerActor) extends Actor {
    //        val LOG = Logging(context.system, this)
    //        override def receive: Actor.Receive = {
    //            case "Start" => {
    //                println("Start!")
    //                val service = new ServerSocket(port)
    //                while(true) {
    //
    //                    val conn = service.accept()
    //                    val is = new BufferedReader(new InputStreamReader(conn.getInputStream))
    //                    val os = new PrintStream(conn.getOutputStream)
    //                    sender ! new User(conn, is, os)
    //                }
    //            }
    //            case _ => {
    //                LOG.error("Unknowable Command!")
    //            }
    //        }
    //    }

    //        override def receive: Actor.Receive = {
    //            case "Start" => {
    //                println("Start!")
    //                val service = new ServerSocket(port)
    //                service.getChannel.configureBlocking(false)
    //                while(true) {
    //                    val conn = service.accept()
    //                    val is = new BufferedReader(new InputStreamReader(conn.getInputStream))
    //                    val os = new PrintStream(conn.getOutputStream)
    //                    sender ! new User(conn, is, os)
    //                }
    //            }
    //            case _ => {
    //                LOG.error("Unknowable Command!")
    //            }
    //        }
    //    }

    override def main(args: Array[String]) {
        val conf = new SparkConf().setAppName("naive_bayes")
        val sc = new SparkContext(conf)
        val server = new SelectorServer(9999)

        server.start()
    }
}

