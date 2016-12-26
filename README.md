--同步异步概念
同步：多个任务或者事件的执行必须逐个进行，一个事件或者任务的执行会导致整个流程的等待
	体现在线程竞争资源上是必须一个一个线程访问竞争资源

异步：多个任务或者事件可并发执行，一个事件或者任务的执行不会导致整个流程的等待
	体现在线程竞争资源上是可同时访问竞争资源

keyword:竞争资源；多任务


	
--阻塞非阻塞概念
阻塞：存在某个任务事件中，它发出一个请求后由于条件不满足，则会进行等待，直到条件满足再返回

非阻塞：它发出一个请求，不管条件满不满足都会立即返回一个结果

keyword:阻塞条件



--完整的IO读请求
1.查看数据是否可读
2.内核将数据拷贝到用户线程



--同步IO/异步IO
　同步IO：当用户发出IO请求操作之后，如果数据没有就绪，需要通过用户线程或者内核不断地去轮询数据是否就绪，当数据就绪时，再将数据从内核拷贝到用户线程
　异步IO：只有IO请求操作的发出是由用户线程来进行的，IO操作的两个阶段都是由内核自动完成，然后发送通知告知用户线程IO操作已经完成。也就是说在异步IO中，不会对用户线程产生任何阻塞



--阻塞IO/非阻塞IO
  都是反应在读操作的第一个阶段


--IO原型
BIO-线程池模式、NIO-Reactor模式（读写异步+事件注册+轮询事件）、AIO-Proactor模式

------------------------NIO浅析------------------------

1）SelectionKey事件:int
OP_READ(1)/OP_ACCEPT(16)/OP_WRITE(4)/OP_CONNECT(8)

2）socketChannel.register(Selector selector,int interestOps):SelectionKey
一个通道channel/连接 对应一个唯一的selectionKey，这个唯一的key对应多个事件
interestOps=5表示读操作+写操作(1+4)
上述的注册等同于selectionKey.interestOps(int interestOps)

3）Selector.select()阻塞方法，直到至少有一个channel被选择到或者被唤醒或者被中断
Selector.selectedKeys()根据keys来生成publicSelectedKeys，所以之前注册过的事件应当删除



------------------------AIO浅析------------------------
/**
 * AIO-Proactor模型 纯异步非阻塞IO
 * 
 * 应用于Netty/Mina等异步框架
 * 
 * 内部也采用了连接池+事件通知(系统级)
 * 
 * @author jtj
 *         accept()/read()/write()/connect()方法中的第一个参数类型就是ComplectionHandler的第二个参数类型
 */

ComplectionHandler()是其最重要的回调类，其中的completed()/failed()方法对应事件的请求成功后/失败

针对读写都是返回操作后的读写，即操作系统处理好读写字节后返回给用户线程的

