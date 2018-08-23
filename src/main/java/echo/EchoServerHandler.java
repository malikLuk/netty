package echo;

/**
 * Created by Lukmanov.MN on 21.08.2018.
 */

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Если discard-сервер не возврщал ничего, то echo-сервер будет нам отвечать.
 * А так, все тоже самое.
 * */
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * Здесь, едиснвтенное отличие от discard в том, что ответ будет писаться не в консоль,
     * а будет возвращаться клиенту.
     * ChannelHandlerContext предоставляет различные операции, которые позволяют запускать
     * события и операции ввода-вывода. Здесь мы записываем сообщение. В данном случае, нет
     * необходимости освобождать ресурс(сообщение). После write, Netty сделает это сама.
     * Однако ctx.write(msg); не пишет сообщение в ответ, оно лишь записывает его во внутренний
     * буфер. А вот уже ctx.flush(); отправляет записанное сообщение из буфера в ответ.
     * Кстати, можно заменить на ctx.writeAndFlush(msg);
     * */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ctx.write(msg);
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
