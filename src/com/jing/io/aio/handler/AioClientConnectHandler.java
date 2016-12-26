package com.jing.io.aio.handler;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import com.jing.io.aio.AioClient;

/**
 * @author jingsir
 **
 * 
 */
public class AioClientConnectHandler implements CompletionHandler<Void, AioClient> {

	private AsynchronousSocketChannel socketChannel;

	public AioClientConnectHandler(AsynchronousSocketChannel channel) {
		this.socketChannel = channel;
	}

	@Override
	public void completed(Void arg0, AioClient arg1) {
		System.err.println("连接服务端成功");
		// write操作
		ByteBuffer src = ByteBuffer.allocate(1024);
		src.put("hello server".getBytes());
		src.flip();
		socketChannel.write(src, src, new AioClientWriteHandler(socketChannel));
	}

	@Override
	public void failed(Throwable arg0, AioClient arg1) {
		arg0.printStackTrace();
		arg1.getLatch().countDown();
	}

}
