/**
 *
 * Устанвока клиента схожа с установкой сервера. Разница лишь в том, что сервер биндится слушать порт, а клиент использует
 * хост и порт для подключения к серверу.
 * Здесть Bootstrap используется для инициализации клиента.
 * Создаем объект NioEventLoopGroup для обработки событий. Он же создает новые соединения и обрабатывает входящие.
 * InetSocketAddress - нужен для подключения к серверу
 * Наш EchoClientHandler будет использован, когда подключение уже будет установлено.
 *
 * */

package netty_in_action.chapter2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class EchoClient {

    private final String host;
    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            /**
             * Подключение к серверу (Bootstrap.connect()). sync() - ждать, пока не подключится
             * */
            ChannelFuture f = b.connect().sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws InterruptedException {
//        String host = args[0];
//        int port = Integer.parseInt(args[1]);
        new EchoClient("localhost", 8585).start();
    }

}
