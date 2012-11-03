package com.ttProject.nettyTest.binary;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.flazr.util.Utils;

public class BinaryServer {
	public static void main(String[] args) {
		new BinaryServer();
	}
	public BinaryServer() {
		ServerBootstrap bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool())
				);
		bootstrap.setPipelineFactory(new TestServerPipelineFactory());
		bootstrap.bind(new InetSocketAddress(12345));
	}
	public class TestServerPipelineFactory implements ChannelPipelineFactory {
		@Override
		public ChannelPipeline getPipeline() throws Exception {
			ChannelPipeline pipeline = Channels.pipeline();
			pipeline.addLast("handler", new TestServerHandler());
			return pipeline;
		}
	}
	@ChannelPipelineCoverage("one")
	public class TestServerHandler extends SimpleChannelUpstreamHandler {
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
	}
}
