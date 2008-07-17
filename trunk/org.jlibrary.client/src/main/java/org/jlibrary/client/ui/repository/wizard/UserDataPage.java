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
package org.jlibrary.client.ui.repository.wizard;

import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.config.ClientConfig;
import org.jlibrary.client.ui.dialogs.NewServerProfileDialog;
import org.jlibrary.core.entities.Credentials;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.security.SecurityService;
import org.jlibrary.core.security.exception.AuthenticationException;
import org.jlibrary.core.security.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * user data page
 * 
 * 
 */
public class UserDataPage extends WizardPage {

	static Logger logger = LoggerFactory.getLogger(UserDataPage.class);
	
    private Text userText;
    private Text passwordText;
	private CCombo serverCombo;
	private Button serverButton;
	private Button connectButton;
    
	private HashMap profiles = new HashMap();
    private Ticket ticket;
    private ServerProfile serverProfile;
	private boolean isOpenDialog;
	private int heightHint = 200;

	private static String lastProfile = "";
	
	private String nextMessage = Messages.getMessage("repository_wizard_ok");
	private boolean connected;
	private Credentials credentials;
	
    public UserDataPage(String pageName, 
    					String description, 
						boolean isOpenDialog) {
        
        super(pageName);
        this.isOpenDialog = isOpenDialog;
        setPageComplete(false);
        setDescription(description);
        setImageDescriptor(SharedImages.getImageDescriptor(
        		SharedImages.IMAGE_CONNECT_WIZARD)); 
    }

