package org.jlibrary.client.ui.editor;

import java.util.EventObject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.jlibrary.client.ui.editor.relations.model.RelationsSchema;
import org.jlibrary.client.ui.editor.relations.part.factory.RelationsEditPartFactory;
import org.jlibrary.core.entities.Document;

public class RelationsEditor extends GraphicalEditor {
	
	private Document document;
	private GraphicalViewer graphicalViewer;
	
	public RelationsEditor(Document document) {
		this.document = document;
		
		setEditDomain(new DefaultEditDomain(this));
	}
	
	protected GraphicalViewer getGraphicalViewer() {

		return graphicalViewer;
	}
	
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {

		// store site and input
		setSite(site);
		setInput(input);

		// add CommandStackListener
		getCommandStack().addCommandStackListener(this);

		// add selection change listener
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);

		// initialize actions
		createActions();
	}
	
	
	protected void createGraphicalViewer(Composite parent) {

		ScrollingGraphicalViewer viewer = new ScrollingGraphicalViewer();
		viewer.createControl(parent);
		
		viewer.getControl().setBackground(ColorConstants.lightGray);
		viewer.setEditPartFactory(new RelationsEditPartFactory());
		viewer.setRootEditPart(new ScalableFreeformRootEditPart());
		viewer.setKeyHandler(new GraphicalViewerKeyHandler(viewer));
		
		// hook the viewer into the EditDomain
		getEditDomain().addViewer(viewer);

		// acticate the viewer as selection provider for Eclipse
		getSite().setSelectionProvider(viewer);
		
		viewer.setContents(new RelationsSchema(document));
		
		this.graphicalViewer = viewer;
	}

	protected void initializeGraphicalViewer() {

		super.initializeActionRegistry();
	}
	
	/**
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {}
	/**
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	public void doSaveAs() {}
	
	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#commandStackChanged(java.util.EventObject)
	 */
	public void commandStackChanged(EventObject event) {
		firePropertyChange(IEditorPart.PROP_DIRTY);
		super.commandStackChanged(event);
	}
	/**
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {


		return false;
	}
	
	public void dispose()
	{
		// remove CommandStackListener
		getCommandStack().removeCommandStackListener(this);
		// remove selection listener
		getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(this);
		// dispos3 the ActionRegistry (will dispose all actions)
		getActionRegistry().dispose();
		// important: always call super implementation of dispose
		super.dispose();
	}

	public void refreshRelations() {

		graphicalViewer.getContents().refresh();
	}
}
