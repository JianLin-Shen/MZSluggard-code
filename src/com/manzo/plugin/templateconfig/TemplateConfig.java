package com.manzo.plugin.templateconfig;

import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shenjianlin on 2018/9/12.
 */
public class TemplateConfig {
    //模板配置属性
    public static final String BASE_PRESENTER = "MVPTemplateBasePresenter";
    public static final String FA_INTERFACE = "FA_INTERFACE";
    public static final String SELF_IVIEW_NAME = "SELF_IVIEW_NAME";
    public static final String SELF_PRESENT_NAME = "SELF_PRESENT_NAME";

    //预置类名称
    public static final String BASE_PRESENTER_CLASS = "BasePresenter";

    /**
     * 通过模板MVPBaseViewInterface 创建Interface
     *
     * @param directory        创建文件的目录
     * @param className        创建文件的名字
     * @param extendsClassName 被继承的interface的名字
     */
    public static PsiClass createInterfaceBySelfTemplate(PsiDirectory directory, String className, String extendsClassName) {
        Map<String, String> params = new HashMap<>();
        params.put(FA_INTERFACE, extendsClassName);
        return JavaDirectoryService.getInstance().createClass(directory, className, "MVPBaseViewInterface", false, params);
    }

    public static PsiClass createPresentBySelfTemplate(PsiDirectory directory, String className, String iViewName) {
        Map<String, String> params = new HashMap<>();
        params.put(TemplateConfig.FA_INTERFACE, iViewName);
        return JavaDirectoryService.getInstance().createClass(directory, className, "MVPTemplateSelfPresenter", false, params);
    }

    public static PsiClass createMVPActivityByTemplate(PsiDirectory directory, String className, String selfIView, String selfPresenter) {
        Map<String, String> params = new HashMap<>();
        params.put(TemplateConfig.SELF_IVIEW_NAME, selfIView);
        params.put(TemplateConfig.SELF_PRESENT_NAME, selfPresenter);
        return JavaDirectoryService.getInstance().createClass(directory, className, "MVPTemplateActivity", false, params);
    }
}
