package com.manzo.plugin.controller;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.manzo.plugin.templateconfig.TemplateConfig;
import com.manzo.plugin.utils.JavaCommonUtils;

/**
 * Created by shenjianlin on 2018/9/11.
 * 控制mvp生成逻辑控制器
 */
public class MvpHcbController {

    public static boolean generateMvpCode(String activityName, AnActionEvent anActionEvent) {
        Project fatherProject = anActionEvent.getProject();
        if (fatherProject == null) {
            return false;
        }
        //获取所选的目录，即需要添加类的的包路径file
        VirtualFile virtualFile = JavaCommonUtils.getSelectVirtualFile(anActionEvent);
        if (virtualFile == null || !virtualFile.isDirectory()) {
            return false;
        }
        //通过所选文件，获取包的directory
        PsiDirectory directory = PsiDirectoryFactory.getInstance(fatherProject).createDirectory(virtualFile);
        //添加MVP相关文件
        //如果没有IView则添加IView
        createIViewInterface(activityName, fatherProject, directory);

        return true;
    }

    private static void createIViewInterface(String activityName, Project fatherProject, PsiDirectory directory) {
        String iViewName = "IView";
        String basViewName = "BaseView";
        PsiClass iViewClass = JavaCommonUtils.classHasCreated(fatherProject, iViewName);
        PsiDirectory iViewParentDir; //装填base的父目录
        //判断将要创建的IView是否存在，不存在就需要创建。
        if (iViewClass == null) {
            //当IView不存在是，查询是否有base目录，不存在则在父目录创建base目录，装填IView。
            PsiDirectory parentDir = directory.getParent();
            if (parentDir == null || JavaDirectoryService.getInstance().isSourceRoot(parentDir)) {
                iViewParentDir = createOrFindDir(directory, "base");
            } else {
                iViewParentDir = createOrFindDir(parentDir, "base");
            }
            JavaDirectoryService.getInstance().createInterface(iViewParentDir, iViewName);
        } else {
            //如果存在，则读取装填base的父目录
            iViewParentDir = PsiDirectoryFactory.getInstance(fatherProject).
                    createDirectory(iViewClass.getContainingFile().getVirtualFile().getParent());
        }
        //是否需要创建 BaseView interface
        PsiClass baseViewInterface = JavaCommonUtils.classHasCreated(fatherProject, basViewName);
        if (baseViewInterface == null) {
            TemplateConfig.createInterfaceBySelfTemplate(iViewParentDir, basViewName, iViewName);
        }

        //创建BasePresenter
        if (JavaCommonUtils.classHasCreated(fatherProject, TemplateConfig.BASE_PRESENTER_CLASS) == null) {
            JavaDirectoryService.getInstance().createClass(iViewParentDir, "", TemplateConfig.BASE_PRESENTER);
        }

        //创建activity自己的IView 并codeStyle
        String selfIViewName = activityName + "View";
        PsiClass selfClass = TemplateConfig.createInterfaceBySelfTemplate(directory, selfIViewName, basViewName);
        JavaCommonUtils.importSelfProjectPackageRunWriteAction(selfClass, fatherProject, basViewName);
        CodeStyleManager.getInstance(fatherProject).reformat(selfClass);
        //创建activity自己的presenter
        String selfPresentName = activityName + "Presenter";
        PsiClass selfPresentClass = TemplateConfig.createPresentBySelfTemplate(directory, selfPresentName, selfIViewName);
        JavaCommonUtils.importSelfProjectPackageRunWriteAction(selfPresentClass, fatherProject, TemplateConfig.BASE_PRESENTER_CLASS);
        CodeStyleManager.getInstance(fatherProject).reformat(selfPresentClass);
        //创建activity
        String selfActivity = activityName + "Activity";
        PsiClass selfActivityClass = TemplateConfig.createMVPActivityByTemplate(directory, selfActivity, selfIViewName, selfPresentName);
        CodeStyleManager.getInstance(fatherProject).reformat(selfActivityClass);

    }


    private static PsiDirectory createOrFindDir(PsiDirectory parentDir, String dirName) {
        return parentDir.findSubdirectory(dirName) == null ?
                parentDir.createSubdirectory(dirName) :
                parentDir.findSubdirectory(dirName);
    }
}
