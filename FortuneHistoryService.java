package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FortuneHistoryService {
    private static final int MAX_HISTORY_SIZE = 10;
    private static final String HISTORY_FILE_NAME = ".fortune_history.tsv";
    private final LinkedList<FortuneHistoryRecord> dailyRecords = new LinkedList<>();
    private final LinkedList<FortuneHistoryRecord> endlessRecords = new LinkedList<>();
    private final File historyFile;

    public FortuneHistoryService() {
        historyFile = new File(System.getProperty("user.home"), HISTORY_FILE_NAME);
        load();
    }

    public void add(FortuneMode mode, FortuneDrawType drawType, FortuneResult result) {
        if (drawType.isDaily()) {
            LocalDate today = LocalDate.now();
            for (FortuneHistoryRecord record : dailyRecords) {
                LocalDate recordDate = record.getCreatedAt().toLocalDate();
                if (recordDate.equals(today) && record.getMode() == mode) {
                    return;
                }
            }
            dailyRecords.addFirst(new FortuneHistoryRecord(mode, drawType, result));
            while (dailyRecords.size() > MAX_HISTORY_SIZE) {
                dailyRecords.removeLast();
            }
        } else {
            endlessRecords.addFirst(new FortuneHistoryRecord(mode, drawType, result));
            while (endlessRecords.size() > MAX_HISTORY_SIZE) {
                endlessRecords.removeLast();
            }
        }
        save();
    }

    public List<FortuneHistoryRecord> recentRecords() {
        List<FortuneHistoryRecord> allRecords = new ArrayList<>();
        allRecords.addAll(dailyRecords);
        allRecords.addAll(endlessRecords);
        return allRecords;
    }

    public List<FortuneHistoryRecord> getDailyRecords() {
        return new ArrayList<>(dailyRecords);
    }

    public List<FortuneHistoryRecord> getEndlessRecords() {
        return new ArrayList<>(endlessRecords);
    }

    public boolean isEmpty() {
        return dailyRecords.isEmpty() && endlessRecords.isEmpty();
    }

    public boolean isDailyEmpty() {
        return dailyRecords.isEmpty();
    }

    public boolean isEndlessEmpty() {
        return endlessRecords.isEmpty();
    }

    private void load() {
        if (!historyFile.isFile()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(historyFile), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                FortuneHistoryRecord record = FortuneHistoryRecord.fromStorageLine(line);
                FortuneDrawType drawType = record.getDrawType();
                LinkedList<FortuneHistoryRecord> records = (drawType != null && drawType.isDaily()) ? dailyRecords : endlessRecords;
                if (records.size() < MAX_HISTORY_SIZE) {
                    records.add(record);
                }
            }
        } catch (RuntimeException | IOException ex) {
            dailyRecords.clear();
            endlessRecords.clear();
        }
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(historyFile), StandardCharsets.UTF_8))) {
            for (FortuneHistoryRecord record : dailyRecords) {
                writer.write(record.toStorageLine());
                writer.newLine();
            }
            for (FortuneHistoryRecord record : endlessRecords) {
                writer.write(record.toStorageLine());
                writer.newLine();
            }
        } catch (IOException ex) {
            System.err.println("保存历史记录失败：" + ex.getMessage());
        }
    }
}
