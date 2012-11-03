package com.ttProject.nettyTest.binary2;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
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
		private Set<Channel> channels = new HashSet<Channel>();
		public TestServerHandler() {
			super();
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						while(true) {
							ChannelBuffer buffer = ChannelBuffers.buffer(10);
							buffer.writeByte((byte)0x01);
							buffer.writeByte((byte)0x02);
							buffer.writeByte((byte)0x03);
							buffer.writeByte((byte)0x04);
							buffer.writeByte((byte)0x05);
							buffer.writeByte((byte)0x06);
							buffer.writeByte((byte)0x07);
							buffer.writeByte((byte)0x08);
							buffer.writeByte((byte)0x09);
							buffer.writeByte((byte)0x00);
							synchronized(channels) {
								for(Channel channel : channels) {
									ChannelFuture f = channel.write(buffer);
									f.addListener(ChannelFutureListener.CLOSE);
								}
							}
							Thread.sleep(1000);
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			t.setDaemon(true);
			t.start();
		}
		@Override
		public void channelConnected(ChannelHandlerContext ctx,
				ChannelStateEvent e) throws Exception {
			synchronized(channels) {
				channels.add(e.getChannel());
			}
			super.channelConnected(ctx, e);
		}
		@Override
		public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
				throws Exception {
			synchronized(channels) {
				channels.remove(e.getChannel());
			}
			super.channelClosed(ctx, e);
		}
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
