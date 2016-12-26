package com.jing.io.aio.handler;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @author jingsir
 **
 * 客户端读取事件
 */
public class AioClientReadHandler implements CompletionHandler<Integer, ByteBuffer> {

	private AsynchronousSocketChannel socketChannel;

	public AioClientReadHandler(AsynchronousSocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	@Override
	public void completed(Integer arg0, ByteBuffer buffer) {
		System.err.println("读取了 " + arg0 + " 个字节");
		buffer.flip();
		byte[] data = new byte[buffer.remaining()];
		try {
			System.err.println("接收到的服务端的信息为: " + new String(data, "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// write操作
		ByteBuffer src = ByteBuffer.allocate(1024);
		src.put("hello server".getBytes());
		src.flip();
		socketChannel.write(src, src, new AioClientWriteHandler(socketChannel));
	}

	@Override
	public void failed(Throwable arg0, ByteBuffer arg1) {
		arg0.printStackTrace();
	}

}
