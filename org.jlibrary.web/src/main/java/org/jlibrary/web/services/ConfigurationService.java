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

import java.util.ArrayList;
import java.util.List;

import org.jlibrary.core.entities.User;
import org.jlibrary.web.services.config.ConfigNotFoundException;
import org.jlibrary.web.services.config.RepositoryConfig;

/**
 * <p>This Spring service will hold several configuration values. It holds a collection 
 * of configuration beans for each repository and several common configuration properties.</p>
 * 
 * @author dlatorre
 * @author mpermar
 *
 */
public class ConfigurationService {
	
	private Long operationInputBandwidth;
	private Long operationOutputBandwidth;
	private Long totalInputBandwidth;
	private Long totalOutputBandwidth;
	
	private static final String DEFAULT_CONFIG = "default";
	
	// Default password
	private String rootPassword = User.DEFAULT_PASSWORD;
	
	private List<RepositoryConfig> configEntries = new ArrayList<RepositoryConfig>();
	
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

	public void setConfigEntries(List<RepositoryConfig> configEntries) {
		this.configEntries = configEntries;
	}
	
	/**
	 * Returns a configuration entry for the given repository. Whether the configuration is not found
	 * a ConfigNotFoundException will be thrown.
	 *  
	 * @param repositoryName Repository for which we are looking for a configuration object
	 * 
	 * @return RepositoryConfig Configuration for the given repository
	 * 
	 * @throws ConfigNotFoundException Whether the configuration entry cannot be found
	 */
	public RepositoryConfig getRepositoryConfig(String repositoryName) throws ConfigNotFoundException {
		
		if (repositoryName == null) {
			throw new ConfigNotFoundException("Invalid configuration for repository: " + repositoryName);
		}
		
		for(RepositoryConfig config: configEntries) {
			if (repositoryName.equalsIgnoreCase(config.getRepositoryName())) {
				return config;
			}
		}
		
		// Configuratoin not found try to find the default config
		for(RepositoryConfig config: configEntries) {
			if (DEFAULT_CONFIG.equalsIgnoreCase(config.getRepositoryName())) {
				return config;
			}
		}
		
		throw new ConfigNotFoundException();
	}

	public String getRootPassword() {
		return rootPassword;
	}

	public void setRootPassword(String rootPassword) {
		this.rootPassword = rootPassword;
	}
}
