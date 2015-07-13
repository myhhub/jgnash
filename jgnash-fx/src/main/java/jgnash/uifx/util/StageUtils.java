/*
 * jGnash, a personal finance application
 * Copyright (C) 2001-2015 Craig Cavanaugh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jgnash.uifx.util;

import java.awt.geom.Rectangle2D;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Saves and restores Stage sizes
 *
 * @author Craig Cavanaugh
 */
public class StageUtils {

    private static final String DEFAULT_KEY = "bounds";

    private static final Pattern COMMA_DELIMITER_PATTERN = Pattern.compile(",");

    private static final char COMMA_DELIMITER = ',';

    /**
     * Restores and saves the size and location of a stage
     *
     * @param stage The stage to save and restore size and position
     * @param prefNode This should typically be the calling controller
     */
    public static void addBoundsListener(final Stage stage, final Class<?> prefNode) {
        addBoundsListener(stage, prefNode.getName().replace('.', '/'));
    }

    private static void addBoundsListener(final Stage stage, final String prefNode) {
        final String bounds = Preferences.userRoot().node(prefNode).get(DEFAULT_KEY, null);

        if (bounds != null) { // restore to previous size and position
            final Rectangle2D.Double rectangle = decodeRectangle2D(bounds);

            //TODO Avoid use of AWT classes

            boolean resizable = stage.isResizable();

            // Stage will not reposition if resizable is false... JavaFx bug?
            stage.setResizable(false);

            stage.setX(rectangle.getX());
            stage.setY(rectangle.getY());

            if (resizable) { // don't resize if originally false
                stage.setWidth(rectangle.getWidth());
                stage.setHeight(rectangle.getHeight());
            }
            stage.setResizable(resizable); // restore the resize property
        }

        final ChangeListener<Number> boundsListener = new BoundsListener(stage, prefNode);

        stage.widthProperty().addListener(boundsListener);
        stage.heightProperty().addListener(boundsListener);
        stage.xProperty().addListener(boundsListener);
        stage.yProperty().addListener(boundsListener);
    }

    /**
     * Save Window bounds.  Limits rate of saves to the preferences system
     */
    private static class BoundsListener implements ChangeListener<Number> {
        private static final int FORCED_DELAY = 1000;

        private final ThreadPoolExecutor executor;
        private final Preferences p;
        private final Window window;

        public BoundsListener(final Window window, final String prefNode) {
            executor = new ThreadPoolExecutor(0, 1, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1));
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
            p = Preferences.userRoot().node(prefNode);
            this.window = window;
        }

        @Override
        public void changed(final ObservableValue<? extends Number> observable, final Number oldValue, final Number newValue) {
            executor.execute(() -> {
                p.put(DEFAULT_KEY, encodeRectangle2D(window.getX(), window.getY(),
                        window.getWidth(), window.getHeight()));
                try {
                    Thread.sleep(FORCED_DELAY); // forcibly limits amount of saves
                } catch (final InterruptedException e) {
                    Logger.getLogger(BoundsListener.class.getName()).log(Level.SEVERE, e.getMessage(), e);
                }
            });
        }
    }

    public static String encodeRectangle2D(final double x, final double y, final double width, final double height) {
        return String.valueOf(x) + COMMA_DELIMITER + y + COMMA_DELIMITER + width + COMMA_DELIMITER + height;
    }

    public static Rectangle2D.Double decodeRectangle2D(final String bounds) {
        if (bounds == null) {
            return null;
        }

        Rectangle2D.Double rectangle = null;
        String[] array = COMMA_DELIMITER_PATTERN.split(bounds);
        if (array.length == 4) {
            try {
                rectangle = new Rectangle2D.Double();
                rectangle.x = Float.parseFloat(array[0]);
                rectangle.y = Float.parseFloat(array[1]);
                rectangle.width = Float.parseFloat(array[2]);
                rectangle.height = Float.parseFloat(array[3]);
            } catch (final NumberFormatException nfe) {
                Logger.getLogger(StageUtils.class.getName()).log(Level.SEVERE, null, nfe);
                rectangle = null;
            }
        }
        return rectangle;
    }
}
