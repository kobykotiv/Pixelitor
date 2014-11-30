/*
 * Copyright 2009-2014 Laszlo Balazs-Csiki
 *
 * This file is part of Pixelitor. Pixelitor is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License, version 3 as published by the Free
 * Software Foundation.
 *
 * Pixelitor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Pixelitor.  If not, see <http://www.gnu.org/licenses/>.
 */

package pixelitor;

import pixelitor.layers.Layer;
import pixelitor.layers.LayerChangeListener;
import pixelitor.tools.Symmetry;
import pixelitor.utils.GUIUtils;
import pixelitor.utils.debug.AppNode;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Static methods to support global application logic
 */
public final class AppLogic {
    private static final Collection<LayerChangeListener> layerChangeListeners = new ArrayList<>();

    private AppLogic() {
    }

    public static void activeCompositionDimensionsChanged(Composition comp) {
        Symmetry.setCompositionSize(comp.getCanvasWidth(), comp.getCanvasHeight());
    }

    public static void setStatusMessage(String msg) {
        PixelitorWindow.getInstance().setStatusBarMessage(msg);
    }

    public static void addLayerChangeListener(LayerChangeListener listener) {
        layerChangeListeners.add(listener);
    }

    public static void activeCompLayerCountChanged(Composition comp, int newLayerCount) {
        for (LayerChangeListener listener : layerChangeListeners) {
            listener.activeCompLayerCountChanged(comp, newLayerCount);
        }
    }

    public static void activeLayerChanged(Layer newActiveLayer) {
        for (LayerChangeListener listener : layerChangeListeners) {
            listener.activeLayerChanged(newActiveLayer);
        }
    }

    public static void layerOrderChanged(Composition comp) {
        for (LayerChangeListener listener : layerChangeListeners) {
            listener.layerOrderChanged(comp);
        }
    }

    public static void showDebugAppDialog() {
        final AppNode node = new AppNode();
        String title = "Pixelitor Debug";

        JTree tree = new JTree(node);
        String text = node.toDetailedString();

        GUIUtils.showTextDialog(tree, title, text);
    }

    public static void showFileSavedMessage(File file) {
        AppLogic.setStatusMessage("File " + file.getAbsolutePath() + " saved.");
    }
}

