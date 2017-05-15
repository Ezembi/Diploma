/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package admin.gui;

import java.awt.Frame;
import model.data.VirtualMachine;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.StandardDialog;

/**
 *
 * @author user
 */
public class VMStateChartPanel extends StandardDialog {

    public VMStateChartPanel(Frame owner, String title, boolean modal, VirtualMachine vMachine) {
        super(owner, title, modal);
        JFreeChart barChart = ChartFactory.createBarChart(
                "Статистика",
                "",
                "Загруженность",
                createDataset(vMachine),
                PlotOrientation.VERTICAL,
                true, true, false);

        this.setMinimumSize(new java.awt.Dimension(400, 300));
        ChartPanel cp = new ChartPanel(barChart);
        setContentPane( cp ); 
    }
    
    private static CategoryDataset createDataset(VirtualMachine vMachine) {
        final String id = "Machine # " + vMachine.getId();
        final String ram = "bt";
        final String cpu = "%";
        
        
        final DefaultCategoryDataset dataset
                = new DefaultCategoryDataset();

        dataset.addValue(vMachine.getRam(), id, ram);
        dataset.addValue(vMachine.getCpu(), id, cpu);

        return dataset;
    }
    
}
