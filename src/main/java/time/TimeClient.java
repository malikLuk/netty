package time;

/**
 * Created by Lukmanov.MN on 21.08.2018.
 */

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Для Time-сервера сделаем клиент. Проблема тут следующая. В потоковой передаче данных, такой как TCP/IP,
 * полученные данные размещаются в буфере приема сокета. К сожалению, буффер потока - это не очередь пакетов,
 * разделенных между собой, а очередь байтов. Это значит, что, если мы отправим два разных сообщения, операционная
 * система не будет рассматривать их как два отдельных сообщения, а будет считать их одним пучком байтов. Следовательно,
 * нет никакой гарантии, что мы прочитаем именно то, что нужно.
 * Решение номер 1. Создание внутреннего суммирующего буффера и подождать, пока он не заполнится нашими 4-мя байтами -
 * Integer - 32 бита, то есть, целое число. Модификация в TimeClientHandler.
 * */
public class TimeClient {

    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 8585;
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            /**
             * Bootstrap это тоже самое, что и ServerBootstrap, только для не-верверных каналов, а
             * например, как тут, для клиентов.
             * */
            Bootstrap b = new Bootstrap();
            /**
             * Если мы определим только одну группу, то она будет использоваться и как Боссы и как Работники.
             * Боссы не используются для клиентской части
             *
             * */
            b.group(workerGroup);
            /**
             * NioSocketChannel используется для клиентской стороны вместо NioServerSocketChannel
             * */
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new TimeDecoder(), new TimeClientHandler());
                }
            });
            /**
             * Запуск клиента. Здесь мы должны вызвать connect вместо bind
             * */
            ChannelFuture f = b.connect(host, port).sync();

            //wait until the connection is closed
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

}
