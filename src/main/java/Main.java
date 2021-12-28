import org.apache.commons.io.FileUtils;
import org.apache.poi.sl.usermodel.VerticalAlignment;
import org.apache.poi.xwpf.usermodel.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import static java.lang.Thread.sleep;

public class Main {

    static int i = 0;

    public static void main(String[] args) {


        JFrame frame = new JFrame("SEO Booster - Главное меню");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(592,690);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        frame.setResizable(false);

        JLabel welcome = new JLabel("Добро пожаловать в SEO Booster!");
        welcome.setFont(welcome.getFont().deriveFont(Font.BOLD));
        welcome.setFont(welcome.getFont().deriveFont(20.0f));
        welcome.setBounds(10, 20, 600, 20);
        frame.add(welcome);

        JPanel modules = new JPanel();
        LineBorder line = new LineBorder(Color.GRAY, 2);
        TitledBorder titledBorder = new TitledBorder(line, "Модули");
        modules.setBorder(titledBorder);
        modules.setBounds(10, 60, 560, 400);
        modules.setLayout(null);
        frame.add(modules);

        JLabel rdsLabel = new JLabel("Проверка параметров сайта");
        ImageIcon rdsIcon = new ImageIcon("rds.png");
        rdsLabel.setIcon(rdsIcon);
        rdsLabel.setHorizontalTextPosition(JLabel.CENTER);
        rdsLabel.setVerticalTextPosition(JLabel.BOTTOM);
        rdsLabel.setBounds(10, 50, 170, 150);
        generateMouseListener(rdsLabel);
        rdsLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                ParametersWindow window = new ParametersWindow();
            }
        });
        modules.add(rdsLabel);

        JLabel textUnique = new JLabel("Проверка качества текста");
        ImageIcon uniqueIcon = new ImageIcon("text.png");
        textUnique.setIcon(uniqueIcon);
        textUnique.setHorizontalTextPosition(JLabel.CENTER);
        textUnique.setVerticalTextPosition(JLabel.BOTTOM);
        textUnique.setBounds(200, 50, 185, 150);
        generateMouseListener(textUnique);
        textUnique.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                AntiplagiatWindow window = new AntiplagiatWindow();
            }
        });
        modules.add(textUnique);

        JLabel keywords = new JLabel("Подбор ключевых слов");
        ImageIcon keywordsIcon = new ImageIcon("keyword.png");
        keywords.setIcon(keywordsIcon);
        keywords.setHorizontalTextPosition(JLabel.CENTER);
        keywords.setVerticalTextPosition(JLabel.BOTTOM);
        keywords.setBounds(400, 50, 150, 150);
        generateMouseListener(keywords);
        keywords.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                KeywordWindow window = new KeywordWindow();
            }
        });
        modules.add(keywords);

        JLabel positions = new JLabel("Проверка позиций");
        ImageIcon positionsIcon = new ImageIcon("positions.png");
        positions.setIcon(positionsIcon);
        positions.setHorizontalTextPosition(JLabel.CENTER);
        positions.setVerticalTextPosition(JLabel.BOTTOM);
        positions.setBounds(30, 220, 120, 150);
        generateMouseListener(positions);
        positions.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                PositionWindow window = new PositionWindow();
            }
        });
        modules.add(positions);

        JLabel validation = new JLabel("Валидация технических файлов");
        ImageIcon validationIcon = new ImageIcon("validation.png");
        validation.setIcon(validationIcon);
        validation.setHorizontalTextPosition(JLabel.CENTER);
        validation.setVerticalTextPosition(JLabel.BOTTOM);
        validation.setBounds(200, 220, 200, 150);
        generateMouseListener(validation);
        validation.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ValidationWindow window = new ValidationWindow();
            }
        });
        modules.add(validation);

        JLabel settings = new JLabel("Настройки");
        ImageIcon settingsIcon = new ImageIcon("settings.png");
        settings.setIcon(settingsIcon);
        settings.setHorizontalTextPosition(JLabel.CENTER);
        settings.setVerticalTextPosition(JLabel.BOTTOM);
        settings.setBounds(420, 220, 120, 150);
        generateMouseListener(settings);
        settings.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SettingsWindow window = new SettingsWindow();
            }
        });
        modules.add(settings);

        JPanel reports = new JPanel();
        LineBorder lineReports = new LineBorder(Color.GRAY, 2);
        TitledBorder titledBorderReports = new TitledBorder(lineReports, "Последние отчеты");
        reports.setBorder(titledBorderReports);
        reports.setBounds(10, 480, 560, 150);
        reports.setLayout(null);

        frame.add(reports);

        loadLastReports(reports);

        frame.setVisible(true);

    }

    private static void generateMouseListener(JLabel label) {

        label.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                label.setBorder(new LineBorder(Color.BLUE, 2));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setBorder(null);
            }
        });
    }

    private static void loadLastReports(JPanel panel) {

        int x = 10, y = 20;

        BufferedReader reader;
        try {

            reader = new BufferedReader(new FileReader(
                    "last_reports.txt"));

            String line = reader.readLine();
            while (line != null) {

                String[] data = line.split(";");

                JLabel module = new JLabel(data[0]);
                module.setBounds(x, y, 200, 20);

                String[] path = data[1].split("\\\\");
                String fileName = path[path.length - 1];
                JLabel name = new JLabel("<html><font color='blue'>" + fileName + "</font></html>");
                name.setToolTipText("Нажмите, чтобы открыть");
                name.setBounds(x + 260, y, 150, 20);

                name.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {

                        try {
                            Desktop.getDesktop().open(new File(data[1]));
                        }
                        catch (Exception ex) {

                            ex.printStackTrace();
                        }
                    }
                });

                JLabel date = new JLabel(data[2]);
                date.setBounds(x + 480, y, 150, 20);

                panel.add(module);
                panel.add(name);
                panel.add(date);

                y = y + 25;

                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
