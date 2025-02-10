package org.GUI;

import org.GUI.PowerIrradiance.PowerIrradianceGUI;
import javax.swing.*;
import java.util.logging.*;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) {
        try {
            // Configure the logger to write to a file
            LogManager.getLogManager().reset();
            FileHandler fileHandler = new FileHandler("application.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

            System.out.println("Application Starting...");
            SwingUtilities.invokeLater(() -> {
                try {
                    PowerIrradianceGUI gui = new PowerIrradianceGUI();
                    gui.setVisible(true);
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error initializing GUI", e);
                }
            });
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error", e);
        }
    }
}
