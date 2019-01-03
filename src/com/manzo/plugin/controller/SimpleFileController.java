package com.manzo.plugin.controller;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.DocumentUtil;
import com.intellij.util.IncorrectOperationException;
import com.manzo.plugin.bean.AndroidView;
import com.manzo.plugin.dialog.SimpleFormatSelectDialog;
import com.manzo.plugin.utils.AndroidUtils;
import com.manzo.plugin.utils.JavaCommonUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by shenjianlin on 2018/9/4.
 * 控制生成activity中导入控件逻辑控制器
 */
public class SimpleFileController {

    public static void loadFileByDialog(@NotNull Project project, Editor editor, PsiFile currentFile) throws IncorrectOperationException {

        DocumentUtil.writeInRunUndoTransparentAction(new Runnable() {
            @Override
            public void run() {
                SimpleFormatSelectDialog.showDialog(project, editor, currentFile);
            }
        });

    }

    public static void loadFile(@NotNull Project project, Editor editor, PsiFile currentFile, List<AndroidView> androidViews) throws IncorrectOperationException {
        int offset = editor.getCaretModel().getOffset();
        //获取到正在编辑器中编辑的方法体。
        PsiElement psiElement = currentFile.findElementAt(offset);
        PsiStatement psiStatement = PsiTreeUtil.getParentOfType(psiElement, PsiStatement.class);
        //获取当前操作的java类
        PsiClass psiClass = PsiTreeUtil.getParentOfType(psiElement, PsiClass.class);
        //获取原来的成变量
        Set<String> fieldSet = new HashSet<String>();
        for (PsiField field : psiClass.getFields()) {
            fieldSet.add(field.getName());
        }

        Set<String> methodSet = new HashSet<>();
        for (PsiMethod method : psiClass.getMethods()) {
            methodSet.add(method.getName());
        }

        final Set<String> thisSet = new HashSet<String>();
        processElement(psiElement, thisSet);

        //获取当前工程的操作factory，要靠这个对象来进行添加对象的创建
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
        String initViewMethodStr = "private void initView(){}";
        PsiMethod initViewMethod = elementFactory.createMethodFromText(initViewMethodStr, psiClass);
        PsiStatement initViewUseStatement = elementFactory.createStatementFromText("initView();", psiStatement);
        PsiMethod choiceMethod = PsiTreeUtil.getParentOfType(psiStatement, PsiMethod.class);
        if (choiceMethod != null) {
            choiceMethod.getBody().add(initViewUseStatement);
        }
//                psiStatement.add(initViewUseStatement);
        //导入android.view包
        JavaCommonUtils.importAndroidPackage(psiClass, project, "View");

        String clickStr = "private View.OnClickListener mOnClickListener = new View.OnClickListener() {" +
                "        @Override\n" +
                "        public void onClick(View v) {\n" +
                "            switch (v.getId()) {\n" +
                "                ";
        //遍历每个操作view，生成对应需要的代码。内部拼装方式，很好懂
        for (AndroidView v : androidViews) {
            if (!v.isChoice) {
                continue;
            }
            if (!fieldSet.contains(v.getFieldName())) {
                String sb = "private " + v.getName() + " " + v.getFieldName() + ";";
                psiClass.add(elementFactory.createFieldFromText(sb, psiClass));
                JavaCommonUtils.importAndroidPackage(psiClass, project, v.getName());
                fieldSet.add(v.getFieldName());
            }
            if (!thisSet.contains(v.getFieldName())) {
                String sb1;
                sb1 = String.format("%s = (%s) findViewById(%s);", v.getFieldName(), v.getName(), v.getId());
                PsiStatement statementFromText = elementFactory.createStatementFromText(sb1, null);
                initViewMethod.getBody().add(statementFromText);
            }

        }
        boolean hasClick = false;
        for (AndroidView tv : androidViews) {
            if (!thisSet.contains(tv.getFieldName()) && tv.isClick && fieldSet.contains(tv.getFieldName())) {
                clickStr = clickStr + "case " + tv.getId() + ":\n\nbreak;";
                hasClick = true;
                String sb2;
                sb2 = String.format("%s.setOnClickListener(%s);", tv.getFieldName(), "mOnClickListener");
                PsiStatement clickStrStatement = elementFactory.createStatementFromText(sb2, null);
                initViewMethod.getBody().add(clickStrStatement);

            }

        }
        clickStr = clickStr + "default:\n" +
                "                        break;\n" +
                "            }\n" +
                "        }\n" +
                "    };";
        System.out.print(clickStr);
        //把拼装好的点击事件的对象添加到类中。
        if (!fieldSet.contains("mOnClickListener") && hasClick) {
            PsiField clickText = elementFactory.createFieldFromText(clickStr, psiClass);
            psiClass.add(clickText);
        }

        if (!methodSet.contains("initView")) {
            psiClass.add(initViewMethod);
        }
        //CodeStyle
        CodeStyleManager.getInstance(project).reformat(psiClass.getParent());
    }

