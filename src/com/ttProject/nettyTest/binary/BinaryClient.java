package com.ttProject.nettyTest.binary;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

public class BinaryClient {
	public static void main(String[] args) {
		new BinaryClient();
	}
	public BinaryClient() {
		int port = 12345;
		ClientBootstrap bootstrap = new ClientBootstrap(
				new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		
		bootstrap.setPipelineFactory(new TestClientPipelineFactory());
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(port));
		Channel channel = future.awaitUninterruptibly().getChannel();
		ChannelFuture lastWriteFuture = null;
		if(!future.isSuccess()) {
			future.getCause().printStackTrace();
			bootstrap.releaseExternalResources();
			return;
		}
		ChannelBuffer out = ChannelBuffers.buffer(13);
		out.writeByte((byte)0x01);
		out.writeByte((byte)0x02);
		out.writeByte((byte)0x03);
		out.writeByte((byte)0x04);
		out.writeByte((byte)0x05);
		out.writeByte((byte)0x06);
		lastWriteFuture = channel.write(out);
		if(lastWriteFuture != null) {
			lastWriteFuture.awaitUninterruptibly();
		}
		channel.close().awaitUninterruptibly();
		bootstrap.releaseExternalResources();
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
	}
}
