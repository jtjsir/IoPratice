package com.jing.io.aio.handler;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @author jingsir
 **
 *         写事件
 */
public class AioServerWriteHandler implements CompletionHandler<Integer, ByteBuffer> {

	private AsynchronousSocketChannel socketChannel;

	public AioServerWriteHandler(AsynchronousSocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	@Override
	public void completed(Integer arg0, ByteBuffer buffer) {
		//写完buffer所有数据
		while(buffer.hasRemaining()){
			socketChannel.write(buffer,buffer,this) ;
		}
		//读事件开启
		ByteBuffer readBuf = ByteBuffer.allocate(1024) ;
		socketChannel.read(readBuf, readBuf, new AioServerReadHandler(socketChannel));
	}

	@Override
	public void failed(Throwable arg0, ByteBuffer arg1) {
		arg0.printStackTrace();
	}

}
