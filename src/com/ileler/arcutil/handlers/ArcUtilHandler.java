package com.ileler.arcutil.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ileler.arcutil.ArcUtil;

public class ArcUtilHandler extends AbstractHandler {
    
    private static final String CFG_NAME = ".arcconfig";

	public ArcUtilHandler() {}

	public Object execute(ExecutionEvent event) throws ExecutionException {  
	    IWorkbenchWindow window = HandlerUtil  
	            .getActiveWorkbenchWindowChecked(event);  
	    ISelection sel = window.getSelectionService().getSelection();  
	    if (sel instanceof IStructuredSelection) {
	        Object obj = ((IStructuredSelection) sel).getFirstElement();  
	        String firstSelPath = null;  

	        if (obj instanceof IResource) {  
	            firstSelPath = ((IResource) obj).getLocation().toOSString();  
	        }  
	        if (firstSelPath != null) {  
	            try {  
	                String cfgPath = getCfgPath(new File(firstSelPath));
	                if (cfgPath == null) {
	                    MessageDialog.openInformation(Display.getDefault().getActiveShell(), "ArcUtil", "not found ["+CFG_NAME+"].");
	                    return null;
	                }
	                
	                List<String> paths = new ArrayList<>();
	                List<?> list = ((IStructuredSelection) sel).toList();
	                for (Object _sel : list) {
	                    String path = null;
	                    if (_sel instanceof IResource) {
	                        path = ((IResource) _sel).getLocation().toOSString();
	                    } else if (_sel instanceof IJavaElement) {
	                        path = ((IJavaElement) _sel).getResource().getLocation().toOSString();
	                    } else {
	                        continue;
	                    }
	                    if (path != null && !path.equals(cfgPath) && path.startsWith(cfgPath)) {
	                        paths.add(path);
	                    }
	                }
	                
	                if (paths.size() < 1) {
	                    MessageDialog.openInformation(Display.getDefault().getActiveShell(), "ArcUtil", "pre commit paths invalid.");
                        return null;
	                }
	                
                    MessageDialog.openInformation(Display.getDefault().getActiveShell(), "ArcUtil", "done["+(new ArcUtil(cfgPath, paths).exec())+"].");
	            } catch (Exception e) {  
	                MessageDialog.openError(Display.getDefault().getActiveShell(), "ArcUtil", e.getMessage());
	            }  
	        } else {
	            MessageDialog.openInformation(Display.getDefault().getActiveShell(), "ArcUtil", "selected item invalid.");
	        } 
	  
	    } else {
	        MessageDialog.openInformation(Display.getDefault().getActiveShell(), "ArcUtil", "param invalid.");
	    } 
	    return null;  
	}  
	
	private String getCfgPath(File selected) {
	    if (selected == null || !selected.exists())    return null;
	    if (selected.isDirectory()) {
	        File cfgFile = new File(selected.getPath() + File.separator + CFG_NAME);
	        if (cfgFile.exists()) {
	            return cfgFile.getParent();
	        } else {
	            return getCfgPath(selected.getParentFile());
	        }
	    } else {
	        return getCfgPath(selected.getParentFile());
	    }
	}
	
}
