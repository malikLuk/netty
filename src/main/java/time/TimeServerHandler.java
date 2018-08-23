package time;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by Lukmanov.MN on 21.08.2018.
 */

/**
 * Здесь Time Server. Он отправляет сообщение, содержащее 32-битный Integer, без получения каких-либо
 * запросов и закрывает соединение после отправки сообщения.
 * Так как мы игнорируем получаемые данные, но отправляем сообщение в тот момент, когда соединение установлено -
 * мы не можем сипользовать channelRead() метод. Вместо этого мы должны использовать channelActive().
 * */
public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * channelActive будет вызван, когда соединение установлено и готово генерировать траффик.
     * Запишем число, которое показывает текущее время.
     * */
    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        /**
         * Для отправки нового сообщения, нам нужно выделить(allocate) новый буффер, который будет
         * содержать новое сообщение. Мы создаем ByteBuf емкостью 4 байта(32 бит int).
         * Кстати, для ByteBuffer'а можно не использовать flip() (чтение <-> запись), так как он содержит два
         * указателя: один для чтения, второй для записи. Индекс записи увеличивается, когда мы записываем
         * что-то в буффер, до тех пор, пока индекс чтения не изменится. Индексы чтения и записи показывают,
         * где сообщение начинается и заканчивается соответственно.
         * */
        final ByteBuf time = ctx.alloc().buffer(4);
        time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));

        /**
         * Записываем сконструированное сообщение.
         * ChannelFuture - предстваляет собой операцию ввода-вывод, которая еще не произошла. Это
         * означает, что любая запрошенная операция, возможно, еще не была выполнена, поскольку в Netty
         * все поерации асинхронны.
         * То есть, такой код может закрыть соединение до того, как сообщение будет отправлено.
         *  Channel ch = ....;
         *  ch.writeAndFlush(message);
         *  ch.close();
         * Селдовательно, мы должны закрывать канал после выполнения ChannelFuture, в коллбэке
         * operationComplete(ChannelFuture future). Нужно иметь ввиду, что close() может не закрыть
         * канал немедленно.
         * Коллбэк из addListener вызывается по завершении операции.
         * Альтернатива - f.addListener(ChannelFutureListener.CLOSE);
         * */
        final ChannelFuture f = ctx.writeAndFlush(time);
        f.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                assert f == future;
                ctx.close();
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
