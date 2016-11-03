package dex.base

import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.{SelectionKey, Selector, ServerSocketChannel, SocketChannel}
import java.nio.charset.Charset

import com.typesafe.scalalogging.slf4j.Logger
import org.slf4j.LoggerFactory

/**
  * Created by laiqingquan on 16/10/25.
  */
class SelectorServer(port: Int) {
    val LOG = Logger(LoggerFactory.getLogger("SelectorServer"))

    val serviceChannel = ServerSocketChannel.open()
    serviceChannel.socket.bind(new InetSocketAddress(port))
    serviceChannel.configureBlocking(false)

    def start(): Unit = {
        val selector = Selector.open()

        val charDecoder = Charset.forName("UTF-8").newDecoder()

        serviceChannel.register(selector, SelectionKey.OP_ACCEPT)
        val buffer = ByteBuffer.allocate(1024)
        while (true) {
            if (selector.select() > 0) {
                val keyIterator = selector.selectedKeys().iterator()
                while (keyIterator.hasNext) {
                    val key = keyIterator.next()
                    keyIterator.remove()
                    if (key.isAcceptable) {         //新接受连接
                    val channel = key.channel.asInstanceOf[ServerSocketChannel]
                        val conn = channel.accept
                        LOG.info(conn.getRemoteAddress.toString + " arrive!")
                        conn.configureBlocking(false)
                        conn.register(selector, SelectionKey.OP_READ)
                    } else if (key.isReadable) {    //新可读内容
                    val conn = key.channel.asInstanceOf[SocketChannel]
                        buffer.clear
                        val size = conn.read(buffer)
                        if (size < 0) {             //连接断开or出错
                            LOG.info(conn.getRemoteAddress.toString + " leave!")
                            conn.close()
                            selector.selectedKeys.remove(key)
                        } else {                    //echo收到的内容
                            buffer.flip
                            conn.write(buffer)
                        }
                    }
                }
            }
        }
    }
}
