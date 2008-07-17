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

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.output.ProxyOutputStream;

/**
 * This input stream limits the amount of data that can download. The idea offer 
 * a way to avoid DOS attacks without having to touch the client side. If the caller tries 
 * to download too much data from this stream then an IOException will be called. The amount 
 * of data allowed to be read can be specified from the constructor.
 * 
 * @author mperez
 *
 */
public class LimitedOutputStream extends ProxyOutputStream {

	private long byteCount;
	private long byteLimit;
	
	/**
	 * Creates a limited output stream. This stream will just proxy all the calls to the 
	 * output stream passed as parameter but it will count the amount of bytes written. If the 
	 * amount of bytes written is bigger than the specified limit then an IOException will 
	 * be thrown. 
	 * 
	 * @param proxy
	 * @param byteLimit
	 */
	public LimitedOutputStream(OutputStream proxy, long byteLimit) {
		
		super(proxy);
		this.byteLimit = byteLimit;
	}

	@Override
	public void write(byte[] bts) throws IOException {

		byteCount+=bts.length;
		checkByteLimit();
		super.write(bts);
	}
	
	@Override
	public void write(int idx) throws IOException {

		byteCount+=4;
		checkByteLimit();
		super.write(idx);
	}
	
	@Override
	public void write(byte[] bts, int st, int end) throws IOException {

		byteCount+=end-st+1;
		checkByteLimit();
		super.write(bts, st, end);
	}
	
	private void checkByteLimit() throws IOException {
		
		if (byteCount > byteLimit) {
			throw new IOException("This service is limited to " + byteLimit + " bytes. The maximum allowed throughput has been exceeded.");
		}
	}

	/**
	 * Returns the number of bytes that have been written through this output stream
	 *  
	 * @return long Number of bytes written
	 */
	public long getByteCount() {
		return byteCount;
	}
}
