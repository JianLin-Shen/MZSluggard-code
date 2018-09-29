
===============================
MZSluggard-code
===============================
.. image:: https://img.shields.io/badge/version-1.0.4-green.svg?style=green
        :target: https://github.com/JianLin-Shen/MZSluggard-code/releases
        
.. image:: https://img.shields.io/badge/jetbrains-download-green.svg?style=green
        :target: https://plugins.jetbrains.com/plugin/11140-mzsluggard-code
        
.. image:: https://img.shields.io/badge/local-download-green.svg?style=green
        :target: https://github.com/JianLin-Shen/MZSluggard-code/blob/master/resleaseversion/MZSluggard-code-1.0.4.jar

Android插件，辅助生成代码MZSluggard-code

* 根据layout控件id，生成可用变量
* 生成mvp模式activity相关类
* 生成adapter关联layout控件对象
**Reference:**  ManzoShen_

===============================
AndroidStudio中添加
===============================

**🔍关键字：MZSluggard**

注：通过Browse搜索添加的插件，版本不一定最新。原因是jetBrains审核插件需要时间。最新插件可通过点击标签 download_，本地安装。

.. image:: https://github.com/JianLin-Shen/MZSluggard-code/blob/master/guidimg/load_bos.png

===============================
操作指南
===============================
**通过Layout控件生成对象**

.. image:: https://github.com/JianLin-Shen/MZSluggard-code/blob/master/guidimg/v1.0.4.gif

**生成MVPActivity**

.. image:: https://github.com/JianLin-Shen/MZSluggard-code/blob/master/guidimg/v1.0.3.gif

**Adapter生成代码**

.. image:: https://github.com/JianLin-Shen/MZSluggard-code/blob/master/guidimg/Adapter_user.gif

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
.. _DownLoad: https://github.com/JianLin-Shen/MZSluggard-code/blob/master/resleaseversion/MZSluggard-code-1.0.4.jar
