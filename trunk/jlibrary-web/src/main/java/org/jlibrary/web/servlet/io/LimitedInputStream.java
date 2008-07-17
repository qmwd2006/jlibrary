/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Martín Pérez Mariñán, Blandware (represented by
* Andrey Grebnev), and individual contributors as indicated by the
* @authors tag. See copyright.txt in the distribution for a full listing of
* individual contributors. All rights reserved.
* 
* This is free software; you can redistribute it and/or modify it
* under the terms of the Modified BSD License as published by the Free 
* Software Foundation.
* 
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Modified
* BSD License for more details.
* 
* You should have received a copy of the Modified BSD License along with 
* this software; if not, write to the Free Software Foundation, Inc., 
* 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the
* FSF site: http://www.fsf.org.
*/
package org.jlibrary.web.servlet.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.input.ProxyInputStream;

/**
 * This input stream limits the amount of data that can be sent through it. The idea offer 
 * a way to avoid DOS attacks without having to touch the client side. If the caller tries 
 * to read too much data from this InputStream then an IOException will be called. The amount 
 * of data allowed to be read can be specified from the constructor.
 * 
 * @author mperez
 *
 */
public class LimitedInputStream extends FilterInputStream {

	private long byteCount;
	private long byteLimit;
	
	/**
	 * Creates a limited input stream. This stream will just proxy all the calls to the 
	 * input stream passed as parameter but it will count the amount of bytes readed. If the 
	 * amount of bytes readed is bigger than the specified limit then an IOException will 
	 * be thrown. 
	 * 
	 * @param proxy
	 * @param byteLimit
	 */
	public LimitedInputStream(InputStream proxy, long byteLimit) {
		
		super(proxy);
		this.byteLimit = byteLimit;
	}
	
	@Override
	public int available() throws IOException {
		// TODO Auto-generated method stub
		return super.available();
	}
	
	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		super.close();
	}
	
	@Override
	public synchronized void mark(int readlimit) {
		// TODO Auto-generated method stub
		super.mark(readlimit);
	}
	
	@Override
	public boolean markSupported() {
		// TODO Auto-generated method stub
		return super.markSupported();
	}
	
	@Override
	public synchronized void reset() throws IOException {
		// TODO Auto-generated method stub
		super.reset();
	}
	
	@Override
	public long skip(long n) throws IOException {
		// TODO Auto-generated method stub
		return super.skip(n);
	}
	
	@Override
	public int read() throws IOException {

		byteCount+=4;
		checkByteLimit();
		return super.read();
	}
	
	@Override
	public int read(byte[] bts) throws IOException {

		byteCount+=bts.length;
		checkByteLimit();
		
		return super.read(bts);
	}
	
	@Override
	public int read(byte[] bts, int st, int end) throws IOException {

		byteCount+=end-st+1;
		checkByteLimit();
		return super.read(bts, st, end);
	}
	
	private void checkByteLimit() throws IOException {
		
		if (byteCount > byteLimit) {
			throw new IOException("This service is limited to " + byteLimit + " bytes. The maximum allowed throughput has been exceeded.");
		}
	}
	
	/**
	 * Returns the number of bytes that have been read through this Input Stream
	 *  
	 * @return long Number of bytes read
	 */
	public long getByteCount() {
		return byteCount;
	}
}
