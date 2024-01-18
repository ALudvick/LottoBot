package me.ludvick.brisk.walker.bots.telegram.engine;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ChartCreator {
    private final Map<Integer, Integer> data;
    private final String fileName;
    private final String chartTitle;
    private final int fileWidth;
    private final int fileHeight;

    public ChartCreator(Map<Integer, Integer> data, String fileName, String chartTitle, int fileWidth, int fileHeight) {
        this.data = data;
        this.fileName = fileName;
        this.chartTitle = chartTitle;
        this.fileWidth = fileWidth;
        this.fileHeight = fileHeight;
    }

    private JFreeChart createBarChart() {
        return ChartFactory.createBarChart(
                chartTitle,
                "",
                "%%",
                createDataset(),
                PlotOrientation.VERTICAL,
                false,
                false,
                false
        );
    }

    private CategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        int total = data.values().stream().mapToInt(Integer::intValue).sum();

        for (Map.Entry<Integer, Integer> entry : data.entrySet()) {
            int key = entry.getKey();
            int value = entry.getValue();
            double percentage = ((double) value / total) * 100;
            dataset.addValue(percentage, "Percentage", String.valueOf(key));
        }

        return dataset;
    }

    public void saveChartAsJPEG() {
        try {
            ChartUtilities.saveChartAsJPEG(new File(fileName), createBarChart(), fileWidth, fileHeight);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
