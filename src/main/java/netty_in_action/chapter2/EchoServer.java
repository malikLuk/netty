/**
 *
 * Замутим сам сервер. Он включает в себя:
 *  - Биндинг на порт, который сервер будет слушать
 *  - Конфигурацию Каналов для оповещения EchoServerHandler-инстанса о входящих сообщениях.
 * Здесь мы столкнемся с термином Транспорт. В многоуровнем виде сетевых протоколов есть Транспортный уровень,
 * предназначенныйдля доставки данных. Интернет базируется на TCP-транспортировке. НИО-транспортировка почти идентична
 * TCP-транспортировке, за исключением улучшеной производительности на стороне сервера, обеспечиваемой самой Java NIO.
 *
 * */

package netty_in_action.chapter2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * Created by Lukmanov.MN on 22.08.2018.
 */
public class EchoServer {

    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        new EchoServer(8585).start();
    }

    public void start() throws Exception {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        /**
         * В нашем сервере, так как мы использует НИО-транспортировку, мы
         * определили NioEventLoopGroup для приема и обработки новых подключений.
         * Также определили NioServerSocketChannel в качестве типа канала.
         * Ниже мы определили ChannelInitializer. Когда принято новое подключение,
         * создается новый дочерний Канал(initChannel) и ChannelInitializer добавит объект
         * нашего EchoServerHandler в ChannelPipeline созданного Канала.
         * */
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    .channel(NioServerSocketChannel.class)          // какой Транспортный канал использовать
                    .localAddress(new InetSocketAddress(port))      // локальный(на текущей машине) адрес сокета,
                                                                    // сервер будет слушать этот порт
                    .childHandler(new ChannelInitializer<SocketChannel>() {         // устанавливаем обработчик
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(serverHandler);
                        }
                    });
            ChannelFuture f = b.bind().sync(); // Биндим сервак асинхронно, sync() - ждет завершения биндинга
            f.channel().closeFuture().sync();  // Другими словами, sync() блокирует поток
        } finally {
            group.shutdownGracefully().sync();
        }
    }

}
