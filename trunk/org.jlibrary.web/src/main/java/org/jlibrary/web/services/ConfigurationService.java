/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Martín Pérez Mariñán, and individual 
* contributors as indicated by the @authors tag. See copyright.txt in the
* distribution for a full listing of individual contributors.
* All rights reserved.
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
package org.jlibrary.web.services;

/**
 * This Spring service will hold several configuration values.
 * 
 * @author dlatorre
 * @author mpermar
 *
 */
public class ConfigurationService {
	
	private String templateDirectory;
	private Boolean registrationEnabled;
	
	private Long operationInputBandwidth;
	private Long operationOutputBandwidth;
	private Long totalInputBandwidth;
	private Long totalOutputBandwidth;

	public Long getOperationInputBandwidth() {
		return operationInputBandwidth;
	}

	public void setOperationInputBandwidth(Long operationInputBandwidth) {
		this.operationInputBandwidth = operationInputBandwidth;
	}

	public Long getOperationOutputBandwidth() {
		return operationOutputBandwidth;
	}

	public void setOperationOutputBandwidth(Long operationOutputBandwidth) {
		this.operationOutputBandwidth = operationOutputBandwidth;
	}

	public Long getTotalInputBandwidth() {
		return totalInputBandwidth;
	}

	public void setTotalInputBandwidth(Long totalInputBandwidth) {
		this.totalInputBandwidth = totalInputBandwidth;
	}

	public Long getTotalOutputBandwidth() {
		return totalOutputBandwidth;
	}

	public void setTotalOutputBandwidth(Long totalOutputBandwidth) {
		this.totalOutputBandwidth = totalOutputBandwidth;
	}

	public Boolean isRegistrationEnabled() {
		return registrationEnabled;
	}

	public void setRegistrationEnabled(Boolean isRegistrationEnabled) {
		this.registrationEnabled = isRegistrationEnabled;
	}

	public String getTemplateDirectory() {
		return templateDirectory;
	}

	public void setTemplateDirectory(String templateDirectory) {
		this.templateDirectory = templateDirectory;
	}
}
