package discard; /**
 * Created by Lukmanov.MN on 21.08.2018.
 */

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Теперь сам сервер
 * */
public class DiscardServer {

    private int port;

    public DiscardServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        /**
         * NioEventLoopGroup - это многопоточный цикл событий, которая управляет операциями ввода-вывода.
         * Netty предоставляет множетсво реализаций EventLoopGroup для разных видов транспортировки. Здесь
         * мы реализуем сервер, поэтому используем два NioEventLoopGroup. Первый (bossGroup) - принимает
         * входящие соединения. Второй (workerGroup) - обрабатывает траффик прнятых подключений, когда Босс
         * принимает соединение, то регистрирует его у работника. Количество потоков и как они будут соотноситься
         * с Каналами зависит от реализации EventLoopGroup и может быть задано в конструкторе.
         * */
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            /**
             * Channel - абстракция над связью с сетевым сокетом или компонентом, который может выполнять
             * операции чтения, записи, соединения и привязки. Channel предоставляет пользователю следующую
             * информацию:
             *  - состояние Канала (открыт, законекчен и т д)
             *  - параметры конфигурации (например, размер буффера)
             *  - операции ввода-вывода, которые канал поддерживает
             *  - объект ChannelPipeline, который управляет всеми событиями ввода-вывода и запросами,
             *      ассоциированными с каналами.
             * ServerBootstrap - это вспомогательный класс, который устанавливает сервер. Можно установить сервер
             * используя Channel напрямую
             * */
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    /**
                     * Здесь мы используем NioServerSocketChannel, который нужен для создания нового Канала
                     * для принятия входящего подключения.
                     * */
                .channel(NioServerSocketChannel.class)
                    /**
                     * ChannelInitializer - это специальный обработчик, цель которого помочь пользователю
                     * сконфигурировать новый Канал
                     * */
                .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new DiscardServerHandler());
                }
                /**
                 * Так как мы пишем TCP/IP сервер, то мы должны указать параметры соединения,
                 * сколько жить Каналу и т д.
                 * Также, стоит заметить, что у нас есть option() и childOption()
                 * option() относится к NioServerSocketChannel и принимает входящие подключения
                 * childOptions() относится к Channel(Каналам), принятым как раз NioServerSocketChannel
                 * */
            }).option(ChannelOption.SO_BACKLOG, 128)
              .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8585;
        }
        new DiscardServer(port).run();
    }

}
