import org.apache.batik.util.HaltingThread;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class SettingsWindow {

    SettingsWindow() {

        //прокси
        //место для сохранения отчетов по умолчанию

        JFrame frame = new JFrame("SEO Booster - Настройки");
        frame.setSize(400, 400);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);

        JLabel enterSaveLocation = new JLabel("Место хранения отчетов:");
        enterSaveLocation.setBounds(10, 10, 200, 20);
        frame.add(enterSaveLocation);

        JTextField saveLocation = new JTextField();
        saveLocation.setBounds(10, 30, 260, 20);
        saveLocation.setEditable(false);
        frame.add(saveLocation);

        fillDefaultPath(saveLocation);

        JButton saveButton = new JButton("Выбрать...");
        saveButton.setBounds(280, 28, 100, 20);
        frame.add(saveButton);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {

                        JFileChooser chooser = new JFileChooser();
                        chooser.setCurrentDirectory(new java.io.File("."));
                        chooser.setDialogTitle("Выберите директорию для сохранения отчетов...");
                        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                    List<String> settings = new ArrayList<>();

                    if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {

                        String newPath = chooser.getSelectedFile().getAbsolutePath();
                        saveLocation.setText(newPath);

                        if (!(new File("settings.txt").createNewFile())) {

                            BufferedReader reader;
                            reader = new BufferedReader(new FileReader(
                                    "settings.txt"));

                            String line = reader.readLine();
                            while (line != null) {

                                settings.add(line);
                                line = reader.readLine();

                            }

                            boolean found = false;
                            for (int i = 0; i < settings.size(); i++) {

                                if (settings.get(i).contains("default_path=")) {
                                    settings.set(i, "default_path=" + newPath);
                                    found = true;
                                }
                            }

                            if (!found)
                                settings.add("default_path=" + newPath);

                            reader.close();

                            new File("settings.txt").delete();
                            new File("settings.txt").createNewFile();

                            FileWriter fw = new FileWriter("settings.txt");

                            for (String option : settings) {
                                fw.write(option + "\n");
                            }

                            fw.close();

                        } else {

                            FileWriter fw = new FileWriter("settings.txt");
                            fw.write("default_path=" + newPath + "\n");
                            fw.close();

                        }
                    }

                } catch (Exception ex) {

                    ex.printStackTrace();
                }
            }
        });

        JLabel enterProxy = new JLabel("Прокси-сервер:");
        enterProxy.setBounds(10, 50, 100, 20);
        frame.add(enterProxy);

        JTextField proxy = new JTextField();
        proxy.setBounds(10, 70, 260, 20);
        frame.add(proxy);

        fillProxy(proxy);

        JButton saveProxy = new JButton("Сохранить");
        saveProxy.setBounds(280, 68, 100, 20);
        saveProxy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {

                        List<String> settings = new ArrayList<>();

                        if (!(new File("settings.txt").createNewFile())) {

                            BufferedReader reader;
                            reader = new BufferedReader(new FileReader(
                                    "settings.txt"));

                            String line = reader.readLine();
                            while (line != null) {

                                settings.add(line);
                                line = reader.readLine();

                            }


                            boolean found = false;
                            for (int i = 0; i < settings.size(); i++) {

                                if (settings.get(i).contains("proxy_address=")) {

                                    settings.set(i, "proxy_address=" + proxy.getText());
                                    found = true;

                                }
                            }

                            if (!found)
                                settings.add("proxy_address=" + proxy.getText());

                            reader.close();

                            new File("settings.txt").delete();
                            new File("settings.txt").createNewFile();


                            FileWriter fw = new FileWriter("settings.txt");

                            for (String option : settings) {
                                fw.write(option + "\n");
                            }

                            fw.close();
                        } else {

                            FileWriter fw = new FileWriter("settings.txt");
                            fw.write("proxy_address=" + proxy.getText() + "\n");
                            fw.close();

                        }




                } catch (Exception ex) {

                    ex.printStackTrace();
                }

            }

        });

        frame.add(saveProxy);

        JTextArea proxyList = new JTextArea();
        proxyList.setEditable(false);
        JScrollPane proxyPane = new JScrollPane(proxyList);
        proxyPane.setBounds(10, 100, 370, 180);
        frame.add(proxyPane);

        JLabel proxyDesc = new JLabel("<html><p>В этом списке представлены бесплатные прокси, взятые из открытых источников. Список обновляется раз в час. Стабильность работы не гарантируется.</p></html>");
        proxyDesc.setBounds(10, 290, 290, 60);
        frame.add(proxyDesc);

        JButton updateProxyList = new JButton("Обновить");
        updateProxyList.setBounds(280, 290, 100, 20);
        updateProxyList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Thread proxyThread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {

                            proxyList.setText("");

                            FileUtils.copyURLToFile(
                                    new URL("https://spys.me/proxy.txt"),
                                    new File("proxy_list.txt"),
                                    3000,
                                    1000);

                            List<String> addresses = new ArrayList<>();

                            BufferedReader reader;
                            reader = new BufferedReader(new FileReader(
                                    "proxy_list.txt"));

                            String line = reader.readLine();
                            while (line != null) {

                                addresses.add(line);
                                line = reader.readLine();

                            }

                            for (int i = 0; i < 9; i++)
                                addresses.remove(0);

                            addresses.remove(addresses.size() - 1);
                            addresses.remove(addresses.size() - 1);
                            addresses.remove(addresses.size() - 1);

                            for (String addr : addresses)
                                proxyList.append(addr.split(" ")[0] + "\n");
                        } catch (Exception e) {


                            e.printStackTrace();
                        }
                    }
                });

                proxyThread.start();
            }
        });
        frame.add(updateProxyList);

        frame.setVisible(true);
    }

    void fillProxy(JTextField field) {

        try {
            if (new File("settings.txt").exists()) {

                BufferedReader reader;
                reader = new BufferedReader(new FileReader(
                        "settings.txt"));

                String line = reader.readLine();
                while (line != null) {

                    if (line.contains("proxy_address=")) {

                        field.setText(line.split("=")[1]);
                        break;
                    }
                    line = reader.readLine();

                }
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    void fillDefaultPath(JTextField field) {

        try {
            if (new File("settings.txt").exists()) {

                BufferedReader reader;
                reader = new BufferedReader(new FileReader(
                        "settings.txt"));

                String line = reader.readLine();
                while (line != null) {

                    if (line.contains("default_path=")) {

                        field.setText(line.split("=")[1]);
                        break;
                    }
                    line = reader.readLine();

                }
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

    }
}
