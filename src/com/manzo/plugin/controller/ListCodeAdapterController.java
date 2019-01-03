package com.manzo.plugin.controller;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.manzo.plugin.templateconfig.TemplateConfig;
import com.manzo.plugin.utils.JavaCommonUtils;

/**
 * Created by shenjianlin on 2018/12/12.
 * 控制通用adapter生成逻辑控制器
 */
public class ListCodeAdapterController {
    private static final String BASE_ADAPTER = "CommonListAdapter";
    private static final String BASE_HOLDER = "BaseViewHolder";
    private static final String ADAPTER_INTERFACE = "AdapterRefreshInterface";
    private static final String VIEW_HANDLER = "ViewHandler";
    private static final String INJECT_VIEW = "InjectView";
    private static final String COMMON_BEAN = "CommonBean";

    public static void generateAdapterCode(String className, String beanName, AnActionEvent anActionEvent) {
        Project fatherProject = anActionEvent.getProject();
        if (fatherProject == null) {
            return;
        }
        //获取所选的目录，即需要添加类的的包路径file
        VirtualFile virtualFile = JavaCommonUtils.getSelectVirtualFile(anActionEvent);
        if (virtualFile == null || !virtualFile.isDirectory()) {
            return;
        }
        //通过所选文件，获取包的directory
        PsiDirectory directory = PsiDirectoryFactory.getInstance(fatherProject).createDirectory(virtualFile);
        createListAdapter(fatherProject, directory, className, beanName);
    }

    private static void createListAdapter(Project fatherProject, PsiDirectory directory, String className, String beanName) {
        boolean commonIsCreate = false;
        //检查是否要创建bean
        PsiClass beanClass = JavaCommonUtils.classHasCreated(fatherProject, beanName);
        if (beanClass == null) {
            JavaDirectoryService.getInstance().createClass(directory, beanName, TemplateConfig.BEAN_PLATE);
        }
        PsiDirectory baseDir; //装填base的父目录

        //是否创建BaseViewHolder
        PsiClass baseViewHolderClass = JavaCommonUtils.classHasCreated(fatherProject, BASE_HOLDER);
        if (baseViewHolderClass == null) {
            PsiDirectory parentDir = directory.getParent();
            if (parentDir == null || JavaDirectoryService.getInstance().isSourceRoot(parentDir)) {
                baseDir = JavaCommonUtils.createOrFindDir(directory, "base");
            } else {
                baseDir = JavaCommonUtils.createOrFindDir(parentDir, "base");
            }
            JavaDirectoryService.getInstance().createClass(baseDir, BASE_HOLDER, TemplateConfig.BASE_ADAPTER_VIEW_HOLDER);
        } else {
            baseDir = PsiDirectoryFactory.getInstance(fatherProject).
                    createDirectory(baseViewHolderClass.getContainingFile().getVirtualFile().getParent());
        }
        //是否创建AdapterRefreshInterface
        PsiClass refreshInterfaceClass = JavaCommonUtils.classHasCreated(fatherProject, ADAPTER_INTERFACE);
        if (refreshInterfaceClass == null) {
            JavaDirectoryService.getInstance().createClass(baseDir, ADAPTER_INTERFACE, TemplateConfig.ADATPER_INTERFACE);
        }
        //目标adapter
        PsiClass selfClass = TemplateConfig.createAdapterByTemplate(directory, className, beanName);
        PsiClass commonLIstAdapterClass = JavaCommonUtils.classHasCreated(fatherProject, BASE_ADAPTER);
        if (commonLIstAdapterClass == null) {
            commonIsCreate = true;
            //当IView不存在是，查询是否有base目录，不存在则在父目录创建base目录，装填IView。
            commonLIstAdapterClass = JavaDirectoryService.getInstance().createClass(baseDir, BASE_ADAPTER, TemplateConfig.BASE_COMMON_ADAPTER_PLATE);
            //导入baseViewHolder
            JavaCommonUtils.importSelfProjectPackageRunWriteAction(commonLIstAdapterClass, fatherProject, BASE_HOLDER);
            //导入AdapterRefreshInterface
            JavaCommonUtils.importSelfProjectPackageRunWriteAction(commonLIstAdapterClass, fatherProject, ADAPTER_INTERFACE);
        }
        PsiClass injectClass = JavaCommonUtils.classHasCreated(fatherProject, INJECT_VIEW);
        if (injectClass == null) {
            JavaDirectoryService.getInstance().createClass(baseDir, INJECT_VIEW, TemplateConfig.ADAPTER_INJECT);
        }

        //是否需要创建viewhandler
        PsiClass viewHandlerClass = JavaCommonUtils.classHasCreated(fatherProject, VIEW_HANDLER);
        if (viewHandlerClass == null) {
            viewHandlerClass = JavaDirectoryService.getInstance().createClass(baseDir, VIEW_HANDLER, TemplateConfig.ADAPTER_VIEW_HOLDER);
            JavaCommonUtils.importSelfProjectPackageRunWriteAction(viewHandlerClass, fatherProject, BASE_ADAPTER);
            JavaCommonUtils.importSelfProjectPackageRunWriteAction(viewHandlerClass, fatherProject, BASE_HOLDER);
            JavaCommonUtils.importSelfProjectPackageRunWriteAction(viewHandlerClass, fatherProject, INJECT_VIEW);
        }

        if (commonIsCreate) {
            JavaCommonUtils.importSelfProjectPackageRunWriteAction(commonLIstAdapterClass, fatherProject, VIEW_HANDLER);
        }

        JavaCommonUtils.importSelfProjectPackageRunWriteAction(selfClass, fatherProject, BASE_ADAPTER);
        JavaCommonUtils.importSelfProjectPackageRunWriteAction(selfClass, fatherProject, BASE_HOLDER);
        JavaCommonUtils.importSelfProjectPackageRunWriteAction(selfClass, fatherProject, COMMON_BEAN);
    }

}
