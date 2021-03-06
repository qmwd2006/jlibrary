/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Mart�n P�rez Mari��n, Blandware (represented by
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
package org.jlibrary.client.ui.editor.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.jlibrary.client.Messages;
import org.jlibrary.client.ui.ToolTipHandler;
import org.jlibrary.client.ui.dnd.DNDItems;
import org.jlibrary.client.ui.list.ListViewer;
import org.jlibrary.client.ui.security.providers.UsersLabelProvider;
import org.jlibrary.core.entities.Group;
import org.jlibrary.core.entities.User;

/**
 * Relations management UI section
 * 
 * @author martin
 */
public class GroupUsersSection extends SectionPart {
	
	private ListViewer viewer;
	
	public static final int CLIENT_VSPACING = 4;

	private GroupFormMetadata formMetadata;
	private Group group;
	
	public ArrayList currentUsers = new ArrayList();

	/**
	 * Internal drop listener
	 * 
	 * @author martin
	 */
	class UserDropListener extends ViewerDropAdapter {

		public UserDropListener(ListViewer viewer) {
		   
			super(viewer);
		}

		/**
		 * Method declared on ViewerDropAdapter
		 */
		public boolean performDrop(Object data) {
		  
			Object[] toDrop = DNDItems.getItems();
		  
			List users = Arrays.asList(toDrop);
			createUsers(users);

			DNDItems.clear();
			
			return true;
	   	}
	   
		/**
		 * Method declared on ViewerDropAdapter
		 */
		public boolean validateDrop(Object target, int op, TransferData type) {
		   
			boolean isValid = TextTransfer.getInstance().isSupportedType(type);
			if (!isValid) {
				return false;
			}
					
			Object[] toDrop = DNDItems.getItems();
			for (int i = 0; i < toDrop.length; i++) {
				if (!(toDrop[i] instanceof User)) {
					return false;
				}
			} 
		   
		   return true;
		}		
	}	
	
	private void createUsers(Collection users) {
		
		Iterator it = users.iterator();
		while (it.hasNext()) {
			User user = (User) it.next();
			if (currentUsers.contains(user)) {
				continue;
			}
			group.addUser(user);
			currentUsers.add(user);
		}
		viewer.refresh();
		formMetadata.propertiesModified();
	}	
	
	public GroupUsersSection(FormToolkit toolkit,
						    GroupFormMetadata formMetadata, 
						    Composite parent) {

		super(parent, toolkit, Section.TITLE_BAR | Section.TWISTIE );
		
		this.formMetadata = formMetadata;
		this.group = formMetadata.getGroup();
		
		if (group.getUsers() != null) {
			this.currentUsers = new ArrayList(group.getUsers());
		} else {
			this.currentUsers = new ArrayList();
		}
		
		getSection().clientVerticalSpacing = CLIENT_VSPACING;
		getSection().setData("part", this);
		
		createClient(toolkit);
	}

	
	protected void createClient(FormToolkit toolkit) {
		
		Section section = getSection();
		
		section.setText(Messages.getMessage("users_section"));
		section.setDescription(Messages.getMessage("users_section_description"));
		section.setExpanded(true);
		
		TableWrapData td = new TableWrapData(TableWrapData.FILL,TableWrapData.TOP);
		td.grabHorizontal = true;
		section.setLayoutData(td);

		Composite client = toolkit.createComposite(section);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = false;
		layout.marginWidth = 10;
		layout.verticalSpacing = 20;
		layout.horizontalSpacing = 10;
		client.setLayout(layout);
		section.setClient(client);
		toolkit.paintBordersFor(client);

		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.heightHint = 100;
		
		viewer = new ListViewer(client, 
				SWT.MULTI | SWT.H_SCROLL | SWT.FLAT | SWT.BORDER);
		viewer.getControl().setLayoutData(gd);
		viewer.setLabelProvider(new UsersLabelProvider());
			
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				
				User user = (User)((IStructuredSelection)
						event.getSelection()).getFirstElement();
				group.removeUser(user);
				currentUsers.remove(user);
				formMetadata.propertiesModified();
				viewer.refresh();
			}
		});	
		
		ToolTipHandler handler = new ToolTipHandler(client.getShell());
		handler.activateHoverHelp(viewer.getTable());		

		// Add drag & drop support
		Transfer[] types = new Transfer[] {TextTransfer.getInstance()};
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		viewer.addDropSupport(operations,
							  types,
							  new UserDropListener(viewer));	
		
		viewer.setInput(currentUsers);
	}
	
	public void dispose() {

		super.dispose();
	}

	/**
	 * @see org.eclipse.ui.forms.SectionPart#hookListeners()
	 */
	protected void hookListeners() {}	
}
