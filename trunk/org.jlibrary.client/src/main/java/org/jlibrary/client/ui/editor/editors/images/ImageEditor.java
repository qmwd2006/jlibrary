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
package org.jlibrary.client.ui.editor.editors.images;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.INavigationLocation;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.texteditor.TextEditorPlugin;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IDocumentProviderExtension;
import org.eclipse.ui.texteditor.IDocumentProviderExtension2;
import org.eclipse.ui.texteditor.IDocumentProviderExtension3;
import org.eclipse.ui.texteditor.TextSelectionNavigationLocation;
import org.jlibrary.cache.LocalCache;
import org.jlibrary.cache.LocalCacheException;
import org.jlibrary.cache.LocalCacheService;
import org.jlibrary.cache.NodeContentHandler;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.ui.dialogs.MessageDialog;
import org.jlibrary.client.ui.images.SWTImageCanvas;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Editor for images
 */
public class ImageEditor extends EditorPart {

	static Logger logger = LoggerFactory.getLogger(ImageEditor.class);

	/** The editor's explicit document provider. */
	private IDocumentProvider fExplicitDocumentProvider;

	/** 
	 * The number of reentrances into error correction code while saving.
	 * @since 2.0
	 */
	private int fErrorCorrectionOnSave;
	/** The actions registered with the editor. */	
	private Map fActions= new HashMap(10);

	private SWTImageCanvas imageCanvas;

	private Object modelObject;

	public ImageEditor(Object object) {
		
		this.modelObject = object;
	}
	
	public ImageEditor(Document document) {
	
		this.modelObject = document;
	}
	
	public void doSave(IProgressMonitor monitor) {

		IDocumentProvider p= getDocumentProvider();
		if (p == null)
			return;
			
		if (p.isDeleted(getEditorInput())) {
			
			if (isSaveAsAllowed()) {
				
				/*
				 * 1GEUSSR: ITPUI:ALL - User should never loose changes made in the editors.
				 * Changed Behavior to make sure that if called inside a regular save (because
				 * of deletion of input element) there is a way to report back to the caller.
				 */
				performSaveAs(monitor);
			
			} else {
				
				Shell shell= getSite().getShell();
				String title= Messages.getMessage("Editor.error.save.deleted.title"); //$NON-NLS-1$
				String msg= Messages.getMessage("Editor.error.save.deleted.message"); //$NON-NLS-1$
				MessageDialog.openError(shell, title, msg);
			}
			
		} else {
			updateState(getEditorInput());
			validateState(getEditorInput());
			performSave(false, monitor);
		}
	}
	
	/**
	 * Updates the state of the given editor input such as read-only flag.
	 * 
	 * @param input the input to be validated
	 * @since 2.0
	 */
	protected void updateState(IEditorInput input) {
		IDocumentProvider provider= getDocumentProvider();
		if (provider instanceof IDocumentProviderExtension) {
			IDocumentProviderExtension extension= (IDocumentProviderExtension) provider;
			try {
				
				boolean wasReadOnly= isEditorInputReadOnly();
				extension.updateStateCache(input);
				
				if (wasReadOnly != isEditorInputReadOnly())
					updateStateDependentActions();
				
			} catch (CoreException x) {
				Bundle bundle = Platform.getBundle(PlatformUI.PLUGIN_ID);			
				ILog log= Platform.getLog(bundle);
				log.log(x.getStatus());
			}
		}
	}
	
	public boolean isEditable() {
		IDocumentProvider provider= getDocumentProvider();
		if (provider instanceof IDocumentProviderExtension) {
			IDocumentProviderExtension extension= (IDocumentProviderExtension) provider;
			return extension.isModifiable(getEditorInput());
		}
		return false;
	}
	
	/**
	 * Updates all state dependent actions.
	 * @since 2.0
	 */
	protected void updateStateDependentActions() {
	}
	
