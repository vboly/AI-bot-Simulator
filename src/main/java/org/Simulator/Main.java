package org.Simulator;

import org.Simulator.AI.simulation;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

import org.Simulator.Utilities.FileManager;

public class Main {
    static String API_KEY = "";
    static String BOT_1   = "";
    static String BOT_2   = "";
    static int CONVLENGTH = 2;

    public static String[] Config_list() {
        File cfg_folder = new File("Config");

        if (cfg_folder.exists()) {
            File[] Config_files = cfg_folder.listFiles();
            String[] Configs = new String[Config_files.length];

            for (int i = 0; i < Config_files.length; i++) {
                Configs[i] = Config_files[i].getName();       
            }

            return Configs;
        }

        return null;
    }

    public static String[] FileUI() {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.showOpenDialog(null);

        String Name     = "";
        String Contents = "";

        try {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile == null) {
                return null;
            }
            Name = selectedFile.getName();
            Contents = Files.readString(Path.of(selectedFile.toString()));
        } catch (IOException e) {
            return null;
        }

        return new String[]{Name, Contents};
    };

    public static void main(String[] args) throws IOException {

        JFrame frame = new JFrame("AI Simulation");

        FileManager workspace = new FileManager(System.getProperty("user.dir"));

        if (!workspace.exists("Models")) {
            workspace.folder("Models");
            File T_BOT1 = new File("Models", "BOT_1.json");
            File T_BOT2 = new File("Models","BOT_2.json");
            File T_API  = new File("Models","API_KEY.json");
            T_BOT1.createNewFile();
            T_BOT2.createNewFile();
            T_API.createNewFile();
        }

        if (!workspace.exists("Config")) {
            workspace.folder("Config");
            File T_FIG = new File("Config", "1.cfg");
            T_FIG.createNewFile();
        }

        // Window settings

        {
            frame.setSize(400, 300);
            frame.setVisible(true);
            frame.setResizable(true);
        }

        JTabbedPane Tabs = new JTabbedPane();
        JPanel Simulation = new JPanel();
        JPanel Configuration = new JPanel();

        {
            Tabs.addTab("Simulation", Simulation);
            Tabs.addTab("Configuration", Configuration);
        }

        // Tabs - Simulation logic

        {

            JPanel RUN_PANEL  = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel LOG_PANEL  = new JPanel(new FlowLayout(FlowLayout.LEFT));

            // RUN PANEL

            {

                JButton START_SIM = new JButton("START");
                JLabel STATE = new JLabel("STATE: IDLE");

                START_SIM.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        STATE.setText("STATE: GENERATING");
                        if(Objects.equals(BOT_1, "") || Objects.equals(BOT_2, "") || Objects.equals(API_KEY, "")) {
                            JOptionPane.showMessageDialog(frame, "Please configure the simulation before running!", "Error", JOptionPane.ERROR_MESSAGE);
                            STATE.setText("STATE: IDLE");
                        } else {
                            JOptionPane.showMessageDialog(frame, "The program will freeze to generate your conversatin.");
                            LOG_PANEL.removeAll();

                            simulation GPT = new simulation(API_KEY);
                            Map<String, String> Conversation = GPT.run(new String[] {BOT_1, BOT_2}, CONVLENGTH);

                            final String[] Convo_file = {""};

                            for(int i = 0; i < Conversation.size() / 2; i++) {

                                String BOT1_MSG = Conversation.get("BOT_1 TURN " + i + ":");
                                String BOT2_MSG = Conversation.get("BOT_2 TURN " + i + ":");

                                JLabel msg1 = new JLabel("BOT_1 TURN " + i + ":" + BOT1_MSG);
                                JLabel msg2 = new JLabel("BOT_2 TURN " + i + ":" + BOT2_MSG);

                                Convo_file[0] = Convo_file[0] + "BOT_1 TURN " + i + ":" + BOT1_MSG + "\n";
                                Convo_file[0] = Convo_file[0] + "BOT_2 TURN " + i + ":" + BOT2_MSG + "\n";
                                
                                LOG_PANEL.add(msg1);
                                LOG_PANEL.add(msg2);
                            }



                            Conversation.forEach((key, value) -> {
                                System.out.println(key + "\n");
                                System.out.println(value + "\n");
                            });

                            File CONV_FIG = new File("Models", "Conversation");

                            try {
                                CONV_FIG.createNewFile();
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }

                            try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONV_FIG.getPath()))) {
                                writer.write(Convo_file[0]);
                            } catch (IOException ignored) {
                            }

                            STATE.setText("STATE: IDLE");
                        }
                    }
                });

                RUN_PANEL.add(START_SIM);
                RUN_PANEL.add(STATE);

            }

            LOG_PANEL.setLayout(new GridLayout(99999,0));
            Simulation.add(RUN_PANEL);
            Simulation.add(LOG_PANEL);
            Simulation.setLayout(new BoxLayout(Simulation, BoxLayout.Y_AXIS));
            RUN_PANEL.setAlignmentX(Component.LEFT_ALIGNMENT);
            RUN_PANEL.setMaximumSize(new Dimension(500, 30));
        }

        // Tabs - Configuration logic

        {

            // Setting panels

            JPanel API_PANEL  = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JPanel BOT1_PANEL = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JPanel BOT2_PANEL = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JPanel CONV_PANEL = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JPanel REFRESH_PANEL = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JPanel LOAD_PANEL = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JPanel SAVE_PANEL = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JPanel CREATE_PANEL = new JPanel(new FlowLayout(FlowLayout.CENTER));

            // BUTTONS

            JButton OPEN_KEY = new JButton("None");
            JButton BOT1_MEM = new JButton("None");
            JButton BOT2_MEM = new JButton("None");
            JSlider SLIDER = new JSlider(JSlider.HORIZONTAL, 2, 20, 2);

            // API_PANEL

            {
                API_PANEL.add(new JLabel("OpenAI Key:"));

                OPEN_KEY.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println(API_KEY);
                        String[] KEY = FileUI();
                        if(KEY == null) {
                            JOptionPane.showMessageDialog(frame, "Unable to update!", "Error", JOptionPane.ERROR_MESSAGE);
                        } else {
                            API_KEY = KEY[1];
                            OPEN_KEY.setText(KEY[0]);
                            JOptionPane.showMessageDialog(frame, "OpenAI Key updated!");
                        }
                        API_PANEL.setMinimumSize(API_PANEL.getPreferredSize());
                        BOT1_PANEL.setMinimumSize(BOT1_PANEL.getPreferredSize());
                        BOT2_PANEL.setMinimumSize(BOT2_PANEL.getPreferredSize());
                    }
                });
                API_PANEL.add(OPEN_KEY);
                {
                    Configuration.setLayout(new BoxLayout(Configuration, BoxLayout.Y_AXIS));
                    Configuration.add(API_PANEL);
                }
            }

            // BOT_1

            {
                BOT1_PANEL.add(new JLabel("BOT 1:"));
                BOT1_MEM.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String[] KEY = FileUI();
                        if(KEY == null) {
                            JOptionPane.showMessageDialog(frame, "Unable to update!", "Error", JOptionPane.ERROR_MESSAGE);
                        } else {
                            BOT_1 = KEY[1];
                            BOT1_MEM.setText(KEY[0]);
                            JOptionPane.showMessageDialog(frame, "BOT 1 updated!");
                        }
                        API_PANEL.setMinimumSize(API_PANEL.getPreferredSize());
                        BOT1_PANEL.setMinimumSize(BOT1_PANEL.getPreferredSize());
                        BOT2_PANEL.setMinimumSize(BOT2_PANEL.getPreferredSize());
                    }
                });
                BOT1_PANEL.add(BOT1_MEM);
                Configuration.add(BOT1_PANEL);
            }

            // BOT_2

            {
                BOT2_PANEL.add(new JLabel("BOT 2:"));
                BOT2_MEM.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String[] KEY = FileUI();
                        if(KEY == null) {
                            JOptionPane.showMessageDialog(frame, "Unable to update!", "Error", JOptionPane.ERROR_MESSAGE);
                        } else {
                            BOT_2 = KEY[1];
                            BOT2_MEM.setText(KEY[0]);
                            JOptionPane.showMessageDialog(frame, "BOT 2 updated!");
                        }

                    }
                });
                BOT2_PANEL.add(BOT2_MEM);
                Configuration.add(BOT2_PANEL);
            }

            // CONVLENGTH

            {
                JLabel LENGTH_COUNTER = new JLabel("Length: 2");
                CONV_PANEL.add(LENGTH_COUNTER);
                SLIDER.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        CONVLENGTH = SLIDER.getValue();
                        LENGTH_COUNTER.setText("Length: " + SLIDER.getValue());

                        API_PANEL.setMinimumSize(API_PANEL.getPreferredSize());
                        BOT1_PANEL.setMinimumSize(BOT1_PANEL.getPreferredSize());
                        BOT2_PANEL.setMinimumSize(BOT2_PANEL.getPreferredSize());
                    }
                });
                CONV_PANEL.add(SLIDER);
                Configuration.add(CONV_PANEL);
            }

            // CONFIGS

            String[] Configurations = Config_list();
            JList<String> Configs = new JList<>(Configurations);
            Configuration.add(Configs);

            // CONFIG - REFRESH

            {
                JButton REFRESH_BUT = new JButton("REFRESH");
                REFRESH_BUT.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Configs.setListData(Config_list());
                    }
                });
                REFRESH_PANEL.add(REFRESH_BUT);
                Configuration.add(REFRESH_PANEL);
            }

            // CONFIG - LOAD

            {
                JButton LOAD_BUT = new JButton("LOAD");
                LOAD_BUT.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        String cfg = Configs.getSelectedValue();
                        String[] cfg_list = Config_list();

                        String Values = "";

                        try {
                            Values = workspace.read("/Config/" + Configs.getSelectedValue());
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }

                        String[] split_values = Values.split("\n");

                        OPEN_KEY.setText(split_values[0]);
                        BOT1_MEM.setText(split_values[1]);
                        BOT2_MEM.setText(split_values[2]);

                        try {
                            API_KEY = workspace.read("/Models/" + split_values[0]);
                            BOT_1 =  workspace.read("/Models/" + split_values[1]);
                            BOT_2 =  workspace.read("/Models/" + split_values[2]);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }

                    }
                });
                LOAD_PANEL.add(LOAD_BUT);
                Configuration.add(LOAD_PANEL);
            }

            // CONFIG - SAVE

            {
                JButton SAVE_BUT = new JButton("SAVE");
                SAVE_BUT.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        String cfg = Configs.getSelectedValue();
                        String[] cfg_list = Config_list();

                        String Values = OPEN_KEY.getText() + "\n"
                                + BOT1_MEM.getText() + "\n"
                                + BOT2_MEM.getText() + "\n";


                        File C_FIG = new File("Config", Configs.getSelectedValue());

                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(C_FIG.getPath()))) {
                            writer.write(Values);
                            JOptionPane.showMessageDialog(frame, "Saved success!");
                        } catch (IOException error) {
                            JOptionPane.showMessageDialog(frame, "Error: " + error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }

                        String[] cfg_values = Values.split("\n");

                        OPEN_KEY.setText(cfg_values[0]);
                        BOT1_MEM.setText(cfg_values[1]);
                        BOT2_MEM.setText(cfg_values[2]);

                    }
                });
                SAVE_PANEL.add(SAVE_BUT);
                Configuration.add(SAVE_PANEL);
            }

            // CONFIG CREATE

            {
                JButton CREATE_BUT = new JButton("CREATE");
                CREATE_BUT.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        String cfg = Configs.getSelectedValue();
                        String[] cfg_list = Config_list();

                        String Values = OPEN_KEY.getText() + "\n"
                                + BOT1_MEM.getText() + "\n"
                                + BOT2_MEM.getText() + "\n";

                        File C_FIG = new File("Config", (cfg_list.length + 1) + ".cfg");

                        try {
                            C_FIG.createNewFile();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }

                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(C_FIG.getPath()))) {
                            writer.write(Values);
                            JOptionPane.showMessageDialog(frame, "Saved success!");
                        } catch (IOException error) {
                            JOptionPane.showMessageDialog(frame, "Error: " + error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }

                    }
                });
                CREATE_PANEL.add(CREATE_BUT);
                Configuration.add(CREATE_PANEL);
            }

            API_PANEL.setMaximumSize(new Dimension(999999999,API_PANEL.getPreferredSize().height));
            BOT1_PANEL.setMaximumSize(new Dimension(999999999,BOT1_PANEL.getPreferredSize().height));
            BOT2_PANEL.setMaximumSize(new Dimension(999999999,BOT2_PANEL.getPreferredSize().height));
            CONV_PANEL.setMaximumSize(new Dimension(999999999,CONV_PANEL.getPreferredSize().height));
            LOAD_PANEL.setMaximumSize(new Dimension(999999999,LOAD_PANEL.getPreferredSize().height));
            REFRESH_PANEL.setMaximumSize(new Dimension(999999999,REFRESH_PANEL.getPreferredSize().height));
            SAVE_PANEL.setMaximumSize(new Dimension(999999999,SAVE_PANEL.getPreferredSize().height));
            CREATE_PANEL.setMaximumSize(new Dimension(999999999,SAVE_PANEL.getPreferredSize().height));
            Configuration.setLayout(new BoxLayout(Configuration, BoxLayout.Y_AXIS));
        }

        frame.getContentPane().add(Tabs, BorderLayout.CENTER);
    }
}