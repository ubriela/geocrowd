package org.datasets.plot;

import java.awt.RenderingHints;
import java.util.List;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.FastScatterPlot;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.datasets.syn.dtype.Point;

/**
 * A demo of the fast scatter plot.
 *
 */
public class ScatterPlot extends ApplicationFrame {

    /** A constant for the number of items in the sample dataset. */
    private static final int COUNT = 5000;

    /** The data. */
    private float[][] data = new float[2][COUNT];

    /**
     * Populates the data array with random values.
     */
    public ScatterPlot(List<Point> points) {
    	super();
   
        for (int i = 0; i < point.size(); i++) {
            final float x = (float) i + 100000;
            this.data[0][i] = x;
            this.data[1][i] = 100000 + (float) Math.random() * COUNT;
        }
    }
    
    /**
     * Creates a new fast scatter plot demo.
     *
     * @param title  the frame title.
     */
    public ScatterPlot(final String title) {

        super(title);
        final NumberAxis domainAxis = new NumberAxis("X");
        domainAxis.setAutoRangeIncludesZero(false);
        final NumberAxis rangeAxis = new NumberAxis("Y");
        rangeAxis.setAutoRangeIncludesZero(false);
        final FastScatterPlot plot = new FastScatterPlot(this.data, domainAxis, rangeAxis);
        final JFreeChart chart = new JFreeChart("Fast Scatter Plot", plot);
//        chart.setLegend(null);

        // force aliasing of the rendered content..
        chart.getRenderingHints().put
            (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final ChartPanel panel = new ChartPanel(chart, true);
        panel.setPreferredSize(new java.awt.Dimension(500, 270));
  //      panel.setHorizontalZoom(true);
    //    panel.setVerticalZoom(true);
        panel.setMinimumDrawHeight(10);
        panel.setMaximumDrawHeight(2000);
        panel.setMinimumDrawWidth(20);
        panel.setMaximumDrawWidth(2000);
        
        setContentPane(panel);

    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(final String[] args) {

        final ScatterPlot demo = new ScatterPlot("Fast Scatter Plot Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
