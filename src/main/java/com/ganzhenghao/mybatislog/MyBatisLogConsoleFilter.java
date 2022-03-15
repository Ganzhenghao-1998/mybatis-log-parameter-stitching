package com.ganzhenghao.mybatislog;

import cn.hutool.core.util.StrUtil;
import com.intellij.execution.filters.Filter;
import com.intellij.openapi.project.Project;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * MyBatisLogConsoleFilter
 *
 * @author huangxingguang
 */
public class MyBatisLogConsoleFilter implements Filter {

    public static final String PREPARING_KEY = MyBatisLogConsoleFilter.class.getName() + ".Preparing";
    public static final String PARAMETERS_KEY = MyBatisLogConsoleFilter.class.getName() + ".Parameters";
    public static final String KEYWORDS_KEY = MyBatisLogConsoleFilter.class.getName() + ".Keywords";

    private static final char MARK = '?';

    private static final Set<String> NEED_BRACKETS;

    static {
        Set<String> types = new HashSet<>(8);
        types.add("String");
        types.add("Date");
        types.add("Time");
        types.add("LocalDate");
        types.add("LocalTime");
        types.add("LocalDateTime");
        types.add("BigDecimal");
        types.add("Timestamp");
        NEED_BRACKETS = Collections.unmodifiableSet(types);
    }

    private final Project project;
    private String sql = null;

    MyBatisLogConsoleFilter(Project project) {
        this.project = project;
    }

    private static Queue<Map.Entry<String, String>> parseParams(String line) {
        line = StringUtils.removeEnd(line, "\n");

//        final String[] params = StringUtils.splitByWholeSeparator(line, "), ");

        List<String> paramList = StrUtil.split(line, ", ");

        final Queue<Map.Entry<String, String>> queue = new ArrayDeque<>(paramList.size());

        for (int i = 0; i < paramList.size(); i++) {

            String param = paramList.get(i);


            String value;
            String type = null;

            // 如果参数名称为null,则类型设置为null
            if (StrUtil.equals("null", param)) {
                value = "null";
            } else {
                value = StrUtil.subBefore(param, '(', false);
                type = StrUtil.subBetween(param, "(", ")");
            }

            queue.offer(new AbstractMap.SimpleEntry<>(value, type));

        }

        return queue;
    }

    @Override
    public @Nullable Result applyFilter(@NotNull String line, int entireLength) {

        final MyBatisLogManager manager = MyBatisLogManager.getInstance(project);
        if (Objects.isNull(manager)) {
            return null;
        }

        if (!manager.isRunning()) {
            return null;
        }

        final String preparing = manager.getPreparing();
        final String parameters = manager.getParameters();
        final List<String> keywords = manager.getKeywords();

        if (CollectionUtils.isNotEmpty(keywords)) {
            for (String keyword : keywords) {
                if (line.contains(keyword)) {
                    sql = null;
                    return null;
                }
            }
        }

        if (line.contains(preparing)) {
            sql = line;
            return null;
        }

        if (StringUtils.isNotBlank(sql) && !line.contains(parameters)) {
            return null;
        }

        if (StringUtils.isBlank(sql)) {
            return null;
        }

        final String logPrefix = StringUtils.substringBefore(sql, preparing);
        final StringBuilder sb = new StringBuilder(StringUtils.substringAfter(sql, preparing));
        final Queue<Map.Entry<String, String>> params = parseParams(StringUtils.substringAfter(line, parameters));

        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) != MARK) {
                continue;
            }

            final Map.Entry<String, String> entry = params.poll();
            if (Objects.isNull(entry)) {
                continue;
            }

            boolean needBrackets = false;
            if (NEED_BRACKETS.contains(entry.getValue())) {
                int j = i - 1;
                needBrackets = j >= 0 && j < sb.length() && sb.charAt(j) != '\"' && sb.charAt(j) != '\'';
                j = i + 1;
                needBrackets = needBrackets && j < sb.length() && sb.charAt(j) != '\"' && sb.charAt(j) != '\'';
            }

            sb.deleteCharAt(i);

            if (needBrackets) {
                sb.insert(i, String.format("'%s'", entry.getKey()));
            } else {
                sb.insert(i, entry.getKey());
            }
        }

        manager.println(logPrefix, sb.toString());

        return null;
    }

}
