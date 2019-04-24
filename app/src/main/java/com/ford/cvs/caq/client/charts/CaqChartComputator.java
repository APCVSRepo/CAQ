package com.ford.cvs.caq.client.charts;

import lecho.lib.hellocharts.computator.ChartComputator;

public class CaqChartComputator extends ChartComputator {

    CaqChartComputator(){
        super();
    }

    @Override
    public void constrainViewport(float left, float top, float right, float bottom) {

        if (right - left < minViewportWidth) {
            // Minimum width - constrain horizontal zoom!
            right = left + minViewportWidth;
            if (left < maxViewport.left) {
                left = maxViewport.left;
                right = left + minViewportWidth;
            } else if (right > maxViewport.right) {
                right = maxViewport.right;
                left = right - minViewportWidth;
            }
        }

        if (top - bottom < minViewportHeight) {
            // Minimum height - constrain vertical zoom!
            bottom = top - minViewportHeight;
            if (top > maxViewport.top) {
                top = maxViewport.top;
                bottom = top - minViewportHeight;
            } else if (bottom < maxViewport.bottom) {
                bottom = maxViewport.bottom;
                top = bottom + minViewportHeight;
            }
        }

        currentViewport.left = Math.max(maxViewport.left, left);
        currentViewport.top = Math.max(maxViewport.top, top);
        currentViewport.right = currentViewport.left + 5;
        currentViewport.bottom = Math.max(maxViewport.bottom, bottom);

        viewportChangeListener.onViewportChanged(currentViewport);
    }
}