	/**
	 * The <code>AbstractTextEditor</code> implementation of this 
	 * <code>IEditorPart</code> method calls <code>performSaveAs</code>. 
	 * Subclasses may reimplement.
	 */
	public void doSaveAs() {
		/*
		 * 1GEUSSR: ITPUI:ALL - User should never loose changes made in the editors.
		 * Changed Behavior to make sure that if called inside a regular save (because
		 * of deletion of input element) there is a way to report back to the caller.
		 */
		performSaveAs(getProgressMonitor());
	}
	
	/**
	 * Performs a save as and reports the result state back to the 
	 * given progress monitor. This default implementation does nothing.
	 * Subclasses may reimplement.
	 * 
	 * @param progressMonitor the progress monitor for communicating result state or <code>null</code>
	 */
	protected void performSaveAs(IProgressMonitor progressMonitor) {
	}
	
	/**
	 * Performs the save and handles errors appropriately.
	 * 
	 * @param overwrite indicates whether or not overwriting is allowed
	 * @param progressMonitor the monitor in which to run the operation
	 * @since 3.0
	 */
	protected void performSave(boolean overwrite, IProgressMonitor progressMonitor) {
		
		IDocumentProvider provider= getDocumentProvider();
		if (provider == null)
			return;
		
		try {
		
			provider.aboutToChange(getEditorInput());
			IEditorInput input= getEditorInput();
			provider.saveDocument(progressMonitor, input, getDocumentProvider().getDocument(input), overwrite);
			editorSaved();
		
		} catch (CoreException x) {
			IStatus status= x.getStatus();
			if (status == null || status.getSeverity() != IStatus.CANCEL)
				handleExceptionOnSave(x, progressMonitor);
		} finally {
			provider.changed(getEditorInput());
		}
	}

	/**
	 * Hook which gets called when the editor has been saved.
	 * Subclasses may extend.
	 * @since 2.1
	 */
	protected void editorSaved() {
		IWorkbenchPage page= getEditorSite().getPage();
		INavigationLocation[] locations= page.getNavigationHistory().getLocations();
		IEditorInput input= getEditorInput();		
		for (int i= 0; i < locations.length; i++) {
			if (locations[i] instanceof TextSelectionNavigationLocation) {
				if(input.equals(locations[i].getInput())) {
					TextSelectionNavigationLocation location= (TextSelectionNavigationLocation) locations[i];
					location.partSaved(this);
				}
			}
		}
	}
	
	/**
	 * Handles the given exception. If the exception reports an out-of-sync
	 * situation, this is reported to the user. Otherwise, the exception
	 * is generically reported.
	 * 
	 * @param exception the exception to handle
	 * @param progressMonitor the progress monitor
	 */
	protected void handleExceptionOnSave(CoreException exception, IProgressMonitor progressMonitor) {
		
		try {
			++ fErrorCorrectionOnSave;
			
			Shell shell= getSite().getShell();
			
			boolean isSynchronized= false;
			IDocumentProvider p= getDocumentProvider();
			
			if (p instanceof IDocumentProviderExtension3)  {
				IDocumentProviderExtension3 p3= (IDocumentProviderExtension3) p;
				isSynchronized= p3.isSynchronized(getEditorInput());
			} else  {
				long modifiedStamp= p.getModificationStamp(getEditorInput());
				long synchStamp= p.getSynchronizationStamp(getEditorInput());
				isSynchronized= (modifiedStamp == synchStamp);
			}
			
			if (fErrorCorrectionOnSave == 1 && !isSynchronized) {
				
				String title= Messages.getMessage("Editor.error.save.outofsync.title"); //$NON-NLS-1$
				String msg= Messages.getMessage("Editor.error.save.outofsync.message"); //$NON-NLS-1$
				
				if (MessageDialog.openQuestion(shell, title, msg))
					performSave(true, progressMonitor);
				else {
					/*
					 * 1GEUPKR: ITPJUI:ALL - Loosing work with simultaneous edits
					 * Set progress monitor to canceled in order to report back 
					 * to enclosing operations. 
					 */
					if (progressMonitor != null)
						progressMonitor.setCanceled(true);
				}
			} else {
				
				String title= Messages.getMessage("Editor.error.save.title"); //$NON-NLS-1$
				String msg= Messages.getMessage("Editor.error.save.message"); //$NON-NLS-1$
				ErrorDialog.openError(shell, title, msg, exception.getStatus());
				
				/*
				 * 1GEUPKR: ITPJUI:ALL - Loosing work with simultaneous edits
				 * Set progress monitor to canceled in order to report back 
				 * to enclosing operations. 
				 */
				if (progressMonitor != null)
					progressMonitor.setCanceled(true);
			}
			
		} finally {
			-- fErrorCorrectionOnSave;
		}
	}
	

	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {

		setSite(site);
		
		IWorkbenchWindow window= getSite().getWorkbenchWindow();
		internalInit(window, site, input);
	}
	