    public UserDataPage(String pageName, 
    					String description, 
						boolean isOpenDialog,
						int heightHint) {
        
        this(pageName,description,isOpenDialog);
        this.heightHint = heightHint;
    }

    
    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(final Composite outer) {
            	
        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL_HORIZONTAL;
        data.heightHint = heightHint;
        outer.setLayoutData(data);

        Composite parent = new Composite(outer, SWT.NONE);
        
        GridLayout pageLayout = new GridLayout();
        pageLayout.numColumns = 3;
		pageLayout.verticalSpacing = 10;
        parent.setLayout(pageLayout);
        data = new GridData();
        data.horizontalAlignment = GridData.FILL_HORIZONTAL;
        data.heightHint = 140;
        parent.setLayoutData(data);
        
		Label labServer = new Label (parent, SWT.NONE);
		labServer.setText (Messages.getMessage("new_repository_dialog_server"));
		data = new GridData();
		data = new GridData ();
		data.horizontalAlignment=GridData.FILL_HORIZONTAL;
		labServer.setLayoutData (data);
		
		serverCombo = new CCombo (parent, SWT.BORDER | SWT.WRAP);
		data = new GridData ();
		data.horizontalAlignment=GridData.FILL_HORIZONTAL;
		data.widthHint = 350;
		data.horizontalSpan = 1;
		serverCombo.setLayoutData (data);
		serverCombo.setEditable(false);
		
		serverButton = new Button(parent, SWT.NONE);
		serverButton.setImage(SharedImages.getImage(
								SharedImages.IMAGE_SERVER_PROFILE));
		data = new GridData();
		serverButton.setLayoutData(data);

		Label labName = new Label (parent, SWT.NONE);
		labName.setText (Messages.getMessage("repository_wizard_user"));
		data = new GridData ();
		data.horizontalAlignment=GridData.FILL_HORIZONTAL;
		labName.setLayoutData (data);
		
		userText = new Text (parent, SWT.BORDER);
		data = new GridData ();
		data.horizontalSpan = 2;
		data.widthHint = 350;
		data.horizontalAlignment=GridData.FILL_HORIZONTAL;
		userText.setLayoutData (data);

		Label labPassword = new Label (parent, SWT.NONE);
		labPassword.setText (Messages.getMessage("repository_wizard_password"));
		data = new GridData ();
		data.horizontalAlignment=GridData.FILL_HORIZONTAL;
		labPassword.setLayoutData (data);
		
		passwordText = new Text (parent, SWT.BORDER);
		data = new GridData ();
		data.horizontalSpan = 2;
		data.widthHint = 350;
		passwordText.setLayoutData (data);
		data.horizontalAlignment=GridData.FILL_HORIZONTAL;
		passwordText.setEchoChar('*');
		
		connectButton = new Button(parent, SWT.NONE);
		connectButton.setText(Messages.getMessage("new_repository_wizard_connect"));
		data = new GridData();
		data.horizontalSpan = 3;
		data.widthHint = 490;
		data.horizontalAlignment = GridData.FILL_HORIZONTAL;
		connectButton.setLayoutData (data);
		connectButton.setEnabled(false);
		
		ModifyListener modifyListener = new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				
				checkButtonsEnabled();
			}
		};
        
		userText.addModifyListener(modifyListener);
		passwordText.addModifyListener(modifyListener);
		
		serverButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				
				NewServerProfileDialog fd = NewServerProfileDialog.getInstance(getShell());
				fd.open();
				if (fd.getReturnCode() == IDialogConstants.OK_ID) {
					ServerProfile profile = fd.getServerProfile();
					if (!profiles.values().contains(profile)) {
						profiles.put(profile.toString(),profile);
						serverCombo.add(profile.toString());
						ClientConfig.addProfile(profile);
					}					
					lastProfile = profile.toString();
					serverCombo.select(serverCombo.indexOf(lastProfile));
				}
				checkButtonsEnabled();
			}
		});
		
		serverCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				lastProfile = serverCombo.getItem(serverCombo.getSelectionIndex());				
			}
		});
		
		connectButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				doConnect(outer);
			}

		});
		
		passwordText.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.CR) {
					doConnect(outer);
				}
			}
		});
		
		
		
        setControl(parent);
        initData();
    }

	/**
	 * @param outer
	 */
	private void doConnect(final Composite outer) {
		
		final String profile = serverCombo.getItem(serverCombo.getSelectionIndex());
		final String user = userText.getText();
		final String password = passwordText.getText();
		
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, 
															 InterruptedException {
				
				monitor.beginTask(Messages.getMessage("repository_dialog_connect"),2);
				monitor.worked(1);
				connect(profile,user,password);
				monitor.worked(2);

				monitor.done();
			}
		};
		
		WizardDialog wd = (WizardDialog)getWizard().getContainer();
		try {
			wd.run(true,true,runnable);
			
			if (connected){
				setMessage(nextMessage,IMessageProvider.INFORMATION);
				setPageComplete(true);
			} else {
				setPageComplete(false);
			}
			
		} catch (Exception e) {
			
            logger.error(e.getMessage(),e);
		}
	}

	
    
    private void connect(String profile,String user,String password) {
        
    	connected = false;
		serverProfile = (ServerProfile)profiles.get(profile);
		
		try {
			SecurityService securityService = JLibraryServiceFactory.getInstance(serverProfile).getSecurityService();
			credentials = new Credentials();
			credentials.setUser(user);
			if (user.equals(Messages.getMessage(User.ADMIN_NAME)) ||
				user.equals(User.ADMIN_KEYNAME)) {
			    credentials.setUser(User.ADMIN_NAME);
			}
			credentials.setPassword(password);
			ticket = securityService.login(credentials, 
										   SecurityService.SYSTEM_REPOSITORY);
			if (!isOpenDialog) {
				if (!ticket.getUser().isAdmin()) {
				    //setErrorMessage(Messages.getMessage("not_enough_permissions"));
					getShell().getDisplay().asyncExec(showError(Messages.getMessage("not_enough_permissions")));
				    return;				
				}
			}

			// Move the selected profile to the first place on the combo box
			ClientConfig.moveToFirst(serverProfile);
		
			connected = true;
			
			getShell().getDisplay().asyncExec(showError(null));
			getShell().getDisplay().asyncExec(showMessage(nextMessage,IMessageProvider.INFORMATION));

			
		} catch (ConnectException ce) {
			getShell().getDisplay().asyncExec(showError(Messages.getMessage("connection_refused")));			
		} catch (UserNotFoundException e1) {
			getShell().getDisplay().asyncExec(showError(Messages.getAndParseValue("error_not_global_admin","%1",user)));
		} catch (AuthenticationException e1) {
			getShell().getDisplay().asyncExec(showError(Messages.getMessage("repository_dialog_authentication_error")));
			//		     setErrorMessage(Messages.getMessage("repository_dialog_authentication_error"));
		} catch (Exception e) {
//			setErrorMessage(e1.getMessage());
			
            logger.error(e.getMessage(),e);
			getShell().getDisplay().asyncExec(showError(e.getMessage()));
		}
    }
    

	private Runnable showError(final String message) {
		
		return new Runnable() {
			public void run() {
				setErrorMessage(message);
			}
		};
	}

	private Runnable showMessage(final String message,final int type) {
		
		return new Runnable() {
			public void run() {
				setMessage(message,type);
			}
		};
	}
	
	private void checkButtonsEnabled() {
        
        connectButton.setEnabled(false);
        if (serverCombo.getSelectionIndex() == -1) {
            return;
        }
        if (userText.getText().trim().equals("")) {
            return;
        }
        if (passwordText.getText().trim().equals("")) {
            return;
        }
        connectButton.setEnabled(true);
    }
    
	private void initData() {

		List profiles = ClientConfig.getServerProfiles();
		Iterator it = profiles.iterator();
		while (it.hasNext()) {
			ServerProfile profile = (ServerProfile)it.next();
			
			if (profile.getName().equals(ClientConfig.PROFILE_LOCAL_KEY)) {
			
				serverCombo.add(Messages.getMessage(profile.getName()));
				this.profiles.put(Messages.getMessage(profile.getName()),profile);			
			} else {
				serverCombo.add(profile.getName());
				this.profiles.put(profile.getName(),profile);			
			}
		}
		
		int index = serverCombo.indexOf(lastProfile);
		if (index == -1) {
			index = 0;
			lastProfile = "";
		}
		serverCombo.select(index);
	}

    /**
     * @return Returns the serverProfile.
     */
    public ServerProfile getServerProfile() {
        return serverProfile;
    }
    /**
     * @param serverProfile The serverProfile to set.
     */
    public void setServerProfile(ServerProfile serverProfile) {
        this.serverProfile = serverProfile;
    }
    /**
     * @return Returns the ticket.
     */
    public Ticket getTicket() {
        return ticket;
    }
    /**
     * @param ticket The ticket to set.
     */
    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }	
	
	/**
	 * @param nextMessage The nextMessage to set.
	 */
	public void setNextMessage(String nextMessage) {
		this.nextMessage = nextMessage;
	}
	
	public void dispose() {

	}

	public Credentials getCredentials() {
		return credentials;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}
}
