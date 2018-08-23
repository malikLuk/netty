/**
 *
 * Как правило Нетти-сервер требует:
 *  - по крайней мере одного ChannelHandler'а. ChannelHandler реализует обработку сервером
 * данных, полученных от клиента.
 *  - Bootstrapping - код запуска и настройки сервера. Как минимум он привязывает серер к порту, по которому будут
 * слушаться входящие запросы на подключение.
 * Так как именно реализации ChannelHandler'а получают уведомления о наступлении событий и реагируют на них, то наш сервер
 * должен реализовывать или его или один из его потомков, в нам случае ChannelInboundHandlerAdapter, в котором определны
 * методы для реагирования на поступающие события.
 * Нас интересуют следующие методы:
 *  - channelRead() - вызывается для каждого входящего сообщения
 *  - channelReadComplete() - оповещает обработчик, что последний вызов channelRead() был последним для текущей партии
 * сообщений
 *  - exceptionCaught() - ловит исключения
 *  В обработчике, как правило, заключена вся бизнес логика.
 *
 * */

package netty_in_action.chapter2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * Sharable указывает, что наш хандлер можено безопасно использовать с несколькими каналами
 * */
@Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        System.out.println("Server received: " + in.toString(CharsetUtil.UTF_8));
        // Записываем полученное сообщение без его оправки
        ctx.write(in);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        /**
         * Отправка сообщения пиру и закрытие канала
         * */
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        /**
         * Если что-то пошло не так, закрываем канал руками.
         * */
        cause.printStackTrace();
        ctx.close();
    }
}
