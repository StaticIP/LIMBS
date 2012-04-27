package LIMBS;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import javax.swing.JComponent;

/**
 * Listener that can be attached to a Component to implement Zoom and Pan
 * functionality.
 * 
 * @author Sorin Postelnicu
 * @since Jul 14, 2009
 */
public class ZoomAndPanManager {
    public static final int DEFAULT_MIN_ZOOM_LEVEL = -20;
    public static final int DEFAULT_MAX_ZOOM_LEVEL = 10;
    public static final double DEFAULT_ZOOM_MULTIPLICATION_FACTOR = 1.2;

    private JComponent targetComponent;

    private int zoomLevel = 0;
    private int minZoomLevel = DEFAULT_MIN_ZOOM_LEVEL;
    private int maxZoomLevel = DEFAULT_MAX_ZOOM_LEVEL;
    private double zoomMultiplicationFactor = DEFAULT_ZOOM_MULTIPLICATION_FACTOR;
    private boolean first = true;

    private AffineTransform coordTransform = new AffineTransform();

    public ZoomAndPanManager(JComponent targetComponent) {
        this.targetComponent = targetComponent;
    }

    public ZoomAndPanManager(JComponent targetComponent, int minZoomLevel,
            int maxZoomLevel, double zoomMultiplicationFactor) {
        this.targetComponent = targetComponent;
        this.minZoomLevel = minZoomLevel;
        this.maxZoomLevel = maxZoomLevel;
        this.zoomMultiplicationFactor = zoomMultiplicationFactor;
    }

    public void pan(int p) {
        int adjust = 0;
        if (first) {
            first = false;
            adjust = 174;
        }
        if (p == 0) { // N
            coordTransform.translate(0, -10 - adjust);
        } else if (p == 1) { // E
            coordTransform.translate(10, -adjust);
        } else if (p == 2) { // S
            coordTransform.translate(0, 10 - adjust);
        } else if (p == 3) { // W
            coordTransform.translate(-10, -adjust);
        } else {
            // to fix the weird initial behaviour for zoom
            coordTransform.translate(0, -adjust);
        }
        targetComponent.repaint();

    }

    public void pan(Point dragStartScreen, Point dragEndScreen) {
        if (first) {
            pan(4);
        }
        try {
            Point2D.Float dragStart = transformPoint(dragStartScreen);
            Point2D.Float dragEnd = transformPoint(dragEndScreen);
            double dx = dragEnd.getX() - dragStart.getX();
            double dy = dragEnd.getY() - dragStart.getY();
            coordTransform.translate(dx, dy);
            targetComponent.repaint();
        } catch (NoninvertibleTransformException ex) {
            ex.printStackTrace();
        }
    }

    public void zoom(int z, Point p) {
        if (first) {
            pan(4);
        }
        try {
            if (z > 0) {
                if (zoomLevel < maxZoomLevel) {
                    zoomLevel++;
                    Point2D p1 = transformPoint(p);
                    coordTransform.scale(1 / zoomMultiplicationFactor,
                            1 / zoomMultiplicationFactor);
                    Point2D p2 = transformPoint(p);
                    coordTransform.translate(p2.getX() - p1.getX(), p2.getY()
                            - p1.getY());
                    targetComponent.repaint();
                }
            } else {
                if (zoomLevel > minZoomLevel) {
                    zoomLevel--;
                    Point2D p1 = transformPoint(p);
                    coordTransform.scale(zoomMultiplicationFactor,
                            zoomMultiplicationFactor);
                    Point2D p2 = transformPoint(p);
                    coordTransform.translate(p2.getX() - p1.getX(), p2.getY()
                            - p1.getY());
                    targetComponent.repaint();
                }
            }
        } catch (NoninvertibleTransformException ex) {
            ex.printStackTrace();
        }
    }

    public Point2D.Float transformPoint(Point p1)
            throws NoninvertibleTransformException {

        AffineTransform inverse = coordTransform.createInverse();

        Point2D.Float p2 = new Point2D.Float();
        inverse.transform(p1, p2);
        return p2;
    }

    public int getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(int zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    public AffineTransform getCoordTransform() {
        return coordTransform;
    }

    public void setCoordTransform(AffineTransform coordTransform) {
        this.coordTransform = coordTransform;
    }

    public void showMatrix(AffineTransform at) {
        double[] matrix = new double[6];
        at.getMatrix(matrix); // { m00 m10 m01 m11 m02 m12 }
        int[] loRow = { 0, 0, 1 };
        for (int i = 0; i < 2; i++) {
            System.out.print("[ ");
            for (int j = i; j < matrix.length; j += 2) {
                System.out.printf("%5.1f ", matrix[j]);
            }
            System.out.print("]\n");
        }
        System.out.print("[ ");
        for (int i = 0; i < loRow.length; i++) {
            System.out.printf("%3d   ", loRow[i]);
        }
        System.out.print("]\n");
        System.out.println("---------------------");
    }
}
