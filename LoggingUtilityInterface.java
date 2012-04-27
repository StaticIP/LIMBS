package LIMBS;

import java.io.File;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;

public class LoggingUtilityInterface extends AgentInterface {
  
  final LoggingUtility model;
  
  private JPanel utilityPanel;
  final JFileChooser fc;
  
  final private ChartPanel chartPanel;
  final private JFreeChart chart;
  
  LoggingUtilityInterface( LoggingUtility m ){
    super(m);
    model = m;
    fc  = new JFileChooser();
  
    setupUtilityPanel();
    utilityPanel.setVisible(true);
    
    chart =  ChartFactory.createXYLineChart(
            "Log information",        // chart title
            "Iteration",              // x axis label
            "Value",                  // y axis label
            model.getPlotData(),                  // data
            PlotOrientation.VERTICAL,
            true,                     // include legend
            true,                     // tooltips
            false                     // urls
        );
    chart.setBackgroundPaint(Color.white);
    chartPanel = new ChartPanel(chart);
    
    add(utilityPanel, BorderLayout.LINE_START);
    utilityPanel.add(chartPanel, BorderLayout.LINE_START);
  }
  
  private void setupUtilityPanel()
  {
    utilityPanel = new JPanel();
    utilityPanel.setLayout(new BoxLayout(utilityPanel, BoxLayout.Y_AXIS));
    
    // logging file label
    JLabel label = new JLabel("Logging File");
    label.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
    label.setBorder(BorderFactory.createEmptyBorder(10,2,10,0));
    utilityPanel.add(label);
    
    // file chooser
    final JTextField filename = new JTextField(model.getFilename(), 40);
    filename.setEditable(false);
    filename.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
    utilityPanel.add(filename);
    
    JButton browse = new JButton("Browse ...");
    browse.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
    browse.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        int returnVal = fc.showOpenDialog(utilityPanel);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          File file = fc.getSelectedFile();
          model.getSimulation().registerEndChange( new ChangeNotification(){
            public boolean notifyChange () {
              filename.setText( model.getFilename() );
              repaint();
              return true;
            }
          } );
          model.setFilename(file.getAbsolutePath());
          filename.setText( model.getFilename() );
        }
      }
    });
    utilityPanel.add(browse);
   
  }

@Override
public void refresh() {
  repaint();
    
}
  
};