package com.ganzhenghao.mybatislog.actions;

import cn.hutool.core.util.ObjectUtil;
import com.ganzhenghao.mybatislog.MyBatisLogManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;

import java.util.Objects;

/**
 * @author Ganzhenghao
 * @version 1.0
 * @date 2022/3/15 11:56
 */
public class MybatisLogAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        Project project = e.getProject();

        if (ObjectUtil.isEmpty(project)) {
            return;
        }

        if (!project.isOpen() || !project.isInitialized()) {
            return;
        }

        if ("EditorPopup".equals(e.getPlace())) {
            MyBatisLogManager manager = MyBatisLogManager.getInstance(project);
            if (Objects.nonNull(manager) && manager.getToolWindow().isAvailable()) {
                if (!manager.isRunning()) {
                    manager.run();
                }
                manager.getToolWindow().activate(null);
                return;
            }
        }

        rerun(project);


    }

    public void rerun(final Project project) {
        final MyBatisLogManager manager = MyBatisLogManager.getInstance(project);
        if (Objects.nonNull(manager)) {
            Disposer.dispose(manager);
        }
        MyBatisLogManager.createInstance(project).run();
    }
}
