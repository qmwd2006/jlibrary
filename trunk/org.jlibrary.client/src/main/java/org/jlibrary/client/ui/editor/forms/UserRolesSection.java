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
import org.jlibrary.client.ui.security.providers.RolesLabelProvider;
import org.jlibrary.core.entities.Rol;
import org.jlibrary.core.entities.User;

/**
 * Relations management UI section
 * 
 * @author martin
 */
public class UserRolesSection extends SectionPart {
	
	private ListViewer viewer;
	
	public static final int CLIENT_VSPACING = 4;

	private UserFormMetadata formMetadata;
	private User user;

	public ArrayList currentRoles = new ArrayList();

	/**
	 * Internal drop listener
	 * 
	 * @author martin
	 */
	class RolDropListener extends ViewerDropAdapter {

		public RolDropListener(ListViewer viewer) {
		   
			super(viewer);
		}

		/**
		 * Method declared on ViewerDropAdapter
		 */
		public boolean performDrop(Object data) {
		  
			Object[] toDrop = DNDItems.getItems();
		  
			List roles = Arrays.asList(toDrop);
			createRoles(roles);

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
				if (!(toDrop[i] instanceof Rol)) {
					return false;
				}
			} 
		   
		   return true;
		}		
	}	
	
	private void createRoles(Collection roles) {
		
		Iterator it = roles.iterator();
		while (it.hasNext()) {
			Rol rol = (Rol) it.next();
			if (currentRoles.contains(rol)) {
				continue;
			}
			currentRoles.add(rol);
			user.addRol(rol);
		}
		viewer.refresh();
		formMetadata.propertiesModified();		
	}	
	
	public UserRolesSection(FormToolkit toolkit,
						    UserFormMetadata formMetadata, 
						    Composite parent) {

		super(parent, toolkit, Section.TITLE_BAR | Section.TWISTIE );
		
		this.formMetadata = formMetadata;
		this.user = formMetadata.getUser();
		
		if (user.getRoles() != null) {
			this.currentRoles = new ArrayList(user.getRoles());
		} else {
			this.currentRoles = new ArrayList();
		}
		
		getSection().clientVerticalSpacing = CLIENT_VSPACING;
		getSection().setData("part", this);
		
		createClient(toolkit);
	}

	
	protected void createClient(FormToolkit toolkit) {
		
		Section section = getSection();
		
		section.setText(Messages.getMessage("roles_section"));
		section.setDescription(Messages.getMessage("roles_section_description"));
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
		viewer.setLabelProvider(new RolesLabelProvider());

		ToolTipHandler handler = new ToolTipHandler(client.getShell());
		handler.activateHoverHelp(viewer.getTable());		
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				
				Rol rol = (Rol)((IStructuredSelection)
						event.getSelection()).getFirstElement();
				currentRoles.remove(rol);
				user.removeRol(rol);
				formMetadata.propertiesModified();
				viewer.refresh();
			}
		});				
		
		// Add drag & drop support
		Transfer[] types = new Transfer[] {TextTransfer.getInstance()};
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		viewer.addDropSupport(operations,
							  types,
							  new RolDropListener(viewer));		
		
		viewer.setInput(currentRoles);
	}
	
	public void dispose() {

		super.dispose();
	}

	/**
	 * @see org.eclipse.ui.forms.SectionPart#hookListeners()
	 */
	protected void hookListeners() {}	
}
