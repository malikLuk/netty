package discard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * Created by Lukmanov.MN on 21.08.2018.
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {

    /**
     *  Метод вызывается при получении сообщения от клиента
     **/
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        /**
         * Тут мы реализуем так называемый discard-протокол. Discard - отбрасывание, то есть
         * просто игнорим все получаемые сообщения.
         * ByteBuf - это объект, подсчитывающий ссылки, который освобождает ресурсы явно, через метод
         * release().
         * Следует запомнить, что освобождение ресурса, передеанного в хандлер - задача самого хандлера.
         * Обычно channelRead()-метод выглядит, примерно, так
         *  try {
         *      // Do something
         *  } finally {
         *      ReferenceCountUtil.release(msg);
         *  }
         *
         *  Для discard-сервера актуален этот код
         *  ((ByteBuf)msg).release();
         * */
        ByteBuf in = (ByteBuf) msg;
        try {
            /**
             * 1 - Этот не самый эффективный цикл может быть заменен следующим кодом
             * System.out.println(in.toString(io.netty.util.CharsetUtil.US_ASCII))
             * */
            while (in.isReadable()) { // (1)
                System.out.println((char) in.readByte());
                System.out.flush();
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }

    }

    /**
     * Этот метод вызывается, когда возбуждается исключение в Netty. В большинстве случаев,
     * ассоциированный с исключением канал должен быть закрыт именно здесь.
     * */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
