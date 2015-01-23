package org.datasets.plot;

import java.awt.RenderingHints;
import java.util.List;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.FastScatterPlot;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.datasets.syn.DataProvider;
import org.datasets.syn.dtype.DataTypeEnum;
import org.datasets.syn.dtype.Point;

/**
 * A demo of the fast scatter plot.
 *
 */
public class ScatterPlot extends ApplicationFrame {

    /** The data. */
    private float[][] data = null;
    
    /**
     * Creates a new fast scatter plot demo.
     *
     * @param title  the frame title.
     */
    public ScatterPlot(final String title, List<Point> points) {
        super(title);
        
        /**
         * Populates the data array with random values.
         */
        data = new float[2][points.size()];
        for (int i = 0; i < points.size(); i++) {
        	Point p = points.get(i);
            this.data[0][i] = (float) p.getX();
            this.data[1][i] = (float) p.getY();
        }
        
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
        panel.setPreferredSize(new java.awt.Dimension(500, 500));
//        panel.setHorizontalZoom(true);
//        panel.setVerticalZoom(true);
        panel.setMinimumDrawHeight(10);
        panel.setMaximumDrawHeight(2000);
        panel.setMinimumDrawWidth(20);
        panel.setMaximumDrawWidth(2000);
        
        setContentPane(panel);

    }
    
    public static void main(String[] args) {
		DataProvider md = new DataProvider("./res/dataset/task/tasks0.txt", DataTypeEnum.NORMAL_POINT);
        final ScatterPlot demo = new ScatterPlot("Fast Scatter Plot Demo", md.points);
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }
}
