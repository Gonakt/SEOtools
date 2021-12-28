import org.apache.poi.xwpf.usermodel.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class PositionWindow {

    List<String> qrs = new ArrayList<>();
    List<String> allSites = new ArrayList<>();
    List<SearchResultPosition> positions = new ArrayList<>();

    PositionWindow() {

        JFrame frame = new JFrame("SEO Booster - Проверка позиций");
        frame.setSize(700, 700);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JLabel enterSiteAddress = new JLabel("Адрес сайта:");
        enterSiteAddress.setBounds(10, 10, 200, 20);
        frame.add(enterSiteAddress);

        JTextArea siteAddress = new JTextArea();
        siteAddress.setBounds(10, 30, 560, 20);
        frame.add(siteAddress);

        JButton check = new JButton("Проверить");
        check.setBounds(580, 30, 100, 20);
        frame.add(check);

        JLabel enterQueries = new JLabel("Запросы:");
        enterQueries.setBounds(10, 60, 200, 20);
        frame.add(enterQueries);

        JTextArea queries = new JTextArea("");

        JScrollPane yourPane = new JScrollPane(queries);
        yourPane.setBounds(10, 80, 670, 100);
        frame.add(yourPane);

        JLabel enterCompetitors = new JLabel("Сайты конкурентов:");
        enterCompetitors.setBounds(10, 190, 200, 20);
        frame.add(enterCompetitors);

        JTextArea competitors = new JTextArea("");

        JScrollPane competitorsPane = new JScrollPane(competitors);
        competitorsPane.setBounds(10, 210, 670, 100);
        frame.add(competitorsPane);

        JLabel resultLabel = new JLabel("Результаты:");
        resultLabel.setBounds(10, 320, 200, 20);
        frame.add(resultLabel);

        JTable resultTable = new JTable();
        resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane resultTablePane = new JScrollPane(resultTable);
        DefaultTableModel resultTableModel = new DefaultTableModel();
        resultTableModel.addColumn("#");
        resultTableModel.addColumn("Запрос");
        resultTableModel.addColumn("Сайт");
        resultTableModel.addColumn("Позиция в Google");
        resultTableModel.addColumn("Позиция в Яндекс");
        resultTable.setModel(resultTableModel);
        resultTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        resultTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        resultTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        resultTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        resultTable.getColumnModel().getColumn(4).setPreferredWidth(120);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        resultTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        resultTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        resultTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        resultTablePane.setBounds(10, 340, 670, 280);
        frame.add(resultTablePane);

        check.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (siteAddress.getText().isEmpty()) {

                    JOptionPane.showMessageDialog(frame, "Введите адрес сайта", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
                else {

                    if (queries.getText().isEmpty()) {

                        JOptionPane.showMessageDialog(frame, "Введите список запросов", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                    else {

                        String s[] = queries.getText().split("\n");
                        qrs = Arrays.asList(s);

                        allSites.add(siteAddress.getText());

                        String[] cmpt = competitors.getText().split("\n");
                        for (int i = 0; i < cmpt.length; i++)
                            allSites.add(cmpt[i]);

                        for (String site : allSites) {

                            Thread searchThread = new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    PositionChecker checker = new PositionChecker(site, qrs);
                                    positions = checker.fetchPositions();

                                    int queryNum = resultTableModel.getRowCount() + 1;
                                    for (SearchResultPosition pos : positions) {

                                        String yaPos = "";
                                        String gPos = "";
                                        if (pos.getYaPosition() == -1) {
                                            yaPos = "не найдено";
                                        }
                                        else {

                                            if (pos.getgPosition() == -1) {

                                                gPos = "не найдено";
                                            }
                                            else {

                                                yaPos = String.valueOf(pos.getYaPosition());
                                                gPos = String.valueOf(pos.getgPosition());
                                            }
                                        }

                                        resultTableModel.addRow(new Object[]{queryNum, pos.getQuery(), pos.getSite(), gPos, yaPos});
                                        queryNum++;
                                    }
                                }
                            });

                        searchThread.start();
                        }
                    }
                }
            }
        });

        JButton createReport = new JButton("Сохранить отчет в Word");
        createReport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy'_'HHmmss");
                Date date = new Date(System.currentTimeMillis());

                JFileChooser positionChooser = new JFileChooser();
                positionChooser.setDialogTitle("Сохранить отчет...");
                positionChooser.setSelectedFile(new File("position_report_" + formatter.format(date) + ".docx"));
                int userInput = positionChooser.showSaveDialog(frame);

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

                        positionChooser.setCurrentDirectory(new File(defaultPath));
                        reader.close();

                    }
                    catch (Exception ex) {

                        ex.printStackTrace();
                    }

                }


                if (userInput == JFileChooser.CANCEL_OPTION)
                    return;

                try {

                    FileOutputStream positionStream = new FileOutputStream(positionChooser.getSelectedFile().getAbsolutePath());

                    XWPFDocument positionDocument = new XWPFDocument();

                    formatter = new SimpleDateFormat("dd.MM.yyyy 'в' HH:mm:ss");
                    date = new Date(System.currentTimeMillis());

                    XWPFParagraph positionParagraph = positionDocument.createParagraph();
                    XWPFRun positionRun = positionParagraph.createRun();
                    positionRun.setFontFamily("Times New Roman");
                    positionRun.setFontSize(14);
                    positionRun.setText("Дата и время проверки: " + formatter.format(date));
                    positionRun.addBreak();
                    positionRun.setText("Проверяемый сайт: " + siteAddress.getText());
                    positionRun.addBreak();
                    positionRun.addBreak();

                    positionRun.setText("Запросы:");
                    positionRun.addBreak();
                    for (String qr : qrs) {

                        positionRun.setText(qr);
                        positionRun.addBreak();
                    }
                    positionRun.addBreak();

                    positionRun.setText("Сайты конкурентов:");
                    positionRun.addBreak();
                    allSites.remove(0);
                    for (String site : allSites) {

                        positionRun.setText(site);
                        positionRun.addBreak();
                    }
                    positionRun.addBreak();

                    XWPFTable etxtTable = positionDocument.createTable();

                    XWPFTableRow headerRow = etxtTable.getRow(0);

                    XWPFParagraph numberParagraph = headerRow.getCell(0).addParagraph();
                    setRun(numberParagraph.createRun(), "Times New Roman", 14, "000000", "#", true, false);

                    XWPFParagraph queryParagraph = headerRow.createCell().addParagraph();
                    setRun(queryParagraph.createRun(), "Times New Roman", 14, "000000", "Запрос", true, false);

                    XWPFParagraph siteParagraph = headerRow.createCell().addParagraph();
                    setRun(siteParagraph.createRun(), "Times New Roman", 14, "000000", "Сайт", true, false);

                    XWPFParagraph yaPosParagraph = headerRow.createCell().addParagraph();
                    setRun(yaPosParagraph.createRun(), "Times New Roman", 14, "000000", "Позиция в Яндекс", true, false);

                    XWPFParagraph gPosParagraph = headerRow.createCell().addParagraph();
                    setRun(gPosParagraph.createRun(), "Times New Roman", 14, "000000", "Позиция в Google", true, false);

                    int i = 0;
                    for (SearchResultPosition position : positions) {

                        XWPFTableRow positionResultRow = etxtTable.createRow();
                        positionResultRow.getCell(0).setText(String.valueOf(i));
                        positionResultRow.getCell(1).setText(position.getQuery());
                        positionResultRow.getCell(2).setText(position.getSite());

                        String yaPos = "";
                        String gPos = "";
                        if (position.getYaPosition() == -1) {
                            yaPos = "не найдено";
                        }
                        else {

                            if (position.getgPosition() == -1) {

                                gPos = "не найдено";
                            }
                            else {

                                yaPos = String.valueOf(position.getYaPosition());
                                gPos = String.valueOf(position.getgPosition());
                            }
                        }

                        positionResultRow.getCell(3).setText(yaPos);
                        positionResultRow.getCell(4).setText(gPos);

                        i++;

                    }

                    formatter = new SimpleDateFormat("dd.MM.yyyy");
                    date = new Date(System.currentTimeMillis());
                    String reportData = "Проверка позиций сайта;" +  positionChooser.getSelectedFile().getAbsolutePath() + ";" + formatter.format(date) + "\n";
                    addToLastReports(reportData);

                    positionDocument.write(positionStream);
                    positionDocument.close();
                    positionStream.close();

                    JOptionPane.showMessageDialog(frame, "Отчет успешно сохранен!", "Уведомление", JOptionPane.INFORMATION_MESSAGE);
                }
                catch (Exception ex) {

                    ex.printStackTrace();
                }
            }

        });
        createReport.setBounds(480, 630, 200, 20);
        frame.add(createReport);

        frame.setVisible(true);
    }

    private static void setRun (XWPFRun run , String fontFamily , int fontSize , String colorRGB , String text , boolean bold , boolean addBreak) {
        run.setFontFamily(fontFamily);
        run.setFontSize(fontSize);
        run.setColor(colorRGB);
        run.setText(text);
        run.setBold(bold);
        if (addBreak) run.addBreak();
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
