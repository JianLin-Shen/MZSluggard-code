package com.manzo.plugin.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.manzo.plugin.bean.AndroidView;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;


public class AndroidUtils {

    public static final String ANDROID_SRC_PATH = "src/main/java";
    private AndroidUtils() {
    }

    @Nullable
    public static PsiFile findXmlResource(@Nullable PsiReferenceExpression referenceExpression) {
        if (referenceExpression == null) return null;

        PsiElement firstChild = referenceExpression.getFirstChild();
        if (firstChild == null || !"R.layout".equals(firstChild.getText())) {
            return null;
        }

        PsiElement lastChild = referenceExpression.getLastChild();
        if (lastChild == null) {
            return null;
        }

        String name = String.format("%s.xml", lastChild.getText());
        PsiFile[] foundFiles = FilenameIndex.getFilesByName(referenceExpression.getProject(), name, GlobalSearchScope.allScope(referenceExpression.getProject()));
        if (foundFiles.length <= 0) {
            return null;
        }

        return foundFiles[0];
    }

    /*
    public static PsiFile findXmlResource(Project project, String editNameText) {
        if (project == null) {
            return null;
        }
        if (StringUtils.isBlank(editNameText)) {
            return null;
        }

        String name = String.format("%s.xml", editNameText);
        PsiFile[] foundFiles = FilenameIndex.getFilesByName(project, name, GlobalSearchScope.allScope(project));
        if (foundFiles.length <= 0) {
            return null;
        }

        return foundFiles[0];
    }
*/
    public static List<AndroidView> getProjectViews(Project project) {

        List<AndroidView> androidViews = new ArrayList<AndroidView>();
        for (PsiFile psiFile : getLayoutFiles(project)) {
            androidViews.addAll(getIDsFromXML(psiFile));
        }

        return androidViews;
    }

    public static List<PsiFile> getLayoutFiles(Project project) {

        List<PsiFile> psiFileList = new ArrayList<PsiFile>();

        for (VirtualFile virtualFile : FilenameIndex.getAllFilesByExt(project, "xml")) {
            VirtualFile parent = virtualFile.getParent();
            if (parent != null && "layout".equals(parent.getName())) {
                String relative = VfsUtil.getRelativePath(virtualFile, project.getBaseDir(), '/');
                if (relative != null) {
                    PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
                    if (psiFile != null) {
                        psiFileList.add(psiFile);
                    }
                }
            }
        }

        return psiFileList;
    }

    @Nullable
    public static PsiFile findXmlResource(Project project, String layoutName) {

        String name = String.format("%s.xml", layoutName);
        PsiFile[] foundFiles = FilenameIndex.getFilesByName(project, name, GlobalSearchScope.allScope(project));
        if (foundFiles.length <= 0) {
            return null;
        }

        return foundFiles[0];
    }

    /**
     * AndroidView是自己定义的对象，不用太在意，用来装填数据
     * @param f
     * @return
     */
    @NotNull
    public static List<AndroidView> getIDsFromXML(@NotNull PsiFile f) {
        final List<AndroidView> ret = new LinkedList<>();
        f.accept(new XmlRecursiveElementVisitor() {
            @Override
            public void visitElement(final PsiElement element) {
                super.visitElement(element);
                if (element instanceof XmlTag) {
                    XmlTag t = (XmlTag) element;
                    XmlAttribute id = t.getAttribute("android:id", null);
                    if (id == null) {
                        return;
                    }
                    final String val = id.getValue();
                    if (val == null) {
                        return;
                    }
                    ret.add(new AndroidView(val, t.getName(), id));

                }

            }
        });

        return ret;
    }

    @Nullable
    public static AndroidView getViewType(@NotNull PsiFile f, String findId) {
        List<AndroidView> views = getIDsFromXML(f);
        for (AndroidView view : views) {
            if (findId.equals(view.getId())) {
                return view;
            }
        }
        return null;
    }

    public static void layoutCodeCanLoad(AnActionEvent e){
        Presentation presentation = e.getPresentation();
        Project project = e.getProject();
        if (project == null) {
            presentation.setEnabled(false);
            return;
        }
        Editor editor = CommonDataKeys.EDITOR.getData(e.getDataContext());
        if (editor == null || StringUtils.isBlank(editor.getSelectionModel().getSelectedText())) {
            presentation.setEnabled(false);
            return;
        }
        String layoutName = editor.getSelectionModel().getSelectedText();
        PsiFile xmlFile = AndroidUtils.findXmlResource(project, layoutName);
        if (xmlFile == null) {
            presentation.setEnabled(false);
            return;
        }
        presentation.setEnabled(true);
    }

}
