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
package org.jlibrary.client.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.ui.security.MembersRegistry;
import org.jlibrary.client.util.NodeUtils;
import org.jlibrary.core.entities.Author;
import org.jlibrary.core.entities.Bookmark;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Group;
import org.jlibrary.core.entities.Member;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ResourceNode;
import org.jlibrary.core.entities.Restriction;
import org.jlibrary.core.entities.Rol;
import org.jlibrary.core.entities.User;

/**
 * Emulated tooltip handler Notice that we could display anything in a tooltip
 * besides text and images. For instance, it might make sense to embed large
 * tables of data or buttons linking data under inspection to material
 * elsewhere, or perform dynamic lookup for creating tooltip text on the fly.
 */
public class ToolTipHandler {

	public static final String TEXT_KEY = "TIP_TEXT";

	public static final String IMAGE_KEY = "TIP_IMAGE";

	public static final String HELP_KEY = "TIP_HELP";

	private Shell parentShell;

	private Shell tipShell;

	private Label tipLabelImage, tipLabelText;

	private Widget tipWidget; // widget this tooltip is hovering over

	private Point tipPosition; // the position being hovered over

	/**
	 * Creates a new tooltip handler
	 * 
	 * @param parent
	 *            the parent Shell
	 */
	public ToolTipHandler(Shell parent) {
		final Display display = parent.getDisplay();
		this.parentShell = parent;

		tipShell = new Shell(parent, SWT.ON_TOP);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginWidth = 2;
		gridLayout.marginHeight = 2;
		tipShell.setLayout(gridLayout);

		tipShell.setBackground(display
				.getSystemColor(SWT.COLOR_INFO_BACKGROUND));

		tipLabelImage = new Label(tipShell, SWT.NONE);
		tipLabelImage.setForeground(display
				.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		tipLabelImage.setBackground(display
				.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		tipLabelImage.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_CENTER));

		GridData data = new GridData();
		data.widthHint = 300;
		data.horizontalAlignment = GridData.FILL_HORIZONTAL;
		data.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
		tipLabelText = new Label(tipShell, SWT.WRAP);
		tipLabelText.setForeground(display
				.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		tipLabelText.setBackground(display
				.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		tipLabelText.setLayoutData(data);
	}

	/**
	 * Enables customized hover help for a specified control
	 * 
	 * @control the control on which to enable hoverhelp
	 */
	public void activateHoverHelp(final Control control) {
		/*
		 * Get out of the way if we attempt to activate the control underneath
		 * the tooltip
		 */
		control.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				if (tipShell.isVisible())
					tipShell.setVisible(false);
			}
		});

		/*
		 * Trap hover events to pop-up tooltip
		 */
		control.addMouseTrackListener(new MouseTrackAdapter() {
			public void mouseExit(MouseEvent e) {
				if (tipShell.isVisible())
					tipShell.setVisible(false);
				tipWidget = null;
			}

			public void mouseHover(MouseEvent event) {
				Point pt = new Point(event.x, event.y);
				Widget widget = event.widget;
				if (widget instanceof ToolBar) {
					ToolBar w = (ToolBar) widget;
					widget = w.getItem(pt);
				}
				if (widget instanceof Table) {
					Table w = (Table) widget;
					widget = w.getItem(pt);
				}
				if (widget instanceof Tree) {
					Tree w = (Tree) widget;
					widget = w.getItem(pt);
				}
				if (widget == null) {
					tipShell.setVisible(false);
					tipWidget = null;
					return;
				}
				if (widget == tipWidget)
					return;
				tipWidget = widget;
				tipPosition = control.toDisplay(pt);
				String text = "";
				if (widget.getData(TEXT_KEY) != null) {
					text = (String) widget.getData(TEXT_KEY);
				} else {
					text = getText(widget.getData());

				}
				Image image = null;
				if (widget.getData(IMAGE_KEY) != null) {
					image = (Image) widget.getData(IMAGE_KEY);
				} else {
					image = SharedImages
							.getImage(SharedImages.IMAGE_INFORMATION);
				}
				tipLabelText.setText(text != null ? text : "");
				tipLabelImage.setImage(image); // accepts null
				tipShell.pack();
				setHoverLocation(tipShell, tipPosition);
				tipShell.setVisible(true);
			}
		});

