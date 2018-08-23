/**
 *
 * Пишем клиент. Клиент будет:
 *  - подключаться к серверу
 *  - отправлять сообщения
 *  - для каждого сообщения ждать ответа от сервера
 *  - закрывать соединение
 * Написание клиента включает два тех же самых компонента, что и для сервера: бизнес-логика, настройка и запуск.
 * Для обработки данных клиент также должен наследоваться от производного от ChannelHandler. Здесь мы используем
 * SimpleChannelInboundHandler. В нем нам потребуются три метода:
 *  - channelActive() - вызывается, когда соединение с сервером установлено.
 *  - channelRead0() - вызывается, когда сообщение от сервера получено
 *  - exceptionCaught() - отлов исключений
 * Мы выбрали SimpleChannelInboundHandler, так как он хорошо освобождает ресурсы
 *
 * */

package netty_in_action.chapter2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

@Sharable
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf>{

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        System.out.println("Client received: " + msg.toString(CharsetUtil.UTF_8));
    }

}
