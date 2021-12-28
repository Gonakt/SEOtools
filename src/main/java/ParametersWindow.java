import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ParametersWindow {

    static ParametersChecker checker;
    static int completeSite = 0;
    static int completePage = 0;

    ParametersWindow() {

        generateParametersCheckerInterface();
    }

    private static void generateParametersCheckerInterface() {

        JFrame frame = new JFrame("Проверка параметров сайта");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600,610);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel wholeSite = new JPanel();
        wholeSite.setLayout(null);

        JTextArea address = new JTextArea("Введите адрес сайта...");
        address.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

                address.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });
        address.setBounds(20, 20, 400, 20);
        wholeSite.add(address);

        JLabel indexationLabel = new JLabel("Индексация");
        indexationLabel.setBounds(20, 50, 150, 25);
        makeBoldFont(indexationLabel);
        wholeSite.add(indexationLabel);

        JLabel yaXLabel = new JLabel("ИКС: ");
        yaXLabel.setBounds(20, 80, 50, 25);
        makePlainFont(yaXLabel);
        JLabel yaXData = new JLabel("");
        yaXData.setBounds(55, 80, 150, 25);
        makePlainFont(yaXData);
        wholeSite.add(yaXLabel);
        wholeSite.add(yaXData);

        JLabel pagesYaLabel = new JLabel("<html>Страницы в индексе <font color='red'>Я</font>ндекса: </html>");
        pagesYaLabel.setBounds(20, 100, 250, 25);
        makePlainFont(pagesYaLabel);
        JLabel pagesYaData = new JLabel("");
        pagesYaData.setBounds(225, 100, 150, 25);
        makePlainFont(pagesYaData);
        wholeSite.add(pagesYaLabel);
        wholeSite.add(pagesYaData);

        JLabel pagesGLabel = new JLabel("<html>Страницы в индексе <font color='blue'>G</font>oogle: </html>");
        pagesGLabel.setBounds(20, 120, 250, 25);
        makePlainFont(pagesGLabel);
        JLabel pagesGData = new JLabel("");
        pagesGData.setBounds(220, 120, 150, 25);
        makePlainFont(pagesGData);
        wholeSite.add(pagesGLabel);
        wholeSite.add(pagesGData);

        JLabel pagesBLabel = new JLabel("<html>Страницы в индексе <font color='#43AB6A'>B</font>ing: </html>");
        pagesBLabel.setBounds(20, 140, 250, 25);
        makePlainFont(pagesBLabel);
        JLabel pagesBData = new JLabel("");
        pagesBData.setBounds(200, 140, 150, 25);
        makePlainFont(pagesBData);
        wholeSite.add(pagesBLabel);
        wholeSite.add(pagesBData);

        JLabel imagesYaLabel = new JLabel("<html>Картинки в индексе <font color='red'>Я</font>ндекса: </html>");
        imagesYaLabel.setBounds(20, 160, 250, 25);
        makePlainFont(imagesYaLabel);
        JLabel imagesYaData = new JLabel("");
        imagesYaData.setBounds(220, 160, 150, 25);
        makePlainFont(imagesYaData);
        wholeSite.add(imagesYaLabel);
        wholeSite.add(imagesYaData);

        JLabel robotsTxtLabel = new JLabel("Наличие robots.txt: ");
        robotsTxtLabel.setBounds(20, 180, 250, 25);
        makePlainFont(robotsTxtLabel);
        JLabel robotsTxtData = new JLabel("");
        robotsTxtData.setBounds(150, 180, 150, 25);
        makePlainFont(robotsTxtData);
        wholeSite.add(robotsTxtLabel);
        wholeSite.add(robotsTxtData);

        JLabel sitemapXmlLabel = new JLabel("Наличие sitemap.xml: ");
        sitemapXmlLabel.setBounds(20, 200, 250, 25);
        makePlainFont(sitemapXmlLabel);
        JLabel sitemapXmlData = new JLabel("");
        sitemapXmlData.setBounds(165, 200, 150, 25);
        makePlainFont(sitemapXmlData);
        wholeSite.add(sitemapXmlLabel);
        wholeSite.add(sitemapXmlData);

        JLabel webArchiveLabel = new JLabel("Первая запись в WebArchive: ");
        webArchiveLabel.setBounds(20, 220, 250, 25);
        makePlainFont(webArchiveLabel);
        JLabel webArchiveData = new JLabel("");
        webArchiveData.setBounds(220, 220, 150, 25);
        makePlainFont(webArchiveData);
        wholeSite.add(webArchiveLabel);
        wholeSite.add(webArchiveData);

        JLabel linksLabel = new JLabel("Ссылки");
        linksLabel.setBounds(20, 250, 150, 25);
        makeBoldFont(linksLabel);
        wholeSite.add(linksLabel);

        JLabel linkpadLabel = new JLabel("Входящие/исходящие ссылки Linkpad: ");
        linkpadLabel.setBounds(20, 280, 270, 25);
        makePlainFont(linkpadLabel);
        JLabel linkpadData = new JLabel("");
        linkpadData.setBounds(280, 280, 150, 25);
        makePlainFont(linkpadData);
        wholeSite.add(linkpadLabel);
        wholeSite.add(linkpadData);

        JLabel bingLinksLabel = new JLabel("<html>Входящие ссылки <font color='#43AB6A'>B</font>ing: </html>");
        bingLinksLabel.setBounds(20, 300, 270, 25);
        makePlainFont(bingLinksLabel);
        JLabel bingLinksData = new JLabel("");
        bingLinksData.setBounds(180, 300, 150, 25);
        makePlainFont(bingLinksData);
        wholeSite.add(bingLinksLabel);
        wholeSite.add(bingLinksData);

        JLabel yaMentionsLabel = new JLabel("<html>Упоминания домена в <font color='red'>Я</font>ндексе: </html>");
        yaMentionsLabel.setBounds(20, 320, 270, 25);
        makePlainFont(yaMentionsLabel);
        JLabel yaMentionsData = new JLabel("");
        yaMentionsData.setBounds(235, 320, 150, 25);
        makePlainFont(yaMentionsData);
        wholeSite.add(yaMentionsLabel);
        wholeSite.add(yaMentionsData);

        JLabel technicalInfoLabel = new JLabel("Технические параметры");
        technicalInfoLabel.setBounds(20, 350, 250, 25);
        makeBoldFont(technicalInfoLabel);
        wholeSite.add(technicalInfoLabel);

        JLabel ipAddrLabel = new JLabel("IP адрес: ");
        ipAddrLabel.setBounds(20, 380, 270, 25);
        makePlainFont(ipAddrLabel);
        JLabel ipAddrData = new JLabel("");
        ipAddrData.setBounds(85, 380, 150, 25);
        makePlainFont(ipAddrData);
        wholeSite.add(ipAddrLabel);
        wholeSite.add(ipAddrData);

        JLabel hostingLabel = new JLabel("Хостинг: ");
        hostingLabel.setBounds(20, 400, 270, 25);
        makePlainFont(hostingLabel);
        JLabel hostingData = new JLabel("");
        hostingData.setBounds(80, 400, 150, 25);
        makePlainFont(hostingData);
        wholeSite.add(hostingLabel);
        wholeSite.add(hostingData);

        JLabel cmsLabel = new JLabel("CMS: ");
        cmsLabel.setBounds(20, 420, 270, 25);
        makePlainFont(cmsLabel);
        JLabel cmsData = new JLabel("");
        cmsData.setBounds(58, 420, 150, 25);
        makePlainFont(cmsData);
        wholeSite.add(cmsLabel);
        wholeSite.add(cmsData);

        JLabel allSitesLabel = new JLabel("Все сайты на этом IP: ");
        allSitesLabel.setBounds(20, 440, 270, 25);
        makePlainFont(allSitesLabel);
        JButton allSitesButton = new JButton("Узнать");
        allSitesButton.setBounds(170, 445, 100, 20);
        allSitesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                if (checker == null)
                    checker = new ParametersChecker(address.getText());
                if (address.getText().isEmpty()) {

                    JOptionPane.showMessageDialog(frame, "Введите адрес сайта", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
                else {

                    JOptionPane.showMessageDialog(frame, "Результат проверки появится в отдельном окне", "Внимание!", JOptionPane.INFORMATION_MESSAGE);
                    allSitesButton.setEnabled(false);

                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            JFrame allSitesFrame = new JFrame("Все сайты на IP");
                            allSitesFrame.setSize(400, 400);
                            allSitesFrame.setLocationRelativeTo(null);

                            JTextArea listOfSites = new JTextArea();
                            listOfSites.setEditable(false);
                            List<String> sites = checker.fetchSitesByIP();

                            listOfSites.append("Всего найдено " + String.valueOf(sites.size()) + " сайтов на этом веб-сервере\n\n");
                            for (String site : sites) {

                                listOfSites.append(site + "\n");
                            }

                            JScrollPane scroll = new JScrollPane(listOfSites);
                            scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                            allSitesFrame.add(scroll);

                            allSitesFrame.setVisible(true);

                            allSitesButton.setEnabled(false);
                        }
                    });

                    t.start();
                }
            }
        });
        wholeSite.add(allSitesLabel);
        wholeSite.add(allSitesButton);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setBounds(20, 480, 550, 20);
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        wholeSite.add(progressBar);

        JButton checkSite = new JButton("Проверить");
        checkSite.setBounds(440, 20, 130, 20);
        wholeSite.add(checkSite);
        checkSite.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (address.getText().isEmpty()) {

                    JOptionPane.showMessageDialog(frame, "Введите адрес сайта", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
                else {

                    JOptionPane.showMessageDialog(frame, "Возможно, в течение проверки сайта вам придется ввести капчу несколько раз", "Внимание!", JOptionPane.INFORMATION_MESSAGE);
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            completeSite = 0;

                            checkSite.setEnabled(false);

                            Instant start = Instant.now();

                            long beforeUsedMem = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()) / 1024 / 1024;
                            System.out.println("Занято памяти: " + beforeUsedMem + " МБ");

                            checker = new ParametersChecker(address.getText());

                            String yaX = String.valueOf(checker.fetchYaX());
                            if (yaX.equals("-1"))
                                yaXData.setText("Ошибка");
                            else
                                yaXData.setText(yaX);
                            updateProgressBar(progressBar, 14);

                            System.out.println("Параметр 1: " + String.valueOf((Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()) / 1024 / 1024) + " МБ");

                            String pagesYa = String.valueOf(checker.fetchYaPages());
                            if (pagesYa.equals("-1"))
                                pagesYaData.setText("Ошибка");
                            else
                                pagesYaData.setText(pagesYa);
                            updateProgressBar(progressBar, 14);

                            System.out.println("Параметр 2: " + String.valueOf((Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()) / 1024 / 1024) + " МБ");

                            String pagesG = String.valueOf(checker.fetchGooglePages());
                            if (pagesG.equals("-1"))
                                pagesGData.setText("Ошибка");
                            else
                                pagesGData.setText(pagesG);
                            updateProgressBar(progressBar, 14);

                            System.out.println("Параметр 3: " + String.valueOf((Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()) / 1024 / 1024) + " МБ");

                            int bp = checker.fetchBingPages();
                            if (bp == -1)
                                pagesBData.setText("Ошибка");
                            else
                                pagesBData.setText(String.valueOf(checker.fetchBingPages()));
                            updateProgressBar(progressBar, 14);

                            System.out.println("Параметр 4: " + String.valueOf((Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()) / 1024 / 1024) + " МБ");

                            String imagesYa = String.valueOf(checker.fetchYaImages());
                            if (imagesYa.equals("-1"))
                                imagesYaData.setText("Ошибка");
                            else
                                imagesYaData.setText(imagesYa);
                            updateProgressBar(progressBar, 14);

                            System.out.println("Параметр 5: " + String.valueOf((Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()) / 1024 / 1024) + " МБ");

                            if (checker.fetchRobotsTxt())
                                robotsTxtData.setText("Да");
                            else
                                robotsTxtData.setText("Нет");
                            updateProgressBar(progressBar, 14);

                            if (checker.fetchSitemapXml())
                                sitemapXmlData.setText("Да");
                            else
                                sitemapXmlData.setText("Нет");
                            updateProgressBar(progressBar, 14);

                            webArchiveData.setText(checker.fetchWebArchiveData());
                            updateProgressBar(progressBar, 14);

                            String in = String.valueOf(checker.fetchLinkpadIncomingLinks());
                            String out = String.valueOf(checker.fetchLinkpadOutcomingLinks());
                            if (in.equals("-1"))
                                in = "неизвестно";
                            if (out.equals("-1"))
                                out = "неизвестно";
                            linkpadData.setText("<html><font color='green'>" + in + "</font>/" + "<font color='red'>" + out + "</font></html>");
                            updateProgressBar(progressBar, 14);

                            String bingLinks = String.valueOf(checker.fetchBingIncomingLinks());
                            if (bingLinks.equals("-1"))
                                bingLinksData.setText("неизвестно");
                            else
                                bingLinksData.setText(bingLinks);
                            updateProgressBar(progressBar, 14);

                            String yaMentions = String.valueOf(checker.fetchYaMentionings());
                            if (yaMentions.equals("-1"))
                                yaMentionsData.setText("неизвестно");
                            else
                                yaMentionsData.setText(yaMentions);
                            updateProgressBar(progressBar, 14);

                            String addr = checker.fetchSiteAddress();
                            if (addr.equals("unknown"))
                                addr = "неизвестно";
                            ipAddrData.setText(addr);
                            updateProgressBar(progressBar, 14);

                            String hosting = checker.fetchHosting();
                            if (hosting.isEmpty())
                                hosting = "неизвестно";
                            hostingData.setText(hosting);
                            updateProgressBar(progressBar, 14);

                            String cms = checker.fetchCMS();
                            if (cms.equals("unknown"))
                                cms = "неизвестно";
                            cmsData.setText(cms);
                            updateProgressBar(progressBar, 14);

                            checkSite.setEnabled(true);

                            Instant finish = Instant.now();

                            long timeElapsed = Duration.between(start, finish).toMillis();

                            Date date = new Date(timeElapsed);
                            DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                            String dateFormatted = formatter.format(date);

                            //System.out.println("Время выполнения: " + dateFormatted);

                            System.out.println("Окончание работы алгоритма: " + String.valueOf((Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()) / 1024 / 1024) + " МБ");
                        }
                    });

                    t.start();

                }
            }
        });

        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {


            }

            @Override
            public void windowClosing(WindowEvent e) {

                if (!(checker == null))
                    checker.getDriver().quit();
                System.out.println("shutdown");

            }


            @Override
            public void windowClosed(WindowEvent e) {


            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });

        JPanel page = new JPanel();
        page.setLayout(null);

        JTextArea pageAddress = new JTextArea("Введите адрес страницы...");
        pageAddress.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

                pageAddress.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });
        pageAddress.setBounds(20, 20, 400, 20);
        page.add(pageAddress);

        JLabel pageParamsLabel = new JLabel("Основные параметры страницы");
        pageParamsLabel.setBounds(20, 50, 500, 25);
        makeBoldFont(pageParamsLabel);
        page.add(pageParamsLabel);

        JLabel googleIndexLabel = new JLabel();
        googleIndexLabel.setText("<html>Наличие в индексе <font color='blue'>G</font>oogle:</html>");
        googleIndexLabel.setBounds(20, 80, 250, 25);
        JLabel googleIndexData = new JLabel("");
        googleIndexData.setBounds(205, 80, 30, 25);
        makePlainFont(googleIndexLabel);
        makePlainFont(googleIndexData);
        page.add(googleIndexLabel);
        page.add(googleIndexData);

        JLabel yaIndexLabel = new JLabel();
        yaIndexLabel.setText("<html>Наличие в индексе <font color='red'>Я</font>ндекса:</html>");
        yaIndexLabel.setBounds(20, 100, 250, 25);
        JLabel yaIndexData = new JLabel("");
        yaIndexData.setBounds(215, 100, 30, 25);
        makePlainFont(yaIndexLabel);
        makePlainFont(yaIndexData);
        page.add(yaIndexLabel);
        page.add(yaIndexData);

        JLabel responseCodeLabel = new JLabel();
        responseCodeLabel.setText("Код ответа страницы:");
        responseCodeLabel.setBounds(20, 120, 250, 25);
        JLabel responseCodeData = new JLabel("");
        responseCodeData.setBounds(170, 120, 100, 25);
        makePlainFont(responseCodeLabel);
        makePlainFont(responseCodeData);
        page.add(responseCodeLabel);
        page.add(responseCodeData);

        JLabel responseTimeLabel = new JLabel();
        responseTimeLabel.setText("Время ответа сервера:");
        responseTimeLabel.setBounds(20, 140, 250, 25);
        JLabel responseTimeData = new JLabel("");
        responseTimeData.setBounds(180, 140, 100, 25);
        makePlainFont(responseTimeLabel);
        makePlainFont(responseTimeData);
        page.add(responseTimeLabel);
        page.add(responseTimeData);

        JLabel htmlValidationLabel = new JLabel();
        htmlValidationLabel.setText("Валидация HTML:");
        htmlValidationLabel.setBounds(20, 160, 250, 25);
        JLabel htmlValidationData = new JLabel("");
        htmlValidationData.setBounds(145, 160, 30, 25);
        makePlainFont(htmlValidationLabel);
        makePlainFont(htmlValidationData);
        page.add(htmlValidationLabel);
        page.add(htmlValidationData);

        JLabel cssValidationLabel = new JLabel();
        cssValidationLabel.setText("Валидация CSS:");
        cssValidationLabel.setBounds(20, 180, 250, 25);
        JLabel cssValidationData = new JLabel("");
        cssValidationData.setBounds(140, 180, 30, 25);
        makePlainFont(cssValidationLabel);
        makePlainFont(cssValidationData);
        page.add(cssValidationLabel);
        page.add(cssValidationData);

        JProgressBar bar = new JProgressBar();
        bar.setStringPainted(true);
        bar.setBounds(20, 480, 550, 20);
        bar.setMinimum(0);
        bar.setMaximum(100);
        page.add(bar);

        JButton checkPage = new JButton("Проверить");
        checkPage.setBounds(440, 20, 130, 20);
        page.add(checkPage);

        checkPage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (pageAddress.getText().isEmpty()) {

                    JOptionPane.showMessageDialog(frame, "Введите адрес сайта", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
                else {

                    JOptionPane.showMessageDialog(frame, "Возможно, в течение проверки вам придется ввести капчу несколько раз", "Внимание!", JOptionPane.INFORMATION_MESSAGE);

                    checker = new ParametersChecker();

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            checkPage.setEnabled(false);

                            int pageGoogleIndex = checker.checkPageInGoogleIndex(pageAddress.getText());
                            if (pageGoogleIndex == -1)
                                googleIndexData.setText("неизвестно");
                            if (pageGoogleIndex == 0)
                                googleIndexData.setText("нет");
                            if (pageGoogleIndex == 1)
                                googleIndexData.setText("да");
                            updateProgressBar(bar, 6);

                            int pageYaIndex = checker.checkPageInYaIndex(pageAddress.getText());
                            if (pageYaIndex == -1)
                                yaIndexData.setText("неизвестно");
                            if (pageYaIndex == 0)
                                yaIndexData.setText("нет");
                            if (pageYaIndex == 1)
                                yaIndexData.setText("да");
                            updateProgressBar(bar, 6);

                            PageInfo info = checker.fetchPageResponse(pageAddress.getText());
                            String code = info.getHttpResponse();
                            String time = info.getTime();
                            responseCodeData.setText(code);
                            updateProgressBar(bar, 6);
                            responseTimeData.setText(time);
                            updateProgressBar(bar, 6);

                            boolean htmlValid = checker.checkHtmlValidationStatus(pageAddress.getText());
                            boolean cssValid = checker.checkCssValidationStatus(pageAddress.getText());

                            if (htmlValid)
                                htmlValidationData.setText("да");
                            else
                                htmlValidationData.setText("нет");
                            updateProgressBar(bar, 6);

                            if (cssValid)
                                cssValidationData.setText("да");
                            else
                                cssValidationData.setText("нет");
                            updateProgressBar(bar, 6);

                            checkPage.setEnabled(true);
                        }

                    });

                    thread.start();

                }
            }
        });

        JButton saveReportWholeSite = new JButton("Сохранить отчет в Word");
        saveReportWholeSite.setBounds(20, 515, 180, 20);
        saveReportWholeSite.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                    XWPFDocument wholeSiteDocument = new XWPFDocument();

                    try {
                        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy'_'HHmmss");
                        Date date = new Date(System.currentTimeMillis());

                        JFileChooser wholeSiteChooser = new JFileChooser();
                        wholeSiteChooser.setDialogTitle("Сохранить отчет...");
                        wholeSiteChooser.setSelectedFile(new File(address.getText() + "_report_" + formatter.format(date) + ".docx"));

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

                                wholeSiteChooser.setCurrentDirectory(new File(defaultPath));
                                reader.close();
                            }
                            catch (Exception ex) {

                                ex.printStackTrace();
                            }

                        }


                        int userInput = wholeSiteChooser.showSaveDialog(frame);

                        if (userInput == JFileChooser.CANCEL_OPTION)
                            return;

                        formatter = new SimpleDateFormat("dd.MM.yyyy 'в' HH:mm:ss");
                        date = new Date(System.currentTimeMillis());

                        FileOutputStream wholeSiteStream = new FileOutputStream(wholeSiteChooser.getSelectedFile().getAbsolutePath());

                        XWPFParagraph wholeSiteParagraph = wholeSiteDocument.createParagraph();
                        XWPFRun wholeSiteRun = wholeSiteParagraph.createRun();
                        wholeSiteRun.setFontFamily("Times New Roman");
                        wholeSiteRun.setFontSize(14);
                        wholeSiteRun.setText("Отчет о проверке сайта " + address.getText());
                        wholeSiteRun.addBreak();
                        wholeSiteRun.setText("Дата и время проверки: " + formatter.format(date));
                        wholeSiteRun.addBreak();
                        wholeSiteRun.addBreak();
                        wholeSiteRun.setFontSize(16);
                        wholeSiteRun.setBold(true);
                        wholeSiteRun.setText("Индексация");
                        wholeSiteRun.addBreak();
                        wholeSiteRun.setFontSize(14);
                        wholeSiteRun.setBold(false);
                        wholeSiteRun.setText("ИКС:" + yaXData.getText());
                        wholeSiteRun.addBreak();
                        wholeSiteRun.setText("Страницы в индексе Яндекса: " + pagesYaData.getText());
                        wholeSiteRun.addBreak();
                        wholeSiteRun.setText("Страницы в индексе Google: " + pagesGData.getText());
                        wholeSiteRun.addBreak();
                        wholeSiteRun.setText("Страницы в индексе Bing: " + pagesBData.getText());
                        wholeSiteRun.addBreak();
                        wholeSiteRun.setText("Картинки в индексе Яндекса: " + imagesYaData.getText());
                        wholeSiteRun.addBreak();
                        wholeSiteRun.setText("Наличие robots.txt: " + robotsTxtData.getText());
                        wholeSiteRun.addBreak();
                        wholeSiteRun.setText("Наличие sitemap.xml: " + sitemapXmlData.getText());
                        wholeSiteRun.addBreak();
                        wholeSiteRun.setText("Первая запись в WebArchive: " + webArchiveData.getText());
                        wholeSiteRun.addBreak();
                        wholeSiteRun.addBreak();

                        wholeSiteRun.setFontSize(16);
                        wholeSiteRun.setBold(true);
                        wholeSiteRun.setText("Ссылки");
                        wholeSiteRun.addBreak();

                        wholeSiteRun.setFontSize(14);
                        wholeSiteRun.setBold(false);
                        wholeSiteRun.setText("Входящие/исходящие ссылки Linkpad: " + linkpadData.getText().replaceAll("\\<.*?>", ""));
                        wholeSiteRun.addBreak();
                        wholeSiteRun.setText("Входящие ссылки Bing: " + bingLinksData.getText());
                        wholeSiteRun.addBreak();
                        wholeSiteRun.setText("Упоминания домена в Яндексе: " + yaMentionsData.getText());
                        wholeSiteRun.addBreak();
                        wholeSiteRun.addBreak();

                        wholeSiteRun.setFontSize(16);
                        wholeSiteRun.setBold(true);
                        wholeSiteRun.setText("Технические параметры");
                        wholeSiteRun.addBreak();
                        wholeSiteRun.setFontSize(14);
                        wholeSiteRun.setBold(false);
                        wholeSiteRun.setText("IP-адрес: " + ipAddrData.getText());
                        wholeSiteRun.addBreak();
                        wholeSiteRun.setText("Хостинг: " + hostingData.getText());
                        wholeSiteRun.addBreak();
                        wholeSiteRun.setText("CMS: " + cmsData.getText());
                        wholeSiteRun.addBreak();

                        formatter = new SimpleDateFormat("dd.MM.yyyy");
                        date = new Date(System.currentTimeMillis());
                        String reportData = "Проверка параметров сайта;" +  wholeSiteChooser.getSelectedFile().getAbsolutePath() + ";" + formatter.format(date) + "\n";
                        addToLastReports(reportData);

                        wholeSiteDocument.write(wholeSiteStream);
                        wholeSiteDocument.close();
                        wholeSiteStream.close();
                        JOptionPane.showMessageDialog(frame, "Отчет успешно сохранен!", "Сообщение", JOptionPane.INFORMATION_MESSAGE);


                    } catch (Exception ex) {

                        ex.printStackTrace();
                    }
            }
        });
        wholeSite.add(saveReportWholeSite);

        JButton saveReporPage = new JButton("Сохранить отчет в Word");
        saveReporPage.setBounds(20, 515, 180, 20);
        saveReporPage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                XWPFDocument pageDocument = new XWPFDocument();

                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy'_'HHmmss");
                    Date date = new Date(System.currentTimeMillis());

                    JFileChooser pageChooser = new JFileChooser();
                    pageChooser.setDialogTitle("Сохранить отчет...");
                    pageChooser.setSelectedFile(new File( "page_report_" + formatter.format(date) + ".docx"));
                    int userInput = pageChooser.showSaveDialog(frame);

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
                            pageChooser.setCurrentDirectory(new File(defaultPath));
                        }
                        catch (Exception ex) {

                            ex.printStackTrace();
                        }

                    }


                    if (userInput == JFileChooser.CANCEL_OPTION)
                        return;

                    formatter = new SimpleDateFormat("dd.MM.yyyy 'в' HH:mm:ss");
                    date = new Date(System.currentTimeMillis());

                    FileOutputStream pageStream = new FileOutputStream(pageChooser.getSelectedFile().getAbsolutePath());

                    XWPFParagraph pageParagraph = pageDocument.createParagraph();
                    XWPFRun pageRun = pageParagraph.createRun();
                    pageRun.setFontFamily("Times New Roman");
                    pageRun.setFontSize(14);
                    pageRun.setText("Отчет о проверке страницы " + pageAddress.getText());
                    pageRun.addBreak();
                    pageRun.setText("Дата и время проверки: " + formatter.format(date));
                    pageRun.addBreak();
                    pageRun.addBreak();

                    pageRun.setText("Наличие в индексе Google: " + googleIndexData.getText());
                    pageRun.addBreak();
                    pageRun.setText("Наличие в индексе Яндекса: " + yaIndexData.getText());
                    pageRun.addBreak();
                    pageRun.setText("Код ответа страницы: " + responseCodeData.getText());
                    pageRun.addBreak();
                    pageRun.setText("Валидация HTML: " + htmlValidationData.getText());
                    pageRun.addBreak();
                    pageRun.setText("Валидация CSS: " + cssValidationData.getText());
                    pageRun.addBreak();

                    formatter = new SimpleDateFormat("dd.MM.yyyy");
                    date = new Date(System.currentTimeMillis());
                    String reportData = "Проверка параметров сайта;" +  pageChooser.getSelectedFile().getAbsolutePath() + ";" + formatter.format(date) + "\n";
                    addToLastReports(reportData);

                    pageDocument.write(pageStream);
                    pageDocument.close();
                    pageStream.close();
                    JOptionPane.showMessageDialog(frame, "Отчет успешно сохранен!", "Сообщение", JOptionPane.INFORMATION_MESSAGE);



                } catch (Exception ex) {

                    ex.printStackTrace();
                }
            }
        });
        page.add(saveReporPage);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Проверка сайта", wholeSite);
        tabbedPane.addTab("Проверка отдельной страницы", page);

        frame.getContentPane().add(tabbedPane);


        frame.setVisible(true);

    }

    private static void makePlainFont(JLabel label) {

        label.setFont(label.getFont().deriveFont(Font.PLAIN));
        label.setFont(label.getFont().deriveFont(14.0f));
    }

    private static void makeBoldFont(JLabel label) {

        label.setFont(label.getFont().deriveFont(Font.BOLD));
        label.setFont(label.getFont().deriveFont(20.0f));
    }

    private static void updateProgressBar(JProgressBar bar, double tasks) {


        if (tasks == 6.0) {

            if (completePage == tasks) {

                completePage = 0;
                bar.setValue(0);
            }
            else {

                completePage++;
                double val = completePage / tasks * 100;
                bar.setValue((int)val);
            }

        }

        if (tasks == 14.0) {

            if (completeSite == tasks) {

                completeSite = 0;
                bar.setValue(0);
                return;
            }
            else {

                completeSite++;
                double val = completeSite / tasks * 100;
                bar.setValue((int)val);
            }

        }

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
