package com.example.fortune.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FortuneHistoryRecord {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String FIELD_SEPARATOR = "\t";

    private final LocalDateTime createdAt;
    private final FortuneMode mode;
    private final FortuneDrawType drawType;
    private final String patternSummary;
    private final String keywordSummary;
    private final String advice;

    public FortuneHistoryRecord(FortuneMode mode, FortuneDrawType drawType, FortuneResult result) {
        this.createdAt = LocalDateTime.now();
        this.mode = mode;
        this.drawType = drawType;
        this.patternSummary = result.getPatternSummary();
        this.keywordSummary = result.getKeywordSummary();
        this.advice = result.getAdvice();
    }

    private FortuneHistoryRecord(LocalDateTime createdAt, FortuneMode mode, FortuneDrawType drawType,
                                 String patternSummary, String keywordSummary, String advice) {
        this.createdAt = createdAt;
        this.mode = mode;
        this.drawType = drawType;
        this.patternSummary = patternSummary;
        this.keywordSummary = keywordSummary;
        this.advice = advice;
    }

    public FortuneDrawType getDrawType() {
        return drawType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public FortuneMode getMode() {
        return mode;
    }

    public String toDisplayText(int index) {
        return index + ". " + FORMATTER.format(createdAt) + "\n"
                + "方式：" + drawType + " - " + mode + "\n"
                + "图案：" + patternSummary + "\n"
                + "关键词：" + keywordSummary + "\n"
                + "建议：" + advice;
    }

    public String toStorageLine() {
        return createdAt.toString() + FIELD_SEPARATOR
                + mode.name() + FIELD_SEPARATOR
                + (drawType != null ? drawType.name() : "") + FIELD_SEPARATOR
                + escape(patternSummary) + FIELD_SEPARATOR
                + escape(keywordSummary) + FIELD_SEPARATOR
                + escape(advice);
    }

    public static FortuneHistoryRecord fromStorageLine(String line) {
        String[] parts = line.split(FIELD_SEPARATOR, -1);
        if (parts.length != 5 && parts.length != 6) {
            throw new IllegalArgumentException("历史记录字段数量不正确");
        }

        FortuneDrawType drawType = null;
        int patternIndex = 2;
        if (parts.length == 6) {
            if (!parts[2].isEmpty()) {
                drawType = FortuneDrawType.valueOf(parts[2]);
            }
            patternIndex = 3;
        }

        return new FortuneHistoryRecord(
                LocalDateTime.parse(parts[0]),
                FortuneMode.valueOf(parts[1]),
                drawType,
                unescape(parts[patternIndex]),
                unescape(parts[patternIndex + 1]),
                unescape(parts[patternIndex + 2])
        );
    }

    private static String escape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private static String unescape(String value) {
        StringBuilder builder = new StringBuilder();
        boolean escaping = false;
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (!escaping) {
                if (ch == '\\') {
                    escaping = true;
                } else {
                    builder.append(ch);
                }
                continue;
            }

            if (ch == 't') {
                builder.append('\t');
            } else if (ch == 'n') {
                builder.append('\n');
            } else if (ch == 'r') {
                builder.append('\r');
            } else {
                builder.append(ch);
            }
            escaping = false;
        }
        if (escaping) {
            builder.append('\\');
        }
        return builder.toString();
    }
}