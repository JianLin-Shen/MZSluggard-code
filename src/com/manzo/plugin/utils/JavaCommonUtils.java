package com.manzo.plugin.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

/**
 * Created by shenjianlin on 2018/8/26.
 */
public class JavaCommonUtils {
    /**
     * @param currentClass    当前操作的类
     * @param project         当前工程
     * @param importClassName 需要被添加的类
     */
    public static void importAndroidPackage(PsiClass currentClass, Project project, String importClassName) {
        try {
            PsiClass[] importClasses = PsiShortNamesCache.getInstance(project).getClassesByName(importClassName, GlobalSearchScope.allScope(project));
            PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
            PsiClass tPsiClass = null;
            for (PsiClass mPsiClass : importClasses) {
                String nameStr = mPsiClass.getQualifiedName();
                if (StringUtils.isBlank(nameStr)) {
                    continue;
                }
                if (nameStr.contains("android")) {
                    tPsiClass = mPsiClass;
                    break;
                }
            }
            if (tPsiClass == null) {
                return;
            }
            PsiImportStatement importStatement = elementFactory.createImportStatement(tPsiClass);
            ((PsiJavaFile) currentClass.getContainingFile()).getImportList().add(importStatement);
        } catch (Exception e) {
            //do noting
        }
    }

    public static void importSelProjectPackage(PsiClass currentClass, Project project, String importClassName) {
        try {
            PsiClass[] importClasses = PsiShortNamesCache.getInstance(project).getClassesByName(importClassName, GlobalSearchScope.allScope(project));
            PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
            PsiClass tPsiClass = null;
            for (PsiClass mPsiClass : importClasses) {
                String nameStr = mPsiClass.getQualifiedName();
                if (StringUtils.isBlank(nameStr)) {
                    continue;
                }
                tPsiClass = mPsiClass;
            }
            if (tPsiClass == null) {
                return;
            }
            PsiImportStatement importStatement = elementFactory.createImportStatement(tPsiClass);
            ((PsiJavaFile) currentClass.getContainingFile()).getImportList().add(importStatement);
        } catch (Exception e) {
            //do noting
        }
    }

    public static  @Nullable VirtualFile getSelectVirtualFile(AnActionEvent e) {
        return CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
    }
}
