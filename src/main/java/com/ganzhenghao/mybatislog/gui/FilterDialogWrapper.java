package com.ganzhenghao.mybatislog.gui;

import com.ganzhenghao.mybatislog.MyBatisLogManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.Gray;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * FilterDialogWrapper
 *
 * @author huangxingguang
 */
public class FilterDialogWrapper extends DialogWrapper {
    private final MyBatisLogManager manager;
    private JTextField txtPreparing;
    private JTextField txtParameters;
    private JPanel root;
    private JTextArea txtKeywords;

    public FilterDialogWrapper(@Nullable Project project, MyBatisLogManager manager) {
        super(project, false);

        this.manager = manager;

        init();

        setTitle("Filter Setting");

    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        root.setPreferredSize(new Dimension(600, 400));

        txtKeywords.setBorder(new LineBorder(Gray._212, 1));

        txtPreparing.setText(manager.getPreparing());
        txtParameters.setText(manager.getParameters());
        txtKeywords.setText(String.join("\n", manager.getKeywords()));

        return root;
    }

    public String getPreparing() {
        String text = txtPreparing.getText();
        if (StringUtils.isBlank(text)) {
            text = manager.getPreparing();
        }
        return text;
    }

    public String getParameters() {
        String text = txtParameters.getText();
        if (StringUtils.isBlank(text)) {
            text = manager.getParameters();
        }
        return text;
    }

    public String getKeywords() {
        return txtKeywords.getText();
    }

}
