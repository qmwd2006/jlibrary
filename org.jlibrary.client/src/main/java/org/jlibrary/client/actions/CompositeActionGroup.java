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
package org.jlibrary.client.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.util.Assert;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;

public class CompositeActionGroup extends ActionGroup {

	private ActionGroup[] fGroups;
	
	public CompositeActionGroup() {
	}
	
	public CompositeActionGroup(ActionGroup[] groups) {
		setGroups(groups);
	}

	protected void setGroups(ActionGroup[] groups) {
		Assert.isTrue(fGroups == null);
		Assert.isNotNull(groups);
		fGroups= groups;		
	}
		
	public ActionGroup get(int index) {
		if (fGroups == null)
			return null;
		return fGroups[index];
	}
	
	public void addGroup(ActionGroup group) {
		if (fGroups == null) {
			fGroups= new ActionGroup[] { group };
		} else {
			ActionGroup[] newGroups= new ActionGroup[fGroups.length + 1];
			System.arraycopy(fGroups, 0, newGroups, 0, fGroups.length);
			newGroups[fGroups.length]= group;
			fGroups= newGroups;
		}
	}
	
	public void dispose() {
		super.dispose();
		if (fGroups == null)
			return;
		for (int i= 0; i < fGroups.length; i++) {
			fGroups[i].dispose();
		}
	}

	public void fillActionBars(IActionBars actionBars) {
		super.fillActionBars(actionBars);
		if (fGroups == null)
			return;
		for (int i= 0; i < fGroups.length; i++) {
			fGroups[i].fillActionBars(actionBars);
		}
	}

	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		if (fGroups == null)
			return;
		for (int i= 0; i < fGroups.length; i++) {
			fGroups[i].fillContextMenu(menu);
		}
	}

	public void setContext(ActionContext context) {
		super.setContext(context);
		if (fGroups == null)
			return;
		for (int i= 0; i < fGroups.length; i++) {
			fGroups[i].setContext(context);
		}
	}

	public void updateActionBars() {
		super.updateActionBars();
		if (fGroups == null)
			return;
		for (int i= 0; i < fGroups.length; i++) {
			fGroups[i].updateActionBars();
		}
	}
}
