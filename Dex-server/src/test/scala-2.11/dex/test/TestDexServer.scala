package dex.test

import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import java.nio.charset.Charset

/**
  * Created by laiqingquan on 16/10/25.
  */
object TestDexServer extends App{
    override def main(args: Array[String]) {
        val host = "localhost"
        val port = 9999
        val channel = SocketChannel.open()
        val buffer = ByteBuffer.allocate(1024)
        val charDecoder = Charset.forName("UTF-8").newDecoder()

        channel.connect(new InetSocketAddress(host, port))
        buffer.put("this is a test".getBytes())
        buffer.flip
        channel.write(buffer)
        buffer.clear
        channel.read(buffer)
        buffer.flip
        println(charDecoder.decode(buffer.asReadOnlyBuffer()).toString)
        channel.close()
    }

}
