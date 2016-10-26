/*
 * Copyright (C) 2016 Pablo Campillo-Sanchez <pabcampi@ucm.es>
 *
 * This software has been developed as part of the 
 * SociAAL project directed by Jorge J. Gomez Sanz
 * (http://grasia.fdi.ucm.es/sociaal)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package phat.util.video;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.FrameRecorder.Exception;
/**
 * 
 * @author Rafael Pax
 *
 */
public class JavaCVWriter implements VideoSequenceWriter {

	private FrameRecorder recorder;
	private int width;
	private int height;
	private ExecutorService executor;
	private double framerate = 25;
	private BlockingQueue<Frame> toWrite = new ArrayBlockingQueue<Frame>(512);
	private Queue<Frame> cached = new ConcurrentLinkedQueue<Frame>();
	private boolean finished;

	private int framesRecorded = 0;

	public JavaCVWriter(int width, int height)
	{
		this.width = width;
		this.height = height;
		this.finished = false;
	}

	private Logger logger = Logger.getLogger(getClass().getName());
	private int cachedMaxSize = 0;
	private int toWriteMaxSize = 0;

	private void writeLoop()
	{
		try
		{
			try
			{
				while (!this.finished)
				{

					toWriteMaxSize = Math.max(toWriteMaxSize,
							this.toWrite.size());
					cachedMaxSize = Math.max(this.cachedMaxSize,
							this.cached.size());
					//

					Frame f = this.toWrite.take();
					// f.timestamp = (long) ((this.framesRecorded /
					// this.framerate)
					// * 1000 * 1000);
					// recorder.setTimestamp(f.timestamp);
					this.recorder.record(f);
					framesRecorded++;
					this.cached.add(f);
					if (this.framesRecorded % (framerate * 10) == 0)
					{
						logger.info("Frames Recorded: " + framesRecorded
								+ ". Cached max size: " + this.cachedMaxSize
								+ ". toWriteMaxSize: " + toWriteMaxSize);
					}
				}
			} catch (InterruptedException e)
			{
				this.finished = true;
				while (!this.toWrite.isEmpty())
				{
					this.recorder.record(this.toWrite.poll());
					framesRecorded++;
				}
				logger.info("Frames Recorded: " + framesRecorded);
				this.recorder.stop();
				this.recorder.release();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	@Override
	public void start(File output)
	{

		try
		{
			this.recorder = FrameRecorder.createDefault(output, width, height);
			System.out.println(this.recorder.getClass());
			// OJO, esta a pelo
			this.recorder.setFrameRate(this.framerate);
			this.recorder.setVideoQuality(0.9);
			this.executor = Executors.newSingleThreadExecutor();
			this.executor.submit(new Runnable() {
				public void run()
				{
					try
					{
						JavaCVWriter.this.recorder.start();
					} catch (java.lang.Exception e)
					{
						throw new RuntimeException(e);
					}
				}
			});
			this.executor.submit(new Runnable() {
				public void run()
				{
					JavaCVWriter.this.writeLoop();
				}
			});
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	@Override
	public void writePicture(ByteBuffer rgbaBuff)
	{

		Frame frame = newFrame();
		ByteBuffer img = ((ByteBuffer) frame.image[0]);
		img.clear();
		img.put(rgbaBuff);
		img.limit(rgbaBuff.limit());
		flipY(frame);
		try
		{
			this.toWrite.put(frame);// .put(frame);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}

	}

	private static void flipY(Frame f)
	{
		int height = f.imageHeight;
		int width = f.imageWidth;
		ByteBuffer buf = ((ByteBuffer) f.image[0]);
		for (int y = 0; y < height / 2; y++)
		{
			for (int x = 0; x < width; x++)
			{
				int inPtr0 = (y * width + x) * 4;
				int outPtr0 = ((height - y - 1) * width + x) * 4;
				//

				byte b1 = buf.get(inPtr0 + 0);
				byte g1 = buf.get(inPtr0 + 1);
				byte r1 = buf.get(inPtr0 + 2);
				byte a1 = buf.get(inPtr0 + 3);

				byte b2 = buf.get(outPtr0 + 0);
				byte g2 = buf.get(outPtr0 + 1);
				byte r2 = buf.get(outPtr0 + 2);
				byte a2 = buf.get(outPtr0 + 3);

				// cpuArray[outPtr+0] = a1;
				buf.put(outPtr0 + 0, r1);
				buf.put(outPtr0 + 1, g1);
				buf.put(outPtr0 + 2, b1);
				buf.put(outPtr0 + 3, a1);

				buf.put(inPtr0 + 0, r2);
				buf.put(inPtr0 + 1, g2);
				buf.put(inPtr0 + 2, b2);
				buf.put(inPtr0 + 3, a2);
			}
		}
	}

	private Frame newFrame()
	{
		Frame f = this.cached.poll();
		if (f == null)
		{
			f = new Frame(width, height, Frame.DEPTH_UBYTE,4/* RGBA */);
		}
		return f;
	}

	@Override
	public void stop()
	{

		this.executor.shutdownNow();

	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
		this.recorder.setImageWidth(width);
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
		this.recorder.setImageHeight(height);
	}

}
