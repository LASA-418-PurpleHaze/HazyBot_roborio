package org.lasarobotics.lib.datalogging;

import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import org.lasarobotics.frc2017.hardware.Hardware;

public class Logger {

    private static String fileName;
    private static FileWriter writer;
    private static double startTime;

    static Object lock = new Object();

    static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    static Date date = new Date();

    static ArrayList<Loggable> loggedSystems;
    static LinkedList<String> lines; //strings

    public static void init() {
        lines = new LinkedList();
        loggedSystems = new ArrayList();
    }

    public static void addLog(Loggable l) {
        loggedSystems.add(l);
    }

    public static void log() {
        String line = Hardware.getCurrentTime() - startTime + "";

        if (startTime == Double.MAX_VALUE) {
            startTime = Hardware.getCurrentTime();
        }

        for (Loggable o : loggedSystems) {
            line = line.concat(", ");
            line = line.concat(o.getValues());
        }

        line = line.concat("\n");

        synchronized (lock) {
            lines.addLast(line);

        }
    }

    public static void makeFile() {
        date = new Date();
        startTime = Double.MAX_VALUE;

        fileName = "poopy.csv";
        File logFile = new File(fileName);
        try {
            logFile.createNewFile();
        } catch (IOException ex) {
            System.out.println("Didn't create new file successfully");
        }

        String line = "Time";

        try {
            writer = new FileWriter(logFile);
            System.out.println("Writer made");
        } catch (IOException ex) {
            System.out.println("no writer");
        }

        for (Loggable o : loggedSystems) {
            line = line.concat(", ");
            line = line.concat(o.getNames());
        }

        synchronized (lock) {
            lines.addLast(line);
        }
    }

    public static void closeFile() {
        try {
            writer.close();
        } catch (IOException ex) {
            System.out.println("Botched closing log file.");
        }
    }

    public static void writeToFile() {
        String lineToWrite = null;

        synchronized (lock) {
            if (lines.size() > 0) {
                lineToWrite = lines.removeFirst();
            }
        }

        try {
            if (lineToWrite != null) {
                writer.write(lineToWrite);
            }
            else {
            }
        } catch (Exception ex) {
            System.out.println("Botched writing to log file.");
        }
    }
}
