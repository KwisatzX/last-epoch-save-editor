package io.github.kwisatzx.lastepoch.itemdata;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

@Disabled("Code used for occasional in-game testing and etc")
class InvItemCreator {
    Path file;
    Path backups;
    String operatingDir;

    public InvItemCreator() {
        operatingDir = "C:/Users/Kwisatz Haderach/AppData/LocalLow/Eleventh Hour Games/Last Epoch/Saves";
        file = Paths.get(operatingDir, "Epoch_Local_Global_Data_Beta");
        backups = Paths.get(operatingDir, "backups");
    }

    @Test
    void testPath() throws IOException {
        PrintWriter printWriter = new PrintWriter(new FileWriter("tempItems.txt"));
        int x = 0, y = 3;
        int tabId = 11;
        int tierMod = 2;
        int tier = 96 + tierMod;
        for (int i = 0; i < 140; i++, x++) {
            if (x > 11) {
                x = 0;
                y++;
                if (y > 13) {
                    tabId++;
                    y = 3;
                }
            }
            printWriter.println("{\"itemData\":\"\",\"data\":[0,21,0,1,255,255,255,0,1," + tier + "," + i + ",255,0]," +
                                        "\"inventoryPosition\":{\"x\":" + x + ",\"y\":" + y + "},\"tabID\":" + tabId +
                                        ",\"quantity\":1,\"formatVersion\":0},");
        }
        printWriter.close();
    }

    //    @Test
    void temp() throws IOException {
        PrintWriter printWriter = new PrintWriter(new FileWriter("temp.txt"));
        for (int i = 0; i < 652; i++) {
            printWriter.println(i + " - ");
        }
        printWriter.close();
    }

    //    @Test
    void sortTxt() throws IOException {
        Path file = Paths.get("Affixes.txt");
        PrintWriter printWriter = new PrintWriter(new FileWriter("sorted_" + file));
        Files.lines(file)
                .sorted(Comparator.comparing(s -> s.substring(s.indexOf(' ') + 1)))
                .forEach(printWriter::println);
        printWriter.close();
    }

    //    @Test
    void processTxtList() throws IOException {
        StringBuilder sb = new StringBuilder();
        Path file = Paths.get("tier2_2.txt");
        Files.lines(file)
                .forEach(line -> {
                    if (line.equals("")) sb.append("\r\n");
                    else sb.append(" ").append(line);
                });
        int index;
        while ((index = sb.indexOf("\r\n\r\n")) != -1) {
            sb.replace(index, index + "\r\n\r\n".length(), "\r\n");
        }
        while ((index = sb.indexOf("\r\n ")) != -1) {
            sb.replace(index, index + "\r\n ".length(), "\r\n");
        }
        while ((sb.indexOf("  ") + sb.indexOf("   ") + sb.indexOf("    ")) > 0) {
            index = sb.indexOf("  ");
            if (index != -1) sb.replace(index, index + 2, " ");
            index = sb.indexOf("   ");
            if (index != -1) sb.replace(index, index + 3, " ");
            index = sb.indexOf("    ");
            if (index != -1) sb.replace(index, index + 4, " ");
        }
        FileWriter fileWriter = new FileWriter("processed_" + file.toFile());
        fileWriter.write(sb.toString());
        fileWriter.close();
    }
}