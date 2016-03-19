package org.braheem.nbmodules;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.editor.mimelookup.MimeRegistrations;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.spi.editor.document.OnSaveTask;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.*;
import org.openide.windows.TopComponent;

/**
 * @author Abraham Al-Dabbagh
 */
@MimeRegistrations({
    @MimeRegistration(mimeType = "", service = OnSaveTask.Factory.class, position = 1500)
})
public class AntTargetOnSave implements OnSaveTask.Factory {
    
    @Override
    public OnSaveTask createTask(OnSaveTask.Context ctx)
    {
        return new OnSaveTask()
        {
            Lookup lookup;
            Project project;
            FileObject fileObj;
            FileObject projectDir;
            
            @Override
            public void performTask()
            {
                Logger.getLogger(AntTargetOnSave.class.getName()).log(Level.INFO, 
                        "AntTargetOnSave -- BEGIN");
                try
                {
                    lookup = Utilities.actionsGlobalContext();
                    project = lookup.lookup(Project.class);
                    
                    //if no project is explicitly selected when Save event occurs
                    if (project == null)
                    {
                        //get currently open and active file in IDE
                        TopComponent activeTC = TopComponent.getRegistry().getActivated();
                        DataObject dataLookup = activeTC.getLookup().lookup(DataObject.class);
                        FileObject openFile = dataLookup.getPrimaryFile();
                        
                        //get all open projects in IDE
                        Project[] projects = OpenProjects.getDefault().getOpenProjects();
                        projectDir = matchProjectFolderToFile(projects, openFile);
                    }
                    else
                    {
                        projectDir = project.getProjectDirectory();
                    }
                    
                    if (projectDir == null)
                    {
                        Logger.getLogger(AntTargetOnSave.class.getName()).log(Level.INFO, 
                        "AntTargetOnSave -- Unable to locate project directory, exiting...");
                        return;
                    }
                    fileObj = projectDir.getFileObject("build.xml");
                    if (fileObj == null)
                    {
                        Logger.getLogger(AntTargetOnSave.class.getName()).log(Level.INFO, 
                        "AntTargetOnSave -- Unable to find ant build.xml in project, exiting...");
                        return;
                    }
                    ActionUtils.runTarget(fileObj, new String[]{"executeOnIDESave"}, null);
                }
                catch(Exception e) 
                {
                    Logger.getLogger(AntTargetOnSave.class.getName()).log(Level.SEVERE, 
                            "AntTargetOnSave -- END 1", e);
                    return;
                }
                Logger.getLogger(AntTargetOnSave.class.getName()).log(Level.INFO, 
                        "AntTargetOnSave -- END");
            }
            
            @Override
            public void runLocked(Runnable r)
            {
                r.run();
            }
            
            @Override
            public boolean cancel()
            {
                return true;
            }
            
            public FileObject matchProjectFolderToFile(Project[] projects, FileObject file)
            {
                FileObject matchingProject = null;
                for (Project proj : projects)
                {
                    FileObject projDir = proj.getProjectDirectory();
                    //check if file belongs to this open project
                    if(FileUtil.isParentOf(projDir, file))
                    {
                        matchingProject = projDir;
                        break;
                    }
                }
                return matchingProject;
            }
            
        };
    }
}
