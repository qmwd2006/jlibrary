package org.jlibrary.web.services.config;

import org.jlibrary.core.entities.User;


public class RepositoryConfig {

	private String repositoryName;
	private String templateDirectory;
	private Boolean registrationEnabled;
	private Boolean loginEnabled;
	
	// Default password
	private String adminPassword = User.DEFAULT_PASSWORD;

	public String getAdminPassword() {
		return adminPassword;
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
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

	public String getRepositoryName() {
		return repositoryName;
	}

	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}

	public Boolean getLoginEnabled() {
		return loginEnabled;
	}

	public void setLoginEnabled(Boolean loginEnabled) {
		this.loginEnabled = loginEnabled;
	}

}