	/**
	 * Implements the <code>init</code> method of <code>IEditorPart</code>. 
	 * Subclasses replacing <code>init</code> may choose to call this method in
	 * their implementation.
	 * 
	 * @param window the site's workbench window
	 * @param site the editor's site
	 * @param input the editor input for the editor being created
	 * @throws PartInitException if {@link #doSetInput(IEditorInput)} fails or gets canceled
	 * 
	 * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 * @since 2.1
	 */
	protected final void internalInit(IWorkbenchWindow window, final IEditorSite site, final IEditorInput input) throws PartInitException {
		
		IRunnableWithProgress runnable= new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				try {
					
					if (getDocumentProvider() instanceof IDocumentProviderExtension2) {
						IDocumentProviderExtension2 extension= (IDocumentProviderExtension2) getDocumentProvider();
						extension.setProgressMonitor(monitor);
					}
				} finally {
					if (getDocumentProvider() instanceof IDocumentProviderExtension2) {
						IDocumentProviderExtension2 extension= (IDocumentProviderExtension2) getDocumentProvider();
						extension.setProgressMonitor(null);
					}
				}
			}
		};
					
		try {
			window.run(false, true, runnable);
		} catch (InterruptedException x) {
		} catch (InvocationTargetException x) {
			Throwable t= x.getTargetException();
			if (t instanceof CoreException)
				throw new PartInitException(((CoreException) t).getStatus());
			throw new PartInitException(new Status(IStatus.ERROR, TextEditorPlugin.PLUGIN_ID, IStatus.OK, Messages.getMessage("Editor.error.init"), t)); //$NON-NLS-1$
		}
	}
	
	public boolean isDirty() {
		
		IDocumentProvider p= getDocumentProvider();
		return p == null ? false : p.canSaveDocument(getEditorInput());
	}
	
	public boolean isSaveAsAllowed() {

		return false;
	}
	
	public void createPartControl(Composite parent) {
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		parent.setLayout(gridLayout);

		ToolBar toolbar = new ToolBar(parent, SWT.FLAT);
		
		ToolItem itemZoomin = new ToolItem(toolbar, SWT.FLAT);
		itemZoomin.setText(Messages.getMessage("item_zoomin"));
		itemZoomin.setImage(SharedImages.getImage(SharedImages.IMAGE_ZOOMIN));
		itemZoomin.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				imageCanvas.zoomIn();
			}
		});

		ToolItem itemZoomout = new ToolItem(toolbar, SWT.FLAT);
		itemZoomout.setText(Messages.getMessage("item_zoomout"));
		itemZoomout.setImage(SharedImages.getImage(SharedImages.IMAGE_ZOOMOUT));
		itemZoomout.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				imageCanvas.zoomOut();
			}
		});

		ToolItem itemRotate = new ToolItem(toolbar, SWT.FLAT);
		itemRotate.setText(Messages.getMessage("item_rotate"));
		itemRotate.setImage(SharedImages.getImage(SharedImages.IMAGE_ROTATE));
		itemRotate.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				/* rotate image anti-clockwise */
				ImageData src=imageCanvas.getImageData();
				if(src==null) return;
				PaletteData srcPal=src.palette;
				PaletteData destPal;
				ImageData dest;
				/* construct a new ImageData */
				if(srcPal.isDirect){
					destPal=new PaletteData(srcPal.redMask,srcPal.greenMask,srcPal.blueMask);
				}else{
					destPal=new PaletteData(srcPal.getRGBs());
				}
				dest=new ImageData(src.height,src.width,src.depth,destPal);
				/* rotate by rearranging the pixels */
				for(int i=0;i<src.width;i++){
					for(int j=0;j<src.height;j++){
						int pixel=src.getPixel(i,j);
						dest.setPixel(j,src.width-1-i,pixel);
					}
				}
				imageCanvas.setImageData(dest);
			}
		});
		
		ToolItem itemFit = new ToolItem(toolbar, SWT.FLAT);
		itemFit.setText(Messages.getMessage("item_fit"));
		itemFit.setImage(SharedImages.getImage(SharedImages.IMAGE_FIT));
		itemFit.setDisabledImage(SharedImages.getImage(SharedImages.IMAGE_FIT_DISABLED));
		itemFit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				imageCanvas.fitCanvas();
			}
		});
		
		ToolItem itemOriginal = new ToolItem(toolbar, SWT.FLAT);
		itemOriginal.setText(Messages.getMessage("item_original"));
		itemOriginal.setImage(SharedImages.getImage(SharedImages.IMAGE_ORIGINAL));
		itemOriginal.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				imageCanvas.showOriginal();
			}
		});
		
		imageCanvas=new SWTImageCanvas(parent);
		
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		imageCanvas.setLayoutData(data);
		
		loadImageContent();
	}
	
	/**
	 * 
	 */
	private void loadImageContent() {
		
		final Node node = (Node)modelObject;
		String repositoryId = node.getRepository();
		
		Repository repository = RepositoryRegistry.getInstance().getRepository(repositoryId);
		final Ticket ticket = repository.getTicket();
		ServerProfile serverProfile = repository.getServerProfile();
		final RepositoryService service = 
			JLibraryServiceFactory.getInstance(serverProfile).getRepositoryService();
		
		InputStream canvasIs = null;
		try {

			// First check if the document is cached
			LocalCache cache = LocalCacheService.getInstance().getLocalCache();
			
			if (cache.isNodeCached(node)) {
				canvasIs = cache.getNodeContent(node);
				imageCanvas.loadImage(canvasIs);
			} else {
				cache.addNodeToCache(node,new NodeContentHandler() {
					public void copyTo(OutputStream os) throws LocalCacheException {
						
						try {
							service.loadDocumentContent(node.getId(),ticket,os);
						} catch (Exception e) {
							logger.error(e.getMessage(),e);
							throw new LocalCacheException(e);
						}
					}
				});
				canvasIs = cache.getNodeContent(node);
				imageCanvas.loadImage(canvasIs);
			}
		} catch (final Exception e) {
			
            logger.error(e.getMessage(),e);
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					ErrorDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							"ERROR",
							e.getMessage(),
							new Status(IStatus.ERROR,"JLibrary",101,e.getMessage(),e));		
				}
			});			
		} finally {
			if (canvasIs != null) {
				try {
					canvasIs.close();
				} catch (IOException e) {
					logger.error(e.getMessage(),e);
				}
			}
		}
	}

	public void setFocus() {

		imageCanvas.setFocus();
	}
	
	public IDocumentProvider getDocumentProvider() {
		
		return fExplicitDocumentProvider;
	}
	
	/**
	 * Sets this editor's document provider. This method must be 
	 * called before the editor's control is created.
	 *
	 * @param provider the document provider
	 */
	protected void setDocumentProvider(IDocumentProvider provider) {
		Assert.isNotNull(provider);
		fExplicitDocumentProvider= provider;
	}
	
	/**
	 * Disposes of the connection with the document provider. Subclasses
	 * may extend.
	 * 
	 * @since 3.0
	 */
	protected void disposeDocumentProvider() {
		IDocumentProvider provider= getDocumentProvider();
		if (provider != null) {
			
			IEditorInput input= getEditorInput();
			if (input != null)
				provider.disconnect(input);
			
			fExplicitDocumentProvider= null;
		}
	}
	
	/**
	 * Returns the progress monitor related to this editor. It should not be
	 * necessary to extend this method.
	 * 
	 * @return the progress monitor related to this editor
	 * @since 2.1
	 */
	protected IProgressMonitor getProgressMonitor() {
		
		IProgressMonitor pm= null;
		
		IStatusLineManager manager= getStatusLineManager();
		if (manager != null)
			pm= manager.getProgressMonitor();
			
		return pm != null ? pm : new NullProgressMonitor();
	}
	
	/**
	 * Returns the status line manager of this editor.
	 * @return the status line manager of this editor
	 * @since 2.0
	 */
	private IStatusLineManager getStatusLineManager() {

		IEditorActionBarContributor contributor= getEditorSite().getActionBarContributor();		
		if (!(contributor instanceof EditorActionBarContributor))
			return null;
			
		IActionBars actionBars= ((EditorActionBarContributor) contributor).getActionBars();
		if (actionBars == null)
			return null;
			
		return actionBars.getStatusLineManager();
	}
	
	/*
	 * @see ITextEditorExtension#isEditorInputReadOnly()
	 * @since 2.0
	 */
	public boolean isEditorInputReadOnly() {
		IDocumentProvider provider= getDocumentProvider();
		if (provider instanceof IDocumentProviderExtension) {
			IDocumentProviderExtension extension= (IDocumentProviderExtension) provider;
			return extension.isReadOnly(getEditorInput());
		}
		return true;
	}
	
	/*
	 * @see ITextEditorExtension2#isEditorInputModifiable()
	 * @since 2.1
	 */
	public boolean isEditorInputModifiable() {
		IDocumentProvider provider= getDocumentProvider();
		if (provider instanceof IDocumentProviderExtension) {
			IDocumentProviderExtension extension= (IDocumentProviderExtension) provider;
			return extension.isModifiable(getEditorInput());
		}
		return true;
	}
	
	/**
	 * Validates the state of the given editor input. The predominate intent
	 * of this method is to take any action probably necessary to ensure that
	 * the input can persistently be changed.
	 * 
	 * @param input the input to be validated
	 * @since 2.0
	 */
	protected void validateState(IEditorInput input) {		
		
		IDocumentProvider provider= getDocumentProvider();
		if (! (provider instanceof IDocumentProviderExtension))
			return;
			
		IDocumentProviderExtension extension= (IDocumentProviderExtension) provider;	
				
		try {
			
			extension.validateState(input, getSite().getShell());	
			
		} catch (CoreException x) {
			IStatus status= x.getStatus();
			if (status == null || status.getSeverity() != IStatus.CANCEL) {
				Bundle bundle = Platform.getBundle(PlatformUI.PLUGIN_ID);			
				ILog log= Platform.getLog(bundle);
				log.log(x.getStatus());
				
				Shell shell= getSite().getShell();
				String title= Messages.getMessage("Editor.error.validateEdit.title"); //$NON-NLS-1$
				String msg= Messages.getMessage("Editor.error.validateEdit.message"); //$NON-NLS-1$			
				ErrorDialog.openError(shell, title, msg, x.getStatus());
			}
			return;
		}

		updateStateDependentActions();
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose() {
		
		super.dispose();
		
		if (fActions != null) {
			fActions.clear();
			fActions= null;
		}
		
		imageCanvas.dispose();
	}
}
