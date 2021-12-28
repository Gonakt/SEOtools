import org.apache.poi.xwpf.usermodel.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ValidationWindow {

    ValidationChecker checker;
    List<RobotsValidationIssue> issues = new ArrayList<>();
    List<HTMLValidationIssue> htmlIssues = new ArrayList<>();
    List<CSSValidationIssue> cssIssues = new ArrayList<>();

    ValidationWindow() {

        JFrame frame = new JFrame("SEO Booster - Валидация технических файлов");
        frame.setSize(700, 790);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
       // frame.setLayout(null);
        frame.setLocationRelativeTo(null);

        JPanel robotsValidationTab = new JPanel();
        robotsValidationTab.setLayout(null);

        JPanel robotsValidationContent = new JPanel();
        robotsValidationContent.setLayout(null);
        robotsValidationContent.setBounds(10, 40, 660, 700);
        robotsValidationTab.add(robotsValidationContent);

        generateRobotsInterface(robotsValidationContent, robotsValidationTab);

        JPanel sitemapValidationTab = new JPanel();
        sitemapValidationTab.setLayout(null);

        JPanel sitemapValidationContent = new JPanel();
        sitemapValidationContent.setLayout(null);
        sitemapValidationContent.setBounds(10, 40, 660, 700);
        sitemapValidationTab.add(sitemapValidationContent);

        generateSitemapInterface(sitemapValidationContent, sitemapValidationTab);

        JPanel htmlValidationTab = new JPanel();
        htmlValidationTab.setLayout(null);

        JPanel htmlValidationContent = new JPanel();
        htmlValidationContent.setLayout(null);
        htmlValidationContent.setBounds(10, 40, 660, 700);
        htmlValidationTab.add(htmlValidationContent);

        generateHtmlInterface(htmlValidationContent, htmlValidationTab);

        JPanel cssValidationTab = new JPanel();
        cssValidationTab.setLayout(null);

        JPanel cssValidationContent = new JPanel();
        cssValidationContent.setLayout(null);
        cssValidationContent.setBounds(10, 40, 660, 700);
        cssValidationTab.add(cssValidationContent);

        generateCssInterface(cssValidationContent, cssValidationTab);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add(robotsValidationTab, "Валидация robots.txt");
        tabs.add(sitemapValidationTab, "Валидация sitemap.xml");
        tabs.add(htmlValidationTab, "Валидация HTML");
        tabs.add(cssValidationTab, "Валидация CSS");

        frame.add(tabs);
        frame.setVisible(true);
    }

    void setAdvancedBorder(JPanel panel) {

        LineBorder line = new LineBorder(Color.GRAY, 2);
        panel.setBorder(line);
    }

    void setFontSize(JLabel label) {

        label.setFont(label.getFont().deriveFont(14.0f));
    }

    void generateRobotsInterface(JPanel panel, JPanel panel1) {

        JTextField robotsSiteAddress = new JTextField("Введите адрес сайта...");
        robotsSiteAddress.setBounds(10, 10, 500, 20);
        panel1.add(robotsSiteAddress);
        robotsSiteAddress.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

                robotsSiteAddress.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });

        JButton checkTechnical = new JButton("Проверить");
        checkTechnical.setBounds(520, 10, 140, 20);
        panel1.add(checkTechnical);

        JLabel checkResult = new JLabel("<html><b>Результат проверки: </b></html>");
        setFontSize(checkResult);

        ImageIcon warningsIcon = new ImageIcon("warning.png");
        JLabel warningsIconHolder = new JLabel(warningsIcon);
        warningsIconHolder.setBounds(10, 80, 32, 32);
        JLabel robotWarnings = new JLabel("<html><b>Предупреждения: </b></html>");
        setFontSize(robotWarnings);

        ImageIcon errorsIcon = new ImageIcon("error.png");
        JLabel errorsIconHolder = new JLabel(errorsIcon);
        errorsIconHolder.setBounds(10, 50, 32, 32);
        JLabel robotErrors = new JLabel("<html><b>Ошибки: </b></html>");
        setFontSize(robotErrors);

        checkResult.setBounds(10, 20, 390, 20);
        robotErrors.setBounds(52, 50, 300, 20);
        robotWarnings.setBounds(52, 80, 300, 20);

        panel.add(checkResult);
        panel.add(robotWarnings);
        panel.add(robotErrors);
        panel.add(warningsIconHolder);
        panel.add(errorsIconHolder);

        JLabel thisIsContentLabel = new JLabel("Содержимое robots.txt:");
        thisIsContentLabel.setBounds(10, 120, 200, 20);
        panel.add(thisIsContentLabel);

        JTextArea robotsContent = new JTextArea("");
        JScrollPane robotsPane = new JScrollPane(robotsContent);
        robotsPane.setBounds(10, 150, 640, 200);
        panel.add(robotsPane);

        JLabel thisIsErrors = new JLabel("Ошибки и предупреждения:");
        thisIsErrors.setBounds(10, 360, 200, 20);
        panel.add(thisIsErrors);

        JTable robotsTable = new JTable();
        robotsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane robotsTablePane = new JScrollPane(robotsTable);
        DefaultTableModel robotsTableModel = new DefaultTableModel();
        robotsTableModel.addColumn("Строка");
        robotsTableModel.addColumn("Тип");
        robotsTableModel.addColumn("Проблема");
        robotsTable.setModel(robotsTableModel);
        robotsTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        robotsTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        robotsTable.getColumnModel().getColumn(2).setPreferredWidth(500);
        robotsTablePane.setBounds(10, 390, 640, 250);
        panel.add(robotsTablePane);

        checkTechnical.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (robotsSiteAddress.getText().isEmpty() || robotsSiteAddress.getText().equals("Введите адрес сайта...")) {

                    JOptionPane.showMessageDialog(panel, "Введите адрес сайта", "Ошибка", JOptionPane.ERROR_MESSAGE);

                }
                else {

                    Thread robotsThread = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            issues.clear();
                            checker = new ValidationChecker(robotsSiteAddress.getText());


                            if (checker.isRobotsTxtExists()) {

                                issues = checker.validateRobotsTxt();

                                robotWarnings.setText("<html><b>Предупреждения: </b>" + String.valueOf(checker.getRobotsWarningsNum()) + "</html>");
                                robotErrors.setText("<html><b>Ошибки: </b>" + String.valueOf(checker.getRobotsErrorsNum()) + "</html>");
                                robotsContent.setText(checker.getRobotsTxtSource());

                                if (checker.getRobotsWarningsNum() == 0 && checker.getRobotsErrorsNum() == 0)
                                    checkResult.setText("<html><b>Результат проверки: </b>robots.txt корректен</html>");
                                else
                                    checkResult.setText("<html><b>Результат проверки: </b>есть ошибки/предупреждения</html>");

                                for (RobotsValidationIssue issue : issues) {

                                    robotsTableModel.addRow(new Object[]{issue.getRow(), issue.getType(), issue.getDescription()});
                                }

                            } else {

                                JOptionPane.showMessageDialog(panel, "Файл robots.txt отсутствует или недоступен", "Ошибка", JOptionPane.ERROR_MESSAGE);
                            }

                        }
                    });

                    robotsThread.start();

                }
            }
        });

        JButton saveRobotsReport = new JButton("Сохранить отчет в Word");
        saveRobotsReport.setBounds(450, 650, 200, 20);
        saveRobotsReport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy'_'HHmmss");
                    Date date = new Date(System.currentTimeMillis());

                    JFileChooser robotsChooser = new JFileChooser();
                    robotsChooser.setDialogTitle("Сохранить отчет...");
                    robotsChooser.setSelectedFile(new File("robotstxt_report_" + formatter.format(date) + ".docx"));

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
                            robotsChooser.setCurrentDirectory(new File(defaultPath));
                        }
                        catch (Exception ex) {

                            ex.printStackTrace();
                        }


                    }


                    int userInput = robotsChooser.showSaveDialog(panel);

                    if (userInput == JFileChooser.CANCEL_OPTION)
                        return;

                    FileOutputStream robotsTxtStream = new FileOutputStream(robotsChooser.getSelectedFile().getAbsolutePath());

                    XWPFDocument robotsTxtDocument = new XWPFDocument();

                    formatter = new SimpleDateFormat("dd.MM.yyyy 'в' HH:mm:ss");
                    date = new Date(System.currentTimeMillis());

                    XWPFParagraph robotsTxtParagraph = robotsTxtDocument.createParagraph();
                    XWPFRun robotsTxtRun = robotsTxtParagraph.createRun();
                    robotsTxtRun.setFontFamily("Times New Roman");
                    robotsTxtRun.setFontSize(14);
                    robotsTxtRun.setText("Дата и время проверки: " + formatter.format(date));
                    robotsTxtRun.addBreak();
                    robotsTxtRun.setText(checkResult.getText().replaceAll("\\<.*?>", ""));
                    robotsTxtRun.addBreak();
                    robotsTxtRun.setText(robotErrors.getText().replaceAll("\\<.*?>", "") );
                    robotsTxtRun.addBreak();
                    robotsTxtRun.setText(robotWarnings.getText().replaceAll("\\<.*?>", ""));
                    robotsTxtRun.addBreak();
                    robotsTxtRun.setText("Ссылка на файл: " + robotsSiteAddress.getText() + "/robots.txt");
                    robotsTxtRun.addBreak();
                    robotsTxtRun.addBreak();
                    robotsTxtRun.setText("Содержимое файла на момент проверки:");
                    robotsTxtRun.addBreak();
                    robotsTxtRun.addBreak();

                    String[] robotsContentStr = robotsContent.getText().split("\n");
                    for (int i = 0; i < robotsContentStr.length; i++) {

                        robotsTxtRun.setText(robotsContentStr[i]);
                        robotsTxtRun.addBreak();
                    }

                    robotsTxtRun.addBreak();

                    if (checker.getRobotsErrorsNum() != 0 || checker.getRobotsWarningsNum() != 0) {

                        XWPFTable etxtTable = robotsTxtDocument.createTable();

                        XWPFTableRow headerRow = etxtTable.getRow(0);

                        XWPFParagraph numberParagraph = headerRow.getCell(0).addParagraph();
                        setRun(numberParagraph.createRun(), "Times New Roman", 14, "000000", "Номер строки", true, false);

                        XWPFParagraph typeParagraph = headerRow.createCell().addParagraph();
                        setRun(typeParagraph.createRun(), "Times New Roman", 14, "000000", "Тип", true, false);

                        XWPFParagraph descriptionParagraph = headerRow.createCell().addParagraph();
                        setRun(descriptionParagraph.createRun(), "Times New Roman", 14, "000000", "Проблема", true, false);

                        for (RobotsValidationIssue issue : issues) {

                            XWPFTableRow robotsResultRow = etxtTable.createRow();
                            robotsResultRow.getCell(0).setText(issue.getRow());
                            robotsResultRow.getCell(1).setText(issue.getType());
                            robotsResultRow.getCell(2).setText(issue.getDescription());

                        }
                    }

                    formatter = new SimpleDateFormat("dd.MM.yyyy");
                    date = new Date(System.currentTimeMillis());
                    String reportData = "Валидация технических файлов;" +  robotsChooser.getSelectedFile().getAbsolutePath() + ";" + formatter.format(date) + "\n";
                    addToLastReports(reportData);

                    robotsTxtDocument.write(robotsTxtStream);
                    robotsTxtDocument.close();
                    robotsTxtStream.close();

                    JOptionPane.showMessageDialog(panel, "Отчет успешно сохранен!", "Уведомление", JOptionPane.INFORMATION_MESSAGE);

                }

                catch (Exception ex) {

                    ex.printStackTrace();
                }
            }
        });

        panel.add(saveRobotsReport);


    }

    void generateSitemapInterface(JPanel panel, JPanel panel1) {

        JTextField robotsSiteAddress = new JTextField("Введите адрес сайта...");
        robotsSiteAddress.setBounds(10, 10, 500, 20);
        panel1.add(robotsSiteAddress);
        robotsSiteAddress.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

                robotsSiteAddress.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });

        JButton checkTechnical = new JButton("Проверить");
        checkTechnical.setBounds(520, 10, 140, 20);
        panel1.add(checkTechnical);

        JLabel checkResult = new JLabel("<html><b>Результат проверки: </b></html>");
        setFontSize(checkResult);

        ImageIcon warningsIcon = new ImageIcon("warning.png");
        JLabel warningsIconHolder = new JLabel(warningsIcon);
        warningsIconHolder.setBounds(10, 80, 32, 32);
        JLabel robotWarnings = new JLabel("<html><b>Предупреждения: </b></html>");
        setFontSize(robotWarnings);

        ImageIcon errorsIcon = new ImageIcon("error.png");
        JLabel errorsIconHolder = new JLabel(errorsIcon);
        errorsIconHolder.setBounds(10, 50, 32, 32);
        JLabel robotErrors = new JLabel("<html><b>Ошибки: </b></html>");
        setFontSize(robotErrors);

        checkResult.setBounds(10, 20, 390, 20);
        robotErrors.setBounds(52, 50, 300, 20);
        robotWarnings.setBounds(52, 80, 300, 20);

        panel.add(checkResult);
        panel.add(robotWarnings);
        panel.add(robotErrors);
        panel.add(warningsIconHolder);
        panel.add(errorsIconHolder);

        JLabel thisIsContentLabel = new JLabel("Содержимое sitemap.xml:");
        thisIsContentLabel.setBounds(10, 120, 200, 20);
        panel.add(thisIsContentLabel);

        JTextArea robotsContent = new JTextArea("");
        JScrollPane robotsPane = new JScrollPane(robotsContent);
        robotsPane.setBounds(10, 150, 640, 200);
        panel.add(robotsPane);

        JLabel thisIsErrors = new JLabel("Ошибки и предупреждения:");
        thisIsErrors.setBounds(10, 360, 200, 20);
        panel.add(thisIsErrors);

        JTable robotsTable = new JTable();
        robotsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane robotsTablePane = new JScrollPane(robotsTable);
        DefaultTableModel robotsTableModel = new DefaultTableModel();
        robotsTableModel.addColumn("Строка");
        robotsTableModel.addColumn("Тип");
        robotsTableModel.addColumn("Проблема");
        robotsTable.setModel(robotsTableModel);
        robotsTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        robotsTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        robotsTable.getColumnModel().getColumn(2).setPreferredWidth(500);
        robotsTablePane.setBounds(10, 390, 640, 250);
        panel.add(robotsTablePane);

        checkTechnical.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (robotsSiteAddress.getText().isEmpty() || robotsSiteAddress.getText().equals("Введите адрес сайта...")) {

                    JOptionPane.showMessageDialog(panel, "Введите адрес сайта", "Ошибка", JOptionPane.ERROR_MESSAGE);

                }
                else {

                    Thread robotsThread = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            checker = new ValidationChecker(robotsSiteAddress.getText());

                            if (checker.isSitemapExists()) {
                                if (checker.validateSitemapXml()) {

                                    checkResult.setText("<html><b>Результат проверки: </b>sitemap.xml корректен </html>");
                                    robotErrors.setText("<html><b>Ошибки: </b>0 </html>");
                                    robotWarnings.setText("<html><b>Предупреждения: </b>0 </html>");

                                }
                                else {

                                    checkResult.setText("<html><b>Результат проверки: </b>Есть предупреждения/ошибки </html>");
                                    robotErrors.setText("<html><b>Ошибки: </b>0 </html>");
                                    robotWarnings.setText("<html><b>Предупреждения: </b>0 </html>");
                                }

                                robotsContent.setText(checker.getSitemapXmlSource());

                            } else {

                                JOptionPane.showMessageDialog(panel, "Файл robots.txt отсутствует или недоступен", "Ошибка", JOptionPane.ERROR_MESSAGE);
                            }

                        }
                    });

                    robotsThread.start();

                }
            }
        });

        JButton saveSitemapReport = new JButton("Сохранить отчет в Word");
        saveSitemapReport.setBounds(450, 650, 200, 20);
        saveSitemapReport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy'_'HHmmss");
                    Date date = new Date(System.currentTimeMillis());

                    JFileChooser sitemapChooser = new JFileChooser();
                    sitemapChooser.setDialogTitle("Сохранить отчет...");
                    sitemapChooser.setSelectedFile(new File("sitemap_report_" + formatter.format(date) + ".docx"));
                    int userInput = sitemapChooser.showSaveDialog(panel);

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
                            sitemapChooser.setCurrentDirectory(new File(defaultPath));
                        }
                        catch (Exception ex) {

                            ex.printStackTrace();
                        }

                    }


                    if (userInput == JFileChooser.CANCEL_OPTION)
                        return;

                    FileOutputStream sitemapStream = new FileOutputStream(sitemapChooser.getSelectedFile().getAbsolutePath());

                    XWPFDocument sitemapDocument = new XWPFDocument();

                    formatter = new SimpleDateFormat("dd.MM.yyyy 'в' HH:mm:ss");
                    date = new Date(System.currentTimeMillis());

                    XWPFParagraph sitemapParagraph = sitemapDocument.createParagraph();
                    XWPFRun sitemapRun = sitemapParagraph.createRun();
                    sitemapRun.setFontFamily("Times New Roman");
                    sitemapRun.setFontSize(14);
                    sitemapRun.setText("Дата и время проверки: " + formatter.format(date));
                    sitemapRun.addBreak();
                    sitemapRun.setText(checkResult.getText().replaceAll("\\<.*?>", ""));
                    sitemapRun.addBreak();
                    sitemapRun.setText(robotErrors.getText().replaceAll("\\<.*?>", "") );
                    sitemapRun.addBreak();
                    sitemapRun.setText(robotWarnings.getText().replaceAll("\\<.*?>", ""));
                    sitemapRun.addBreak();
                    sitemapRun.setText("Ссылка на файл: " + robotsSiteAddress.getText() + "/sitemap.xml");
                    sitemapRun.addBreak();
                    sitemapRun.addBreak();
                    sitemapRun.setText("Содержимое файла на момент проверки:");
                    sitemapRun.addBreak();
                    sitemapRun.addBreak();

                    String[] sitemapContentStr = robotsContent.getText().split("\n");
                    for (int i = 0; i < sitemapContentStr.length; i++) {

                        sitemapRun.setText(sitemapContentStr[i]);
                        sitemapRun.addBreak();
                    }

                    formatter = new SimpleDateFormat("dd.MM.yyyy");
                    date = new Date(System.currentTimeMillis());
                    String reportData = "Валидация технических файлов;" +  sitemapChooser.getSelectedFile().getAbsolutePath() + ";" + formatter.format(date) + "\n";
                    addToLastReports(reportData);

                    sitemapDocument.write(sitemapStream);
                    sitemapDocument.close();
                    sitemapStream.close();

                    JOptionPane.showMessageDialog(panel, "Отчет успешно сохранен!", "Уведомление", JOptionPane.INFORMATION_MESSAGE);

                }

                catch (Exception ex) {

                    ex.printStackTrace();
                }
            }
        });

        panel.add(saveSitemapReport);

    }

    void generateHtmlInterface(JPanel panel, JPanel panel1) {

        JTextField htmlSiteAddress = new JTextField("Введите адрес страницы...");
        htmlSiteAddress.setBounds(10, 10, 500, 20);
        panel1.add(htmlSiteAddress);
        htmlSiteAddress.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

                htmlSiteAddress.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });

        JButton checkTechnical = new JButton("Проверить");
        checkTechnical.setBounds(520, 10, 140, 20);
        panel1.add(checkTechnical);

        JLabel checkResult = new JLabel("<html><b>Результат проверки: </b></html>");
        setFontSize(checkResult);

        ImageIcon warningsIcon = new ImageIcon("warning.png");
        JLabel warningsIconHolder = new JLabel(warningsIcon);
        warningsIconHolder.setBounds(10, 80, 32, 32);
        JLabel robotWarnings = new JLabel("<html><b>Предупреждения: </b></html>");
        setFontSize(robotWarnings);

        ImageIcon errorsIcon = new ImageIcon("error.png");
        JLabel errorsIconHolder = new JLabel(errorsIcon);
        errorsIconHolder.setBounds(10, 50, 32, 32);
        JLabel robotErrors = new JLabel("<html><b>Ошибки: </b></html>");
        setFontSize(robotErrors);

        checkResult.setBounds(10, 20, 390, 20);
        robotErrors.setBounds(52, 50, 300, 20);
        robotWarnings.setBounds(52, 80, 300, 20);

        panel.add(checkResult);
        panel.add(robotWarnings);
        panel.add(robotErrors);
        panel.add(warningsIconHolder);
        panel.add(errorsIconHolder);

        JLabel thisIsContentLabel = new JLabel("Содержимое документа:");
        thisIsContentLabel.setBounds(10, 120, 200, 20);
        panel.add(thisIsContentLabel);

        JTextArea robotsContent = new JTextArea("");
        JScrollPane robotsPane = new JScrollPane(robotsContent);
        robotsPane.setBounds(10, 150, 640, 200);
        panel.add(robotsPane);

        JLabel thisIsErrors = new JLabel("Ошибки и предупреждения:");
        thisIsErrors.setBounds(10, 360, 200, 20);
        panel.add(thisIsErrors);

        JTable robotsTable = new JTable();
        robotsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane robotsTablePane = new JScrollPane(robotsTable);
        DefaultTableModel robotsTableModel = new DefaultTableModel();
        robotsTableModel.addColumn("Строка");
        robotsTableModel.addColumn("Тип");
        robotsTableModel.addColumn("Проблема");
        robotsTable.setModel(robotsTableModel);
        robotsTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        robotsTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        robotsTable.getColumnModel().getColumn(2).setPreferredWidth(500);
        robotsTablePane.setBounds(10, 390, 640, 250);
        panel.add(robotsTablePane);

        checkTechnical.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (htmlSiteAddress.getText().isEmpty() || htmlSiteAddress.getText().equals("Введите адрес страницы...")) {

                    JOptionPane.showMessageDialog(panel, "Введите адрес страницы", "Ошибка", JOptionPane.ERROR_MESSAGE);

                }
                else {

                    Thread robotsThread = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            checker = new ValidationChecker(htmlSiteAddress.getText());

                            if (checker.isPageExists()) {

                                htmlIssues = checker.validateHTML();

                                if (htmlIssues == null) {

                                    checkResult.setText("<html><b>Результат проверки: </b>произошла ошибка </html>");
                                    robotErrors.setText("<html><b>Ошибки: </b>0 </html>");
                                    robotWarnings.setText("<html><b>Предупреждения: </b>0 </html>");
                                    return;
                                }

                                if (checker.getHTMLErrorsNum() == 0 && checker.getHTMLWarningsNum() == 0) {

                                    checkResult.setText("<html><b>Результат проверки: </b>документ корректен </html>");
                                    robotErrors.setText("<html><b>Ошибки: </b>0 </html>");
                                    robotWarnings.setText("<html><b>Предупреждения: </b>0 </html>");
                                }
                                else {

                                    checkResult.setText("<html><b>Результат проверки: </b>Есть предупреждения/ошибки </html>");
                                    robotErrors.setText("<html><b>Ошибки: </b>" + checker.getHTMLErrorsNum() + "</html>");
                                    robotWarnings.setText("<html><b>Предупреждения: </b>" + checker.getHTMLWarningsNum() + "</html>");
                                }

                                for (HTMLValidationIssue issue : htmlIssues) {

                                    robotsTableModel.addRow(new Object[]{issue.getRow(), issue.getType(), issue.getDescription()});
                                }

                                robotsContent.setText(checker.getHtmlSource());

                            } else {

                                JOptionPane.showMessageDialog(panel, "Файл отсутствует на сервере или недоступен", "Ошибка", JOptionPane.ERROR_MESSAGE);
                            }

                        }
                    });

                    robotsThread.start();

                }
            }
        });

        JButton saveSitemapReport = new JButton("Сохранить отчет в Word");
        saveSitemapReport.setBounds(450, 650, 200, 20);
        saveSitemapReport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy'_'HHmmss");
                    Date date = new Date(System.currentTimeMillis());

                    JFileChooser htmlChooser = new JFileChooser();
                    htmlChooser.setDialogTitle("Сохранить отчет...");
                    htmlChooser.setSelectedFile(new File("html_report_" + formatter.format(date) + ".docx"));
                    int userInput = htmlChooser.showSaveDialog(panel);

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
                            htmlChooser.setCurrentDirectory(new File(defaultPath));
                        }
                        catch (Exception ex) {

                            ex.printStackTrace();
                        }

                    }


                    if (userInput == JFileChooser.CANCEL_OPTION)
                        return;

                    FileOutputStream htmlStream = new FileOutputStream(htmlChooser.getSelectedFile().getAbsolutePath());

                    XWPFDocument htmlDocument = new XWPFDocument();

                    formatter = new SimpleDateFormat("dd.MM.yyyy 'в' HH:mm:ss");
                    date = new Date(System.currentTimeMillis());

                    XWPFParagraph htmlParagraph = htmlDocument.createParagraph();
                    XWPFRun htmlRun = htmlParagraph.createRun();
                    htmlRun.setFontFamily("Times New Roman");
                    htmlRun.setFontSize(14);
                    htmlRun.setText("Дата и время проверки: " + formatter.format(date));
                    htmlRun.addBreak();
                    htmlRun.setText(checkResult.getText().replaceAll("\\<.*?>", ""));
                    htmlRun.addBreak();
                    htmlRun.setText(robotErrors.getText().replaceAll("\\<.*?>", "") );
                    htmlRun.addBreak();
                    htmlRun.setText(robotWarnings.getText().replaceAll("\\<.*?>", ""));
                    htmlRun.addBreak();
                    htmlRun.setText("Ссылка на файл: " + htmlSiteAddress.getText());
                    htmlRun.addBreak();
                    htmlRun.addBreak();
                    htmlRun.setText("Содержимое файла на момент проверки:");
                    htmlRun.addBreak();
                    htmlRun.addBreak();

                    if (checker.getHTMLErrorsNum() != 0 || checker.getHTMLWarningsNum() != 0) {

                        XWPFTable etxtTable = htmlDocument.createTable();

                        XWPFTableRow headerRow = etxtTable.getRow(0);

                        XWPFParagraph numberParagraph = headerRow.getCell(0).addParagraph();
                        setRun(numberParagraph.createRun(), "Times New Roman", 14, "000000", "Номер строки", true, false);

                        XWPFParagraph typeParagraph = headerRow.createCell().addParagraph();
                        setRun(typeParagraph.createRun(), "Times New Roman", 14, "000000", "Тип", true, false);

                        XWPFParagraph descriptionParagraph = headerRow.createCell().addParagraph();
                        setRun(descriptionParagraph.createRun(), "Times New Roman", 14, "000000", "Проблема", true, false);

                        for (HTMLValidationIssue issue : htmlIssues) {

                            XWPFTableRow robotsResultRow = etxtTable.createRow();
                            robotsResultRow.getCell(0).setText(issue.getRow());
                            robotsResultRow.getCell(1).setText(issue.getType());
                            robotsResultRow.getCell(2).setText(issue.getDescription());

                        }
                    }

                    formatter = new SimpleDateFormat("dd.MM.yyyy");
                    date = new Date(System.currentTimeMillis());
                    String reportData = "Валидация технических файлов;" +  htmlChooser.getSelectedFile().getAbsolutePath() + ";" + formatter.format(date) + "\n";
                    addToLastReports(reportData);

                    htmlDocument.write(htmlStream);
                    htmlDocument.close();
                    htmlStream.close();

                    JOptionPane.showMessageDialog(panel, "Отчет успешно сохранен!", "Уведомление", JOptionPane.INFORMATION_MESSAGE);

                }

                catch (Exception ex) {

                    ex.printStackTrace();
                }
            }
        });

        panel.add(saveSitemapReport);

    }

    void generateCssInterface(JPanel panel, JPanel panel1) {

        JTextField cssSiteAddress = new JTextField("Введите адрес страницы...");
        cssSiteAddress.setBounds(10, 10, 500, 20);
        panel1.add(cssSiteAddress);
        cssSiteAddress.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

                cssSiteAddress.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });

        JButton checkTechnical = new JButton("Проверить");
        checkTechnical.setBounds(520, 10, 140, 20);
        panel1.add(checkTechnical);

        JLabel checkResult = new JLabel("<html><b>Результат проверки: </b></html>");
        setFontSize(checkResult);

        ImageIcon warningsIcon = new ImageIcon("warning.png");
        JLabel warningsIconHolder = new JLabel(warningsIcon);
        warningsIconHolder.setBounds(10, 80, 32, 32);
        JLabel cssWarnings = new JLabel("<html><b>Предупреждения: </b></html>");
        setFontSize(cssWarnings);

        ImageIcon errorsIcon = new ImageIcon("error.png");
        JLabel errorsIconHolder = new JLabel(errorsIcon);
        errorsIconHolder.setBounds(10, 50, 32, 32);
        JLabel cssErrors = new JLabel("<html><b>Ошибки: </b></html>");
        setFontSize(cssErrors);

        checkResult.setBounds(10, 20, 390, 20);
        cssErrors.setBounds(52, 50, 300, 20);
        cssWarnings.setBounds(52, 80, 300, 20);

        panel.add(checkResult);
        panel.add(cssWarnings);
        panel.add(cssErrors);
        panel.add(warningsIconHolder);
        panel.add(errorsIconHolder);

        JLabel thisIsContentLabel = new JLabel("Содержимое документов CSS:");
        thisIsContentLabel.setBounds(10, 120, 200, 20);
        panel.add(thisIsContentLabel);

        JTextArea robotsContent = new JTextArea("");
        JScrollPane robotsPane = new JScrollPane(robotsContent);
        robotsPane.setBounds(10, 150, 640, 200);
        panel.add(robotsPane);

        JLabel thisIsErrors = new JLabel("Ошибки и предупреждения:");
        thisIsErrors.setBounds(10, 360, 200, 20);
        panel.add(thisIsErrors);

        JTable robotsTable = new JTable();
        robotsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane robotsTablePane = new JScrollPane(robotsTable);
        DefaultTableModel robotsTableModel = new DefaultTableModel();
        robotsTableModel.addColumn("Документ");
        robotsTableModel.addColumn("Строка");
        robotsTableModel.addColumn("Селектор");
        robotsTableModel.addColumn("Тип");
        robotsTableModel.addColumn("Проблема");

        robotsTable.setModel(robotsTableModel);
        robotsTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        robotsTable.getColumnModel().getColumn(1).setPreferredWidth(50);
        robotsTable.getColumnModel().getColumn(2).setPreferredWidth(170);
        robotsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        robotsTable.getColumnModel().getColumn(4).setPreferredWidth(200);
        robotsTablePane.setBounds(10, 390, 640, 250);
        panel.add(robotsTablePane);

        checkTechnical.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (cssSiteAddress.getText().isEmpty() || cssSiteAddress.getText().equals("Введите адрес страницы...")) {

                    JOptionPane.showMessageDialog(panel, "Введите адрес страницы", "Ошибка", JOptionPane.ERROR_MESSAGE);

                }
                else {

                    Thread robotsThread = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            checker = new ValidationChecker(cssSiteAddress.getText());

                            if (checker.isPageExists()) {

                                cssIssues = checker.validateCSS();

                                if (cssIssues == null) {

                                    checkResult.setText("<html><b>Результат проверки: </b>произошла ошибка </html>");
                                    cssErrors.setText("<html><b>Ошибки: </b>0 </html>");
                                    cssWarnings.setText("<html><b>Предупреждения: </b>0 </html>");
                                    return;
                                }

                                if (checker.getCssErrorsNum() == 0 && checker.getCssWarningsNum() == 0) {

                                    checkResult.setText("<html><b>Результат проверки: </b>ошибок и предупреждений нет </html>");
                                    cssErrors.setText("<html><b>Ошибки: </b>0 </html>");
                                    cssWarnings.setText("<html><b>Предупреждения: </b>0 </html>");
                                }
                                else {

                                    checkResult.setText("<html><b>Результат проверки: </b>Есть предупреждения/ошибки </html>");
                                    cssErrors.setText("<html><b>Ошибки: </b>" + checker.getCssErrorsNum() + "</html>");
                                    cssWarnings.setText("<html><b>Предупреждения: </b>" + checker.getCssWarningsNum() + "</html>");
                                }

                                for (CSSValidationIssue issue : cssIssues) {

                                    robotsTableModel.addRow(new Object[]{issue.getAddress(), issue.getRow(), issue.getElement(), issue.getType(), issue.getDescription()});
                                }

                                robotsContent.setText(checker.getCssSource());

                            } else {

                                JOptionPane.showMessageDialog(panel, "Файл отсутствует на сервере или недоступен", "Ошибка", JOptionPane.ERROR_MESSAGE);
                            }

                        }
                    });

                    robotsThread.start();

                }
            }
        });

        JButton saveSitemapReport = new JButton("Сохранить отчет в Word");
        saveSitemapReport.setBounds(450, 650, 200, 20);
        saveSitemapReport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy'_'HHmmss");
                    Date date = new Date(System.currentTimeMillis());

                    JFileChooser htmlChooser = new JFileChooser();
                    htmlChooser.setDialogTitle("Сохранить отчет...");
                    htmlChooser.setSelectedFile(new File("css_report_" + formatter.format(date) + ".docx"));
                    int userInput = htmlChooser.showSaveDialog(panel);

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
                            htmlChooser.setCurrentDirectory(new File(defaultPath));
                        }
                        catch (Exception ex) {

                            ex.printStackTrace();
                        }

                    }


                    if (userInput == JFileChooser.CANCEL_OPTION)
                        return;

                    FileOutputStream htmlStream = new FileOutputStream(htmlChooser.getSelectedFile().getAbsolutePath());

                    XWPFDocument htmlDocument = new XWPFDocument();

                    formatter = new SimpleDateFormat("dd.MM.yyyy 'в' HH:mm:ss");
                    date = new Date(System.currentTimeMillis());

                    XWPFParagraph htmlParagraph = htmlDocument.createParagraph();
                    XWPFRun htmlRun = htmlParagraph.createRun();
                    htmlRun.setFontFamily("Times New Roman");
                    htmlRun.setFontSize(14);
                    htmlRun.setText("Дата и время проверки: " + formatter.format(date));
                    htmlRun.addBreak();
                    htmlRun.setText(checkResult.getText().replaceAll("\\<.*?>", ""));
                    htmlRun.addBreak();
                    htmlRun.setText(cssErrors.getText().replaceAll("\\<.*?>", "") );
                    htmlRun.addBreak();
                    htmlRun.setText(cssWarnings.getText().replaceAll("\\<.*?>", ""));
                    htmlRun.addBreak();
                    htmlRun.setText("Ссылка: " + cssSiteAddress.getText());
                    htmlRun.addBreak();
                    htmlRun.addBreak();

                    if (checker.getCssErrorsNum() != 0 || checker.getCssWarningsNum() != 0) {

                        XWPFTable etxtTable = htmlDocument.createTable();

                        XWPFTableRow headerRow = etxtTable.getRow(0);

                        XWPFParagraph linkParagraph = headerRow.getCell(0).addParagraph();
                        setRun(linkParagraph.createRun(), "Times New Roman", 14, "000000", "Документ", true, false);

                        XWPFParagraph numberParagraph = headerRow.createCell().addParagraph();
                        setRun(numberParagraph.createRun(), "Times New Roman", 14, "000000", "Строка", true, false);

                        XWPFParagraph selectorParagraph = headerRow.createCell().addParagraph();
                        setRun(selectorParagraph.createRun(), "Times New Roman", 14, "000000", "Селектор", true, false);

                        XWPFParagraph typeParagraph = headerRow.createCell().addParagraph();
                        setRun(typeParagraph.createRun(), "Times New Roman", 14, "000000", "Тип", true, false);

                        XWPFParagraph descriptionParagraph = headerRow.createCell().addParagraph();
                        setRun(descriptionParagraph.createRun(), "Times New Roman", 14, "000000", "Проблема", true, false);

                        for (CSSValidationIssue issue : cssIssues) {

                            XWPFTableRow robotsResultRow = etxtTable.createRow();
                            robotsResultRow.getCell(0).setText(issue.getAddress());
                            robotsResultRow.getCell(1).setText(issue.getRow());
                            robotsResultRow.getCell(2).setText(issue.getElement());
                            robotsResultRow.getCell(3).setText(issue.getType());
                            robotsResultRow.getCell(4).setText(issue.getDescription());

                        }
                    }

                    formatter = new SimpleDateFormat("dd.MM.yyyy");
                    date = new Date(System.currentTimeMillis());
                    String reportData = "Валидация технических файлов;" +  htmlChooser.getSelectedFile().getAbsolutePath() + ";" + formatter.format(date) + "\n";
                    addToLastReports(reportData);

                    htmlDocument.write(htmlStream);
                    htmlDocument.close();
                    htmlStream.close();

                    JOptionPane.showMessageDialog(panel, "Отчет успешно сохранен!", "Уведомление", JOptionPane.INFORMATION_MESSAGE);

                }

                catch (Exception ex) {

                    ex.printStackTrace();
                }
            }
        });

        panel.add(saveSitemapReport);

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
