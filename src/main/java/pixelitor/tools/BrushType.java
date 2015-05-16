/*
 * Copyright 2015 Laszlo Balazs-Csiki
 *
 * This file is part of Pixelitor. Pixelitor is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License, version 3 as published by the Free
 * Software Foundation.
 *
 * Pixelitor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Pixelitor. If not, see <http://www.gnu.org/licenses/>.
 */

package pixelitor.tools;

import pixelitor.tools.brushes.AngleSettings;
import pixelitor.tools.brushes.Brush;
import pixelitor.tools.brushes.CalligraphyBrush;
import pixelitor.tools.brushes.IdealBrush;
import pixelitor.tools.brushes.ImageBrushType;
import pixelitor.tools.brushes.ImageDabsBrush;
import pixelitor.tools.brushes.OnePixelBrush;
import pixelitor.tools.brushes.OutlineCircleBrush;
import pixelitor.tools.brushes.OutlineSquareBrush;
import pixelitor.tools.brushes.RadiusRatioSpacing;
import pixelitor.tools.brushes.ShapeBrushSettingsPanel;
import pixelitor.tools.brushes.ShapeDabsBrush;
import pixelitor.tools.brushes.ShapeDabsBrushSettings;
import pixelitor.tools.brushes.WobbleBrush;

import javax.swing.*;
import java.util.IdentityHashMap;
import java.util.Map;

import static pixelitor.tools.brushes.AngleSettings.ANGLE_AWARE_NO_SCATTERING;
import static pixelitor.tools.brushes.AngleSettings.NOT_ANGLE_AWARE;

/**
 * The brush types the user can use
 */
public enum BrushType {
    IDEAL("Hard", false) {
        @Override
        public Brush createBrush(Tool tool) {
            return new IdealBrush();
        }
    }, SOFT("Soft", false) {
        @Override
        public Brush createBrush(Tool tool) {
            return new ImageDabsBrush(ImageBrushType.SOFT, 0.25, NOT_ANGLE_AWARE);
        }
    }, WOBBLE("Wobble", false) {
        @Override
        public Brush createBrush(Tool tool) {
            return new WobbleBrush();
        }
    }, CALLIGRAPHY("Calligraphy", false) {
        @Override
        public Brush createBrush(Tool tool) {
            return new CalligraphyBrush();
        }
    }, REALISTIC("Realistic", false) {
        @Override
        public Brush createBrush(Tool tool) {
            return new ImageDabsBrush(ImageBrushType.REAL, 0.05, NOT_ANGLE_AWARE);
        }
    }, HAIR("Hair", false) {
        @Override
        public Brush createBrush(Tool tool) {
            return new ImageDabsBrush(ImageBrushType.HAIR, 0.02, NOT_ANGLE_AWARE);
        }
    }, SHAPE("Shape", true) {
        // The settings must be shared between the symmetry-brushes of a tool, but
        // they must be different between the different tools
        private Map<Tool, ShapeDabsBrushSettings> settingsByTool = new IdentityHashMap<>();
        private Map<Tool, JPanel> settingPanelsByTool = new IdentityHashMap<>();

        @Override
        public Brush createBrush(Tool tool) {
            ShapeDabsBrushSettings settings = settingsByTool.get(tool);
            if(settings == null) {
                ShapeType shapeType = ShapeBrushSettingsPanel.SHAPE_SELECTED_BY_DEFAULT;
                double spacingRatio = ShapeBrushSettingsPanel.DEFAULT_SPACING_RATIO;
                AngleSettings angleSettings = ANGLE_AWARE_NO_SCATTERING;
                RadiusRatioSpacing spacing = new RadiusRatioSpacing(spacingRatio);

                ShapeDabsBrush shapeDabsBrush = new ShapeDabsBrush(shapeType, spacing, angleSettings);
                settings = (ShapeDabsBrushSettings) shapeDabsBrush.getSettings();
                settingsByTool.put(tool, settings);
                return shapeDabsBrush;
            } else {
                ShapeDabsBrush shapeDabsBrush = new ShapeDabsBrush(settings);
                return  shapeDabsBrush;
            }
        }

        @Override
        public JPanel getSettingsPanel(Tool tool) {
            JPanel settingsPanel = settingPanelsByTool.get(tool);
            if (settingsPanel == null) {
                ShapeDabsBrushSettings settings = settingsByTool.get(tool);
                settingsPanel = new ShapeBrushSettingsPanel(settings);
            }
            return settingsPanel;
        }

        //    }, ARROW("Image-Based Arrow") {
//        @Override
//        public Brush get() {
//            return new ImageDabsBrush(ImageBrushType.ARROW, 2.5, true);
//        }
//    }, GREEK("Image-Based Greek") {
//        @Override
//        public Brush get() {
//            return new ImageDabsBrush(ImageBrushType.GREEK, 2.0, true);
//        }
    }, OUTLINE_CIRCLE("Circles", false) {
        @Override
        public Brush createBrush(Tool tool) {
            return new OutlineCircleBrush();
        }
    }, OUTLINE_SQUARE("Squares", false) {
        @Override
        public Brush createBrush(Tool tool) {
            return new OutlineSquareBrush();
        }
    }, ONE_PIXEL("One Pixel", false) {
        @Override
        public Brush createBrush(Tool tool) {
            return new OnePixelBrush();
        }

        @Override
        public boolean sizeCanBeSet() {
            return false;
        }
    };

    private final String guiName;
    private final boolean hasSettings;

    BrushType(String guiName, boolean hasSettings) {
        this.guiName = guiName;
        this.hasSettings = hasSettings;
    }

    public abstract Brush createBrush(Tool tool);

    @Override
    public String toString() {
        return guiName;
    }

    public boolean sizeCanBeSet() {
        return true; // intended to be overridden if necessary
    }

    public boolean hasSettings() {
        return hasSettings;
    }

    public JPanel getSettingsPanel(Tool tool) {
        assert hasSettings;
        return null; // intended to be overridden if necessary
    }
}
