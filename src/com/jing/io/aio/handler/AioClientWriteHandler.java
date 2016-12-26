package com.jing.io.aio.handler;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @author jingsir
 **
 * 
 */
public class AioClientWriteHandler implements CompletionHandler<Integer, ByteBuffer> {

	private AsynchronousSocketChannel socketChannel;

	public AioClientWriteHandler(AsynchronousSocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	@Override
	public void completed(Integer result, ByteBuffer attachment) {
		while(attachment.hasRemaining()){
			socketChannel.write(attachment, attachment, this);
		}
		ByteBuffer readBuf = ByteBuffer.allocate(1024) ;
		socketChannel.read(readBuf, readBuf, new AioClientReadHandler(socketChannel));
	}

	@Override
	public void failed(Throwable exc, ByteBuffer attachment) {
		exc.printStackTrace();
	}

}
