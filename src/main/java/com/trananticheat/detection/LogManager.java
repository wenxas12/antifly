package com.trananticheat.detection;

import com.trananticheat.TranAntiCheat;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class LogManager {

    private final TranAntiCheat plugin;
    private final File logDir;
    private final Map<UUID, List<String>> pendingLogs = new ConcurrentHashMap<>();
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public LogManager(TranAntiCheat plugin) {
        this.plugin = plugin;
        this.logDir = new File(plugin.getDataFolder(), "logs");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
    }

    public void log(String category, String data) {
        String timestamp = LocalDateTime.now().format(fmt);
        String line = "[" + timestamp + "] [" + category + "] " + data;
        plugin.getLogger().info(line);
        File file = new File(logDir, "violations.log");
        try {
            java.io.FileWriter fw = new java.io.FileWriter(file, true);
            fw.write(line + System.lineSeparator());
            fw.close();
        } catch (IOException ignored) {
        }
    }

    public void recordJoin(UUID uuid, String brand, List<String> modules) {
        List<String> logs = pendingLogs.computeIfAbsent(uuid, k -> new ArrayList<>());
        String timestamp = LocalDateTime.now().format(fmt);
        logs.add("[" + timestamp + "] brand=" + brand + " modules=" + modules);
    }

    public List<String> getJoinLogs(UUID uuid, int windowHours) {
        List<String> stored = pendingLogs.get(uuid);
        if (stored == null || stored.isEmpty()) return List.of();
        return new ArrayList<>(stored);
    }

    public int countFlags(UUID uuid, int windowHours) {
        File file = new File(logDir, "violations.log");
        if (!file.exists()) return 0;
        int count = 0;
        try {
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(uuid.toString()) && line.contains("[FLAG]")) {
                    count++;
                }
            }
            br.close();
        } catch (IOException ignored) {
        }
        return count;
    }

    public void flushAll() {
        pendingLogs.clear();
    }
}