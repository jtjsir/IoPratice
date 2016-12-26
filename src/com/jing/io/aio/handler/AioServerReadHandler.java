package com.jing.io.aio.handler;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @author jingsir
 **
 *         server端读事件
 */
public class AioServerReadHandler implements CompletionHandler<Integer, ByteBuffer> {

	private AsynchronousSocketChannel socketChannel;

	public AioServerReadHandler(AsynchronousSocketChannel channel) {
		this.socketChannel = channel;
	}

	//attchment为系统返回给用户线程的缓冲区，即读取到的字节
	@Override
	public void completed(Integer result, ByteBuffer attachment) {
		System.err.println("已经读取了 " + result + " 个字节");
		// 重新读取
		attachment.flip();
		byte[] data = new byte[attachment.remaining()];
		try {
			attachment.get(data);
			System.err.println("客户端发送的消息为: " + new String(data, "UTF-8"));
			// 写入一定的消息
			doWrite();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void doWrite() {
		ByteBuffer sendBuf = ByteBuffer.allocate(1024);
		sendBuf.put("hello client".getBytes());
		sendBuf.flip();
		// 异步写
		this.socketChannel.write(sendBuf, sendBuf, new AioServerWriteHandler(socketChannel));
	}

	@Override
	public void failed(Throwable exc, ByteBuffer attachment) {
		exc.printStackTrace();
	}

}
