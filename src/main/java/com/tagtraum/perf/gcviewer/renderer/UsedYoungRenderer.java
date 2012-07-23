package com.tagtraum.perf.gcviewer.renderer;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Polygon;
import java.util.Iterator;

import com.tagtraum.perf.gcviewer.ModelChart;
import com.tagtraum.perf.gcviewer.ModelChartImpl;
import com.tagtraum.perf.gcviewer.model.GCEvent;
import com.tagtraum.perf.gcviewer.model.GCModel;

/**
 * UsedYoungRenderer draws a line to indicate the current usage of the young generation. The line
 * is drawn inside the young generation (as drawn by the {@link TotalYoungRenderer}).
 * 
 * @author <a href="mailto:gcviewer@gmx.ch">Joerg Wuethrich</a>
 * <p>created on: 22.07.2012</p>
 */
public class UsedYoungRenderer extends PolygonChartRenderer {
    public static final Paint DEFAULT_LINEPAINT = Color.LIGHT_GRAY;

    public UsedYoungRenderer(ModelChartImpl modelChart) {
        super(modelChart);
        setLinePaint(DEFAULT_LINEPAINT);
        setDrawPolygon(false);
        setDrawLine(true);
    }

    @Override
    public Polygon computePolygon(ModelChart modelChart, GCModel model) {
        ScaledPolygon polygon = createMemoryScaledPolygon();
        for (Iterator<GCEvent> i = model.getGCEvents(); i.hasNext();) {
            GCEvent event = i.next();
            GCEvent youngEvent = event.getYoung();

            int tenuredSize = 0;
            if (modelChart.isShowTenured()) {
                GCEvent tenuredEvent = event.getTenured();
                tenuredSize = tenuredEvent.getTotal();
            }
            // e.g. "GC remark" of G1 algorithm does not contain memory information
            if (youngEvent.getTotal() > 0) {
                final double timestamp = event.getTimestamp() - model.getFirstPauseTimeStamp();
                polygon.addPoint(timestamp, tenuredSize + youngEvent.getPreUsed());
                polygon.addPoint(timestamp, tenuredSize + youngEvent.getPostUsed());
            }
        }
        // dummy point to make the polygon complete
        polygon.addPoint(model.getRunningTime(), 0.0d);
        return polygon;
    }

}
