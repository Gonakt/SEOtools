import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AntiplagiatWindow {

    List<PlagiatLink> etxtLinks = new ArrayList<>();
    String etxtUnique = "";
    TextQualityChecker checker;

    String textRuUnique = "";
    String textRuSpam = "";
    String textRuWater = "";
    List<PlagiatLink> textRuLinks = new ArrayList<>();

    AntiplagiatWindow() {

        generateAntiplagiatWindowInterface();
    }

    void generateAntiplagiatWindowInterface() {

        JFrame frame = new JFrame("SEO Booster - Проверка на антиплагиат");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600,580);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setLayout(null);

        JTextArea text = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(text);
        text.setText("Введите текст для проверки (не больше 3000 символов)");
        text.setLineWrap(true);
        text.setWrapStyleWord(true);

        scrollPane.setBounds(10, 10, 565, 290);
        frame.add(scrollPane);

        JPanel systemsCheckboxes = new JPanel(new GridLayout(1, 5));
        JCheckBox etxt = new JCheckBox("Etxt");
        etxt.setSelected(true);
        JCheckBox textRu = new JCheckBox("Text.ru");
        textRu.setSelected(true);
        systemsCheckboxes.add(etxt);
        systemsCheckboxes.add(textRu);
        systemsCheckboxes.setBounds(10, 310, 565, 20);
        frame.add(systemsCheckboxes);

        LineBorder line = new LineBorder(Color.GRAY, 2);

        JPanel etxtPanel = new JPanel();
        etxtPanel.setLayout(null);
        TitledBorder etxtBorder = new TitledBorder(line, "Etxt");
        etxtPanel.setBorder(etxtBorder);
        etxtPanel.setBounds(10, 340, 280, 160);
        frame.add(etxtPanel);

        JLabel etxtUniqueLabel = new JLabel("Уникальность: ");
        etxtUniqueLabel.setBounds(10, 33, 200, 20);
        JLabel etxtStatus = new JLabel("Ожидание запуска проверки...");
        etxtStatus.setBounds(10, 63, 250, 20);
        JProgressBar etxtProgressBar = new JProgressBar();
        etxtProgressBar.setStringPainted(true);
        JButton etxtReport = new JButton("Посмотреть подробный отчет...");
        etxtReport.setEnabled(false);
        etxtReport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFrame etxtReportFrame = new JFrame("SEO Booster - Отчет по уникальности etxt.ru");
                etxtReportFrame.setLayout(null);
                etxtReportFrame.setSize(600, 600);
                etxtReportFrame.setLocationRelativeTo(null);

                JLabel header = new JLabel("Результаты проверки в сервисе etxt.ru");
                header.setFont(header.getFont().deriveFont(Font.BOLD));
                header.setFont(header.getFont().deriveFont(20.0f));
                header.setBounds(100, 10, 400, 25);
                etxtReportFrame.add(header);

                JLabel summary = new JLabel("Уникальность текста " + etxtUnique + ", всего обнаружено " + etxtLinks.size() + " совпадений");
                summary.setFont(summary.getFont().deriveFont(Font.PLAIN));
                summary.setFont(summary.getFont().deriveFont(14.0f));
                summary.setBounds(10, 45, 600, 25);
                etxtReportFrame.add(summary);

                JLabel etxtReportLink = new JLabel("<html>Ссылка на отчет на etxt.ru: " + "<font color='blue'>" + checker.getDriver().getCurrentUrl() + "</font></html>");
                etxtReportLink.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
                        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                            try {
                                desktop.browse(new URI(checker.getDriver().getCurrentUrl()));

                            } catch (Exception ex) {

                                ex.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {

                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {

                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {

                    }

                    @Override
                    public void mouseExited(MouseEvent e) {

                    }
                });
                etxtReportLink.setFont(etxtReportLink.getFont().deriveFont(Font.PLAIN));
                etxtReportLink.setFont(etxtReportLink.getFont().deriveFont(14.0f));
                etxtReportLink.setBounds(10, 80, 600, 25);
                etxtReportFrame.add(etxtReportLink);

                JTable etxtTable = new JTable();
                etxtTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                JScrollPane etxtTablePane = new JScrollPane(etxtTable);
                DefaultTableModel tableModel = new DefaultTableModel();
                tableModel.addColumn("Ссылка");
                tableModel.addColumn("Схожесть");
                etxtTable.setModel(tableModel);
                etxtTable.getColumnModel().getColumn(0).setPreferredWidth(500);
                etxtTable.getColumnModel().getColumn(1).setPreferredWidth(70);

                etxtTable.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {

                        int row = etxtTable.rowAtPoint(new Point(e.getX(), e.getY()));
                        int col = etxtTable.columnAtPoint(new Point(e.getX(), e.getY()));

                        if (col == 0) {

                            String linkToOpen = (String) etxtTable.getValueAt(row, col);
                            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
                            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                                try {
                                    desktop.browse(new URI(linkToOpen.replaceAll("\\<.*?>", "") ));

                                } catch (Exception ex) {

                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
                });

                for (PlagiatLink link : etxtLinks) {

                    String url = link.getLink();
                    String percent = link.getSamePercent();
                    tableModel.addRow(new Object[]{"<html><font color='blue'>" + url + "</font></html>", percent});
                }

                etxtTablePane.setBounds(10, 115, 570, 400);
                etxtReportFrame.add(etxtTablePane);

                etxtReportFrame.setVisible(true);
            }
        });
        etxtReport.setBounds(10, 123, 260, 20);
        etxtProgressBar.setBounds(10, 93, 260, 20);
        etxtProgressBar.setValue(0);
        etxtPanel.add(etxtUniqueLabel);
        etxtPanel.add(etxtStatus);
        etxtPanel.add(etxtProgressBar);
        etxtPanel.add(etxtReport);

        JPanel textRuPanel = new JPanel();
        TitledBorder textruBorder = new TitledBorder(line, "Text.ru");
        textRuPanel.setBorder(textruBorder);
        textRuPanel.setBounds(300, 340, 280, 160);
        textRuPanel.setLayout(null);
        frame.add(textRuPanel);

        JButton saveAllReports = new JButton("Сохранить отчет в Word");
        saveAllReports.setBounds(377, 510, 200, 20);

        frame.add(saveAllReports);

        saveAllReports.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                if (!etxtUnique.equals("") || !textRuUnique.equals("")) {
                    if (!etxtUnique.equals("")) {

                        XWPFDocument etxtDocument = new XWPFDocument();

                        try {
                            SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy'_'HHmmss");
                            Date date = new Date(System.currentTimeMillis());

                            JFileChooser etxtChooser = new JFileChooser();
                            etxtChooser.setDialogTitle("Сохранить отчет...");
                            etxtChooser.setSelectedFile(new File("etxt_report_" + formatter.format(date) + ".docx"));

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

                                    etxtChooser.setCurrentDirectory(new File(defaultPath));
                                    reader.close();
                                }
                                catch (Exception ex) {

                                    ex.printStackTrace();
                                }

                            }


                            int userInput = etxtChooser.showSaveDialog(frame);

                            if (userInput == JFileChooser.CANCEL_OPTION)
                                return;

                            FileOutputStream etxtStream = new FileOutputStream(etxtChooser.getSelectedFile().getAbsolutePath());

                            XWPFParagraph etxtParagraph = etxtDocument.createParagraph();
                            XWPFRun etxtRun = etxtParagraph.createRun();
                            etxtRun.setFontFamily("Times New Roman");
                            etxtRun.setFontSize(14);
                            etxtRun.setText("Проверенный текст:");
                            etxtRun.addBreak();
                            etxtRun.setText(text.getText());
                            etxtRun.addBreak();
                            etxtRun.addBreak();
                            etxtRun.setText("Уникальность: ");
                            etxtRun.setText(etxtUnique);
                            etxtRun.addBreak();

                            XWPFTable etxtTable = etxtDocument.createTable();

                            XWPFTableRow headerRow = etxtTable.getRow(0);

                            XWPFParagraph linkParagraph = headerRow.getCell(0).addParagraph();
                            setRun(linkParagraph.createRun(), "Times New Roman", 14, "000000", "Ссылка", true, false);

                            XWPFParagraph sameParagraph = headerRow.createCell().addParagraph();
                            setRun(sameParagraph.createRun(), "Times New Roman", 14, "000000", "Схожесть", true, false);

                            for (PlagiatLink link : etxtLinks) {

                                XWPFTableRow etxtResultRow = etxtTable.createRow();
                                etxtResultRow.getCell(0).setText(link.getLink());
                                etxtResultRow.getCell(1).setText(link.getSamePercent());
                            }

                            formatter = new SimpleDateFormat("dd.MM.yyyy");
                            date = new Date(System.currentTimeMillis());
                            String reportData = "Проверка качества текста;" +  etxtChooser.getSelectedFile().getAbsolutePath() + ";" + formatter.format(date) + "\n";
                            addToLastReports(reportData);

                            etxtDocument.write(etxtStream);
                            etxtDocument.close();

                        } catch (Exception ex) {

                            ex.printStackTrace();
                        }
                    }
                    if (!textRuUnique.equals("")) {

                        XWPFDocument textRuDocument = new XWPFDocument();

                        try {
                            SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy'_'HHmmss");
                            Date date = new Date(System.currentTimeMillis());

                            JFileChooser textRuChooser = new JFileChooser();
                            textRuChooser.setDialogTitle("Сохранить отчет...");
                            textRuChooser.setSelectedFile(new File("TextRu_report_" + formatter.format(date) + ".docx"));

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
                                    textRuChooser.setCurrentDirectory(new File(defaultPath));
                                }
                                catch (Exception ex) {

                                    ex.printStackTrace();
                                }

                            }


                            int userInput = textRuChooser.showSaveDialog(frame);

                            if (userInput == JFileChooser.CANCEL_OPTION)
                                return;

                            FileOutputStream textRuStream = new FileOutputStream(textRuChooser.getSelectedFile().getAbsolutePath());

                            XWPFParagraph textRuParagraph = textRuDocument.createParagraph();
                            XWPFRun textRuRun = textRuParagraph.createRun();
                            textRuRun.setFontFamily("Times New Roman");
                            textRuRun.setFontSize(14);
                            textRuRun.setText("Проверенный текст:");
                            textRuRun.addBreak();
                            textRuRun.setText(text.getText());
                            textRuRun.addBreak();
                            textRuRun.addBreak();
                            textRuRun.setText("Уникальность: ");
                            textRuRun.setText(textRuUnique);
                            textRuRun.addBreak();
                            textRuRun.setText("Вода: " + textRuWater);
                            textRuRun.addBreak();
                            textRuRun.setText("Заспамленность: " + textRuSpam);
                            textRuRun.addBreak();

                            XWPFTable textRuTable = textRuDocument.createTable();

                            XWPFTableRow headerRow = textRuTable.getRow(0);

                            XWPFParagraph linkParagraph = headerRow.getCell(0).addParagraph();
                            setRun(linkParagraph.createRun(), "Times New Roman", 14, "000000", "Ссылка", true, false);

                            XWPFParagraph sameParagraph = headerRow.createCell().addParagraph();
                            setRun(sameParagraph.createRun(), "Times New Roman", 14, "000000", "Схожесть", true, false);

                            for (PlagiatLink link : textRuLinks) {

                                XWPFTableRow textRuResultRow = textRuTable.createRow();
                                textRuResultRow.getCell(0).setText(link.getLink());
                                textRuResultRow.getCell(1).setText(link.getSamePercent());
                            }

                            formatter = new SimpleDateFormat("dd.MM.yyyy");
                            date = new Date(System.currentTimeMillis());
                            String reportData = "Проверка качества текста;" +  textRuChooser.getSelectedFile().getAbsolutePath() + ";" + formatter.format(date) + "\n";
                            addToLastReports(reportData);

                            textRuDocument.write(textRuStream);
                            textRuDocument.close();
                        } catch (Exception ex) {

                            ex.printStackTrace();
                        }

                    }

                    JOptionPane.showMessageDialog(frame, "Отчеты успешно сохранены!", "Сообщение", JOptionPane.INFORMATION_MESSAGE);
                }
                else {

                    JOptionPane.showMessageDialog(frame, "Необходимо запустить хотя бы одну проверку текста", "Внимание!", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        JLabel textRuUniqueLabel = new JLabel("Уникальность: ");
        textRuUniqueLabel.setBounds(10, 33, 200, 20);
        JLabel textRuStatus = new JLabel("Ожидание запуска проверки...");
        textRuStatus.setBounds(10, 93, 250, 20);
        JLabel textRuSpamLabel = new JLabel("Заспамленность: ");
        textRuSpamLabel.setBounds(10, 73, 250, 20);
        JLabel textRuWaterLabel = new JLabel("Вода: ");
        textRuWaterLabel.setBounds(10, 53, 250, 20);
        JButton textRuReport = new JButton("Посмотреть подробный отчет...");
        textRuReport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFrame textRuReportFrame = new JFrame("SEO Booster - Отчет по уникальности text.ru");
                textRuReportFrame.setLayout(null);
                textRuReportFrame.setSize(600, 600);
                textRuReportFrame.setLocationRelativeTo(null);

                JLabel header = new JLabel("Результаты проверки в сервисе text.ru");
                header.setFont(header.getFont().deriveFont(Font.BOLD));
                header.setFont(header.getFont().deriveFont(20.0f));
                header.setBounds(100, 10, 400, 25);
                textRuReportFrame.add(header);

                JLabel summary = new JLabel("Уникальность текста " + textRuUnique + ", всего обнаружено " + textRuLinks.size() + " совпадений");
                summary.setFont(summary.getFont().deriveFont(Font.PLAIN));
                summary.setFont(summary.getFont().deriveFont(14.0f));
                summary.setBounds(10, 45, 600, 25);
                textRuReportFrame.add(summary);

                JLabel textRSL = new JLabel("Заспамленность: " + textRuSpam);
                textRSL.setFont(textRSL.getFont().deriveFont(Font.PLAIN));
                textRSL.setFont(textRSL.getFont().deriveFont(14.0f));
                textRSL.setBounds(10, 70, 600, 25);
                textRuReportFrame.add(textRSL);

                JLabel textRW = new JLabel("Вода: " + textRuWater);
                textRW.setFont(textRW.getFont().deriveFont(Font.PLAIN));
                textRW.setFont(textRW.getFont().deriveFont(14.0f));
                textRW.setBounds(10, 95, 600, 25);
                textRuReportFrame.add(textRW);

                JTable textRuTable = new JTable();
                textRuTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                JScrollPane textRuTablePane = new JScrollPane(textRuTable);
                DefaultTableModel tableModel = new DefaultTableModel();
                tableModel.addColumn("Ссылка");
                tableModel.addColumn("Схожесть");
                textRuTable.setModel(tableModel);
                textRuTable.getColumnModel().getColumn(0).setPreferredWidth(500);
                textRuTable.getColumnModel().getColumn(1).setPreferredWidth(70);

                textRuTable.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {

                        int row = textRuTable.rowAtPoint(new Point(e.getX(), e.getY()));
                        int col = textRuTable.columnAtPoint(new Point(e.getX(), e.getY()));

                        if (col == 0) {

                            String linkToOpen = (String) textRuTable.getValueAt(row, col);
                            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
                            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                                try {
                                    desktop.browse(new URI(linkToOpen.replaceAll("\\<.*?>", "") ));

                                } catch (Exception ex) {

                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
                });

                for (PlagiatLink link : textRuLinks) {

                    String url = link.getLink();
                    String percent = link.getSamePercent();
                    tableModel.addRow(new Object[]{"<html><font color='blue'>" + url + "</font></html>", percent});
                }

                textRuTablePane.setBounds(10, 130, 570, 400);
                textRuReportFrame.add(textRuTablePane);

                textRuReportFrame.setVisible(true);
            }
        });
        textRuReport.setBounds(10, 123, 260, 20);
        textRuReport.setEnabled(false);
        textRuPanel.add(textRuUniqueLabel);
        textRuPanel.add(textRuStatus);
        textRuPanel.add(textRuSpamLabel);
        textRuPanel.add(textRuWaterLabel);
        textRuPanel.add(textRuReport);

        JButton startCheck = new JButton("Проверить");
        startCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (text.getText().length() > 3000) {

                    JOptionPane.showMessageDialog(frame, "Длина текста не может превышать 3000 символов", "Слишком длинный текст", JOptionPane.ERROR_MESSAGE);
                } else if (text.getText().isEmpty()) {

                    JOptionPane.showMessageDialog(frame, "Введите текст для проверки", "Пустое поле для текста", JOptionPane.ERROR_MESSAGE);
                } else {

                    if ((!etxt.isSelected()) && (!textRu.isSelected())) {

                        JOptionPane.showMessageDialog(frame, "Выберите хотя бы одну систему проверки", "Не выбрана система проверки", JOptionPane.ERROR_MESSAGE);
                    } else {

                        checker = new TextQualityChecker(text.getText());

                        if (etxt.isSelected()) {
                            checker.checkUniqueEtxt();

                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    Timer etxtTimer = new Timer(3000, new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {

                                            EtxtTextUnique unique = checker.getEtxtStatus();
                                            if (unique.getMsg().equals("in progress")) {

                                                etxtProgressBar.setValue(Integer.valueOf(unique.getCheckPercent().replaceAll("%", "")));
                                                etxtStatus.setText(unique.getQueue());

                                            }

                                            if (unique.getMsg().equals("success")) {

                                                etxtProgressBar.setValue(100);
                                                etxtStatus.setText("Завершено");
                                                etxtUniqueLabel.setText("Уникальность: " + unique.getUnique());
                                                etxtUnique = unique.getUnique();
                                                etxtLinks = unique.getLink();
                                                etxtReport.setEnabled(true);

                                                ((Timer) e.getSource()).stop();
                                            }

                                            if (unique.getMsg().equals("error")) {

                                                etxtProgressBar.setValue(0);
                                                etxtStatus.setText("Произошла ошибка");
                                                ((Timer) e.getSource()).stop();

                                            }

                                        }
                                    });

                                    etxtTimer.start();
                                }
                            });

                            thread.start();

                        }
                        if (textRu.isSelected()) {

                            checker.checkUniqueTextRu();

                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    Timer textRuTimer = new Timer(3000, new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {

                                            TextRuUnique unique = checker.getTextRuStatus();
                                            if (unique.getStatus().equals("in progress")) {

                                                textRuStatus.setText(unique.getQueue());

                                            }

                                            if (unique.getStatus().equals("success")) {

                                                textRuStatus.setText("Завершено");
                                                textRuUniqueLabel.setText("Уникальность: " + unique.getUnique());
                                                textRuSpamLabel.setText("Заспамленность: " + unique.getSpam());
                                                textRuWaterLabel.setText("Вода: " + unique.getWater());
                                                textRuUnique = unique.getUnique();
                                                textRuSpam = unique.getSpam();
                                                textRuWater = unique.getWater();
                                                textRuLinks = unique.getLinks();
                                                textRuReport.setEnabled(true);

                                                ((Timer) e.getSource()).stop();
                                            }

                                            if (unique.getStatus().equals("error")) {

                                                textRuStatus.setText("Произошла ошибка");
                                                ((Timer) e.getSource()).stop();

                                            }

                                        }
                                    });

                                    textRuTimer.start();
                                }
                            });

                            thread.start();

                        }

                    }


                }
            }
        });
        systemsCheckboxes.add(startCheck);


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
