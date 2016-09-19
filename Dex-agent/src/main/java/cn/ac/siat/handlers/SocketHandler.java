package cn.ac.siat.handlers;


import org.apache.avro.io.Decoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * Created by laiqingquan on 16/9/19.
 */
public class SocketHandler implements Runnable{
    private static Logger LOG = LoggerFactory.getLogger(SocketHandler.class);

    private SocketChannel channel;

    public SocketHandler(SocketChannel channel) {
        this.channel = channel;
    }

    public void run() {
        LOG.debug("{}", this.channel);
        ByteBuffer buffer = ByteBuffer.allocate(500);
        StringBuffer content = new StringBuffer();

        try {
            do {
                buffer.clear();
                channel.read(buffer);

                buffer.flip();

                Charset charset = null;
                try {
                    CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
                    String tmp = decoder.decode(buffer.asReadOnlyBuffer()).toString();
                    content.append(tmp);
                    if ((content.charAt(content.length() - 1) == '\n') && (content.charAt(content.length() - 2) == '\t')) {
                        content.deleteCharAt(content.length() - 1);
                        content.deleteCharAt(content.length() - 1);
                        break;
                    }
                } catch (StringIndexOutOfBoundsException ex) {
                    ex.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            } while (true);


            LOG.debug("accept {} msg = {}", channel.getRemoteAddress(), content);
            this.channel.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
