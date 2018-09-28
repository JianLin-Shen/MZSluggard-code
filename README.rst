
===============================
MZSluggard-code
===============================
.. image:: https://img.shields.io/badge/version-1.0.4-green.svg?style=green
        :target: https://github.com/JianLin-Shen/ManzoAndroidPlugin/releases
        
.. image:: https://img.shields.io/badge/jetbrains-download-green.svg?style=green
        :target: https://plugins.jetbrains.com/plugin/11140-mzsluggard-code
        
.. image:: https://img.shields.io/badge/local-download-green.svg?style=green
        :target: https://github.com/JianLin-Shen/ManzoAndroidPlugin/blob/master/resleaseversion/MZSluggard-code-1.0.4.jar

Android插件，辅助生成代码MZSluggard-code

* 根据layout控件id，生成可用变量
* 生成mvp模式activity相关类
* 生成adapter关联layout控件对象
**Reference:**  ManzoShen_

===============================
操作指南
===============================
**通过Layout控件生成对象**

.. image:: https://github.com/JianLin-Shen/ManzoAndroidPlugin/blob/master/guidimg/v1.0.4.gif

**生成MVPActivity**

.. image:: https://github.com/JianLin-Shen/ManzoAndroidPlugin/blob/master/guidimg/v1.0.3.gif


===============================
Notes
===============================
* Layout中的控件成员，生成变量控件时，可配置是否可选，如果对象不生成，且当前Activity中也不包含同名对象，则不会添加click事件。
否则是会添加点击事件的。

===============================
Member
===============================
* ManzoShen_

.. _ManzoShen: https://blog.csdn.net/shenjinalin123
