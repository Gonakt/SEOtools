import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class KeywordWindow {

    KeywordFinder finder;
    List<String> newKeywords = new ArrayList<>();

    KeywordWindow() {

        JFrame frame = new JFrame("SEO Booster - Подбор ключевых слов");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600,500);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel coreExtending = new JPanel();
        coreExtending.setLayout(null);

        frame.add(coreExtending);

        JTextArea yourSite = new JTextArea("Введите запрос...");
        yourSite.setBounds(10, 10, 560, 20);
        setTextDisappearOnClick(yourSite);
        coreExtending.add(yourSite);

        JLabel progressMessage = new JLabel("Идет поиск ключевых запросов...");
        progressMessage.setBounds(10, 40, 250, 20);
        progressMessage.setVisible(false);
        coreExtending.add(progressMessage);

        ImageIcon searchIcon = new ImageIcon("search_icon.png");
        JLabel searchStatus = new JLabel(searchIcon);
        searchStatus.setBounds(215, 35, 32, 32);
        searchStatus.setVisible(false);
        coreExtending.add(searchStatus);

        JTable keywordsTable = new JTable();
        //keywordsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        DefaultTableModel coreExtendingModel = new DefaultTableModel();
        coreExtendingModel.addColumn("#");
        coreExtendingModel.addColumn("Запрос");
        keywordsTable.setModel(coreExtendingModel);
        keywordsTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        keywordsTable.getColumnModel().getColumn(1).setPreferredWidth(530);

        coreExtending.add(addScrollBar(keywordsTable, 10, 70, 560, 350, "table"));

        JButton findKeywords = new JButton("Найти запросы");
        findKeywords.setBounds(420, 40, 150, 20);
        coreExtending.add(findKeywords);

        findKeywords.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (yourSite.getText().equals("Введите запрос...") || yourSite.getText().equals("")) {

                    JOptionPane.showMessageDialog(frame, "Введите запрос", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
                else {

                    coreExtendingModel.setRowCount(0);
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            Timer timer = new Timer(500, new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {

                                    progressMessage.setVisible(!progressMessage.isVisible());
                                    searchStatus.setVisible(!searchStatus.isVisible());
                                }
                            });
                            timer.start();

                            if (finder == null) {

                                finder = new KeywordFinder(yourSite.getText());

                            }

                            newKeywords = finder.findKeywords();

                            for (int i = 0; i < newKeywords.size(); i++) {
                                coreExtendingModel.addRow(new Object[]{i + 1, newKeywords.get(i)});
                            }

                            timer.stop();
                            searchStatus.setVisible(false);
                            progressMessage.setVisible(false);
                        }
                    });

                    thread.start();

                }
            }
        });

        JButton saveReport = new JButton("Сохранить отчет в Excel...");
        saveReport.setBounds(370, 430, 200, 20);
        coreExtending.add(saveReport);

        saveReport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy'_'HHmmss");
                    Date date = new Date(System.currentTimeMillis());

                    JFileChooser etxtChooser = new JFileChooser();
                    etxtChooser.setDialogTitle("Сохранить отчет...");
                    etxtChooser.setSelectedFile(new File("keyword_report_" + formatter.format(date) + ".xlsx"));
                    int userInput = etxtChooser.showSaveDialog(frame);

                    String defaultPath = "";
                    if (new File("settings.txt").exists()) {

                        try {

                            BufferedReader reader = new BufferedReader(new FileReader(
                                    "settings.txt"));

                            String line = reader.readLine();

                            while (line != null) {

                                if (line.contains("default_path=")) {

                                    defaultPath = line.split("=")[1];
                                    break;
                                }

                                line = reader.readLine();
                            }

                            reader.close();
                            etxtChooser.setCurrentDirectory(new File(defaultPath));
                        }
                        catch (Exception ex) {

                            ex.printStackTrace();
                        }

                    }


                    if (userInput == JFileChooser.CANCEL_OPTION)
                        return;

                    FileOutputStream fos = new FileOutputStream(etxtChooser.getSelectedFile().getAbsolutePath());

                    Workbook book = new XSSFWorkbook();
                    Sheet sheet = book.createSheet();

                    Row header = sheet.createRow(0);
                    header.createCell(0).setCellValue("#");
                    header.createCell(1).setCellValue("Ключевое слово");

                    for (int i = 0; i < newKeywords.size(); i++) {

                        Row data = sheet.createRow(sheet.getLastRowNum() + 1);
                        data.createCell(0).setCellValue(i);
                        data.createCell(1).setCellValue(newKeywords.get(i));
                    }

                    formatter = new SimpleDateFormat("dd.MM.yyyy");
                    date = new Date(System.currentTimeMillis());
                    String reportData = "Подбор ключевых слов;" +  etxtChooser.getSelectedFile().getAbsolutePath() + ";" + formatter.format(date) + "\n";
                    addToLastReports(reportData);

                    book.write(fos);
                    fos.close();
                    book.close();

                    JOptionPane.showMessageDialog(frame, "Отчет успешно сохранен!", "Уведомление", JOptionPane.INFORMATION_MESSAGE);
                }
                catch (Exception ex) {

                    ex.printStackTrace();
                }


            }
        });

        frame.setVisible(true);
    }

    void setTextDisappearOnClick(JTextArea area) {

        area.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

                area.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });
    }

    JScrollPane addScrollBar(Object component, int x, int y, int width, int height, String kind) {

        JScrollPane pane = new JScrollPane();
        if (kind == "textarea")
            pane = new JScrollPane((JTextArea) component);
        if (kind == "table")
            pane = new JScrollPane((JTable) component);
        pane.setBounds(x, y, width, height);
        return pane;
    }

    private static void addToLastReports(String fileData) {

        try {
            File lastReports = new File("last_reports.txt");
            lastReports.createNewFile();

            BufferedReader reader;
            reader = new BufferedReader(new FileReader(
                    "last_reports.txt"));

            String line = reader.readLine();
            int i = 0;
            while (line != null) {

                i++;
                line = reader.readLine();

            }

            reader.close();

            if (i == 5)
                eraseLast();

            appendToStart(fileData);

        }
        catch (Exception e) {

            e.printStackTrace();
        }
    }

    private static void eraseLast() {

        byte b;
        try {
            RandomAccessFile f = new RandomAccessFile("last_reports.txt", "rw");
            long length = f.length() - 1;
            do {
                length -= 1;
                f.seek(length);
                b = f.readByte();
            } while (b != 10);
            f.setLength(length + 1);
            f.close();
        }
        catch (Exception e) {

            e.printStackTrace();
        }
    }

    private static void appendToStart(String text) {

        try {
            File mFile = new File("last_reports.txt");
            FileInputStream fis = new FileInputStream(mFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String result = "";
            String line = "";
            while ((line = br.readLine()) != null) {
                result = result + line;
            }

            result = text + result;

            mFile.delete();
            FileOutputStream fos = new FileOutputStream(mFile);
            fos.write(result.getBytes());
            fos.flush();
        }
        catch (Exception e) {

            e.printStackTrace();
        }
    }
}
