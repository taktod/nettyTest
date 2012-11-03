package com.ttProject.nettyTest.text;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

public class TextClient {
	public static void main(String[] args) {
		new TextClient();
	}
	public TextClient() {
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
		lastWriteFuture = channel.write("test\r\n");
		if(lastWriteFuture != null) {
			lastWriteFuture.awaitUninterruptibly();
		}
		channel.close().awaitUninterruptibly();
		bootstrap.releaseExternalResources();
	}
	public class TestClientPipelineFactory implements ChannelPipelineFactory {
		@Override
		public ChannelPipeline getPipeline() throws Exception {
			ChannelPipeline pipeline = Channels.pipeline();
			
			pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
			pipeline.addLast("decoder", new StringDecoder());
			pipeline.addLast("encoder", new StringEncoder());
			pipeline.addLast("handler", new TestClientHandler());
			return pipeline;
		}
	}
	@ChannelPipelineCoverage("one")
	public class TestClientHandler extends SimpleChannelUpstreamHandler {
	}
}
