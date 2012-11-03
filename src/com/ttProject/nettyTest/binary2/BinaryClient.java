package com.ttProject.nettyTest.binary2;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.flazr.util.Utils;

public class BinaryClient {
	public static void main(String[] args) {
		new BinaryClient();
	}
	public BinaryClient() {
		int port = 12345;
		ClientBootstrap bootstrap = new ClientBootstrap(
				new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		
		bootstrap.setPipelineFactory(new TestClientPipelineFactory());
		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("keepAlive", true);
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(port));
		future.awaitUninterruptibly();
		if(!future.isSuccess()) {
			bootstrap.releaseExternalResources();
			return;
		}
		future.getChannel().getCloseFuture().awaitUninterruptibly();
		bootstrap.getFactory().releaseExternalResources();
	}
	private class TestClientPipelineFactory implements ChannelPipelineFactory {
		@Override
		public ChannelPipeline getPipeline() throws Exception {
			ChannelPipeline pipeline = Channels.pipeline();
			
			pipeline.addLast("handler", new TestClientHandler());
			return pipeline;
		}
	}
	@ChannelPipelineCoverage("one")
	private class TestClientHandler extends SimpleChannelUpstreamHandler {
		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
				throws Exception {
			ByteBuffer buffer = ((ChannelBuffer)e.getMessage()).toByteBuffer();
			int length = buffer.limit();
			byte[] data = new byte[length];
			buffer.get(data);
			System.out.println(Utils.toHex(data, 0, length, true));
			super.messageReceived(ctx, e);
		}
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
				throws Exception {
			e.getCause().printStackTrace();
		}
	}
}