    /**
     * TODO Dialog 可配置ViewHolder方法。
     *
     * @param project
     * @param editor
     * @param currentFile
     * @throws IncorrectOperationException
     */
    public static void loadFileToAdapter(@NotNull Project project, Editor editor, PsiFile currentFile) throws IncorrectOperationException {

        DocumentUtil.writeInRunUndoTransparentAction(new Runnable() {
            @Override
            public void run() {
                String layoutName = editor.getSelectionModel().getSelectedText();
                PsiFile xmlFile = AndroidUtils.findXmlResource(project, layoutName);
                if (xmlFile == null) {
                    return;
                }
                List<AndroidView> androidViews = AndroidUtils.getIDsFromXML(xmlFile);
                int offset = editor.getCaretModel().getOffset();
                PsiElement psiElement = currentFile.findElementAt(offset);
                PsiStatement psiStatement = PsiTreeUtil.getParentOfType(psiElement, PsiStatement.class);
                // collection class field
                // check if we need to set them
                PsiClass psiClass = PsiTreeUtil.getParentOfType(psiElement, PsiClass.class);
                Set<String> fieldSet = new HashSet<String>();
                for (PsiField field : psiClass.getFields()) {
                    fieldSet.add(field.getName());
                }

                // collect this.foo = "" and (this.)foo = ""
                // collection already init variables
                final Set<String> thisSet = new HashSet<String>();
                processElement(psiElement, thisSet);
                PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
                JavaCommonUtils.importAndroidPackage(psiClass, project, "View");
                PsiClass innerClass = psiClass.findInnerClassByName("ViewHolder", true);
                JavaCommonUtils.importSelProjectPackage(psiClass, project, "InjectView");
                for (AndroidView v : androidViews) {
                    if (!fieldSet.contains(v.getFieldName())) {
                        String injectViewStr = String.format("@InjectView(%s)\n", v.getId());
                        String sb = "private " + v.getName() + " " + v.getFieldName() + ";";
                        String injectStr = injectViewStr + sb;
                        innerClass.add(elementFactory.createFieldFromText(injectStr, innerClass));
                        JavaCommonUtils.importAndroidPackage(psiClass, project, v.getName());
                    }
                }
                CodeStyleManager.getInstance(project).reformat(psiClass.getParent());
            }
        });

    }

    private static void processElement(PsiElement psiElement, final Set<String> thisSet) {
        PsiTreeUtil.processElements(psiElement, new PsiElementProcessor() {

            @Override
            public boolean execute(@NotNull PsiElement element) {

                if (element instanceof PsiThisExpression) {
                    attachFieldName(element.getParent());
                } else if (element instanceof PsiAssignmentExpression) {
                    attachFieldName(((PsiAssignmentExpression) element).getLExpression());
                }

                return true;
            }

            private void attachFieldName(PsiElement psiExpression) {

                if (!(psiExpression instanceof PsiReferenceExpression)) {
                    return;
                }

                PsiElement psiField = ((PsiReferenceExpression) psiExpression).resolve();
                if (psiField instanceof PsiField) {
                    thisSet.add(((PsiField) psiField).getName());
                }
            }
        });
    }
}
