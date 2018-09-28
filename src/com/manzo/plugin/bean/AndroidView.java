package com.manzo.plugin.bean;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class AndroidView {

    private String id;
    private String name;
    private String mFildName;
    private PsiElement xmlTarget;
    public Boolean isChoice = true;
    public Boolean isClick = true;

    public AndroidView(@NotNull String id, @NotNull String className, PsiElement xmlTarget) {
        this.xmlTarget = xmlTarget;

        if (id.startsWith("@+id/")) {
            this.id = ("R.id." + id.split("@\\+id/")[1]);
        } else if (id.contains(":")) {
            String[] s = id.split(":id/");
            String packageStr = s[0].substring(1, s[0].length());
            this.id = (packageStr + ".R.id." + s[1]);
        }
        if (className.contains("."))
            this.name = className;
        else if ((className.equals("View")) || (className.equals("ViewGroup")))
            this.name = String.format("%s", className);
        else
            this.name = String.format("%s", className);
        mFildName = generFildName();
    }

    public PsiElement getXmlTarget() {
        return xmlTarget;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getFieldName() {
        return mFildName;
    }

    public void setFieldName(String fieldName) {
        mFildName = fieldName;
    }

    private String generFildName() {
        String[] words = id.split("_");
        StringBuilder fieldName = new StringBuilder();
        fieldName.append('m');
        for (String word : words) {
            String[] idTokens = word.split("\\.");
            char[] chars = idTokens[(idTokens.length - 1)].toCharArray();
            if (chars.length > 0) {
                chars[0] = Character.toUpperCase(chars[0]);
            }
            fieldName.append(chars);
        }
        return fieldName.toString();
    }
}