package org.GUI.PowerIrradiance;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.*;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class PowerIrradianceGUI extends JFrame {
    private TimeSeries powerSeries = new TimeSeries("Power Production (mWh)");
    private TimeSeries irradianceSeries = new TimeSeries("Irradiance (W/m²)");

    public PowerIrradianceGUI() {
        setTitle("Power & Irradiance Analyzer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        JButton loadGridButton = new JButton("Load Grid Power File");
        JButton loadIrrButton = new JButton("Load IRR 2025 File");
        JButton avg1H = new JButton("1 Hour");
        JButton avg1D = new JButton("1 Day");
        JButton avg1W = new JButton("1 Week");

        buttonPanel.add(loadGridButton);
        buttonPanel.add(loadIrrButton);
        buttonPanel.add(avg1H);
        buttonPanel.add(avg1D);
        buttonPanel.add(avg1W);
        add(buttonPanel, BorderLayout.NORTH);

        loadGridButton.addActionListener(e -> loadFile(true));
        loadIrrButton.addActionListener(e -> loadFile(false));

        JFreeChart chart = createChart();
        ChartPanel chartPanel = new ChartPanel(chart);
        add(chartPanel, BorderLayout.CENTER);
    }

    private void loadFile(boolean isGrid) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            parseFile(file, isGrid);
        }
    }

    private void parseFile(File file, boolean isGrid) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length >= 2) {
                    Date date = dateFormat.parse(parts[0]);
                    RegularTimePeriod time = new Minute(date);
                    double value = Double.parseDouble(parts[1]);

                    if (isGrid) {
                        powerSeries.addOrUpdate(time, value);
                    } else {
                        irradianceSeries.addOrUpdate(time, value);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error reading file", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JFreeChart createChart() {
        TimeSeriesCollection dataset1 = new TimeSeriesCollection(powerSeries);
        TimeSeriesCollection dataset2 = new TimeSeriesCollection(irradianceSeries);

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Power Production vs Irradiance",
                "Time",
                "Power Production (mWh)",
                dataset1,
                true, true, false);

        XYPlot plot = chart.getXYPlot();

        NumberAxis irradianceAxis = new NumberAxis("Irradiance (W/m²)");
        plot.setRangeAxis(1, irradianceAxis);
        plot.setDataset(1, dataset2);
        plot.mapDatasetToRangeAxis(1, 1);

        return chart;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PowerIrradianceGUI().setVisible(true));
    }
}
