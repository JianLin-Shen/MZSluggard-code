package com.manzo.plugin.controller;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.DocumentUtil;
import com.intellij.util.IncorrectOperationException;
import com.manzo.plugin.bean.AndroidView;
import com.manzo.plugin.utils.AndroidUtils;
import com.manzo.plugin.utils.CodeStyleProcessor;
import com.manzo.plugin.utils.JavaCommonUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by shenjianlin on 2018/9/4.
 */
public class SimpleFileController {

    public static void loadFile(@NotNull Project project, Editor editor, PsiFile currentFile) throws IncorrectOperationException {

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

                PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
                JavaCommonUtils.importAndroidPackage(psiClass, project, "View");
                String clickStr = "private View.OnClickListener mOnClickListener = new View.OnClickListener() {" +
                        "        @Override\n" +
                        "        public void onClick(View v) {\n" +
                        "            switch (v.getId()) {\n" +
                        "                ";
                for (AndroidView v : androidViews) {
                    if (!fieldSet.contains(v.getFieldName())) {
                        String sb = "private " + v.getName() + " " + v.getFieldName() + ";";
                        psiClass.add(elementFactory.createFieldFromText(sb, psiClass));
                        JavaCommonUtils.importAndroidPackage(psiClass, project, v.getName());
                    }
                    if (!thisSet.contains(v.getFieldName())) {
                        clickStr = clickStr + "case " + v.getId() + ":\n\nbreak;";
                        String sb1;
                        sb1 = String.format("%s = (%s) findViewById(%s);", v.getFieldName(), v.getName(), v.getId());
                        PsiStatement statementFromText = elementFactory.createStatementFromText(sb1, null);
                        psiStatement.getParent().addAfter(statementFromText, psiStatement);
                        String sb2;
                        sb2 = String.format("%s.setOnClickListener(%s);", v.getFieldName(), "mOnClickListener");
                        PsiStatement clickStrStatement = elementFactory.createStatementFromText(sb2, null);
                        psiStatement.getParent().addAfter(clickStrStatement, psiStatement);

                    }

                }
                clickStr = clickStr + "default:\n" +
                        "                        break;\n" +
                        "            }\n" +
                        "        }\n" +
                        "    };";
                System.out.print(clickStr);
                PsiStatement clickText = elementFactory.createStatementFromText(clickStr, null);
                psiStatement.getParent().addAfter(clickText, psiElement);
                JavaCodeStyleManager.getInstance(project).shortenClassReferences(psiElement);
                JavaCodeStyleManager.getInstance(project).shortenClassReferences(psiStatement);
                new CodeStyleProcessor(project, psiElement.getContainingFile(), true).run();

            }
        });

    }
}
