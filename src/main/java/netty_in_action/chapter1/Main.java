/**
 *
 * Базовые компоненты Netty:
 *      - Channel - это базовая конструкция Java NIO. Представляет собой открытое соединение с сущностью, такой как
 * какое-либо хард-устройство, файл, сетевой сокет, программный компонент, который способен выполнять операции ввода-вывода.
 * О канале можно думать как о транспортировщике данных(входящих и исходящих). Он может быть открытым/закрытым, подключенным
 * или отключенным
 *      - Callback - это просто метод, ссылка на который предоставлена другому методу. Это позволяет внешнему методу
 * вызывать коллбэк в нужный ему момент. В частности, с помощью коллбэка можно уведомить о завершении какой-либо операции.
 * Когда коллбэк срабатывает, событие может обрабатываться реализацией интерфейса ChannelHandler.
 *      - Future - предоставляет другой способ оповещения приложения о завершении операции. Объект Future работает как
 * хранилище для результата выполнения асинхронных операций. Это, своего рода, ссылка на будущий результат, когда операция
 * завершится и будет предоставлен доступ к результату. Future в Netty отличается от Future в JDK, так как реализация
 * интерфейса Future в JDK дает нам возможность только вручную проверить, завершна ли операция. Нетти же предоставляет
 * собственную реализацию - ChannelFuture, которая предоставляет дополнительные методы, позволяющие нам регистрировать
 * объекты-слушатели ChannelFutureListener. У этих слушателей есть коллбэк-метод operationComplete(), который вызывается
 * по завершении операции. То есть нам не надо чекать руками, когда завершится операция. В Нетти, каждая исходящая операция
 * ввода-вывода возвращает ChannelFuture; то есть они не блокируют друг друга.
 *      - Events and handlers
 *
 * */


package netty_in_action.chapter1;

public class Main {
}