		/*
		 * Trap F1 Help to pop up a custom help box
		 */
		control.addHelpListener(new HelpListener() {
			public void helpRequested(HelpEvent event) {

				if (tipWidget == null) {
					return;
				}
				String text = (String) tipWidget.getData(HELP_KEY);
				if (text == null) {
					text = Messages.getMessage("hover_help_none");
				}

				if (tipShell.isVisible()) {
					tipShell.setVisible(false);
					Shell helpShell = new Shell(parentShell, SWT.SHELL_TRIM);
					helpShell.setLayout(new FillLayout());
					Label label = new Label(helpShell, SWT.NONE);
					label.setText(text);
					helpShell.pack();
					setHoverLocation(helpShell, tipPosition);
					helpShell.open();
				}
			}
		});
	}

	/**
	 * 
	 * @param data
	 * @return
	 */
	protected String getText(Object data) {
		// Category
		if (data instanceof Category) {
			Category category = (Category) data;
			if (category.isUnknownCategory()) {
				return Messages.getMessage(category.toString());
			}
			return category.toString();
			// Document
		} else if (data instanceof Document) {
			Document document = (Document) data;
			if (document.getLock() != null) {
				return loadLockTooltip(document);
			}
			return ((Document) data).getDescription();
			// Directory or Ressource
		} else if ((data instanceof Directory)
				|| (data instanceof ResourceNode)) {
			return ((Node) data).getDescription();
			// Author
		} else if (data instanceof Author) {
			return ((Author) data).getBio();
			// Repository
		} else if (data instanceof Repository) {
			return ((Repository) data).getDescription();
			// user
		} else if (data instanceof User) {
			if (((User) data).equals(User.ADMIN_USER)) {
				return Messages.getMessage(User.ADMIN_NAME);
			}
			// Group
		} else if (data instanceof Group) {
			Group group = (Group) data;
			if (group.getDescription().equals(Group.ADMINS_GROUP_DESCRIPTION)
					|| group.getDescription().equals(
							Group.PUBLISHERS_GROUP_DESCRIPTION)
					|| group.getDescription().equals(
							Group.READERS_GROUP_DESCRIPTION)) {
				return Messages.getMessage(group.getDescription());
			}
			// Role
		} else if (data instanceof Rol) {
			Rol rol = (Rol) data;
			if (rol.getDescription().equals(Rol.ADMIN_ROLE_NAME)
					|| rol.getDescription().equals(
							Rol.PUBLISHER_ROLE_DESCRIPTION)
					|| rol.getDescription().equals(Rol.READER_ROLE_DESCRIPTION)) {
				return Messages.getMessage(rol.getDescription());
			}
		} else if (data instanceof Restriction) {
			Restriction restriction = (Restriction) data;
			Member member = MembersRegistry.getInstance().getMember(
					restriction.getMember());
			if (member.equals(User.ADMIN_USER))
				return Messages.getMessage(User.ADMIN_NAME);
			if (member instanceof Group )
				return Messages.getMessage(((Group)member).getDescription());
			return member.getName();
		} else if (data instanceof Bookmark) {
			return ((Bookmark)data).getDescription();
		}
		return data.toString();
	}

	/**
	 * Sets the location for a hovering shell
	 * 
	 * @param shell
	 *            the object that is to hover
	 * @param position
	 *            the position of a widget to hover over
	 * @return the top-left location for a hovering box
	 */
	private void setHoverLocation(Shell shell, Point position) {

		Rectangle displayBounds = shell.getDisplay().getBounds();

		Rectangle shellBounds = shell.getBounds();
		shellBounds.x = Math.max(Math.min(position.x, displayBounds.width
				- shellBounds.width), 0);
		shellBounds.y = Math.max(Math.min(position.y + 16, displayBounds.height
				- shellBounds.height), 0);

		shell.setBounds(shellBounds);
	}

	private String loadLockTooltip(Document document) {

		StringBuilder description = new StringBuilder(document.getDescription());

		String username = NodeUtils.getCreatorName(document);
		if (username != null) {
			if (username.equals(User.ADMIN_NAME)) {
				username = Messages.getMessage(User.ADMIN_NAME);
			}
			description.append("\n\n"
					+ Messages.getAndParseValue("tooltip_locked", "%1",
							username));
		}
		return description.toString();
	}
}
