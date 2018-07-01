/*
 * Copyright 2018 Laszlo Balazs-Csiki and Contributors
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

package pixelitor.gui;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * A component that maintains a component coordinate system
 * and an image coordinate system, and can convert between the two.
 * The image coordinate system might be zoomed and/or translated relative
 * to the component.
 */
public interface View {
    double componentXToImageSpace(double mouseX);

    double componentYToImageSpace(double mouseY);

    double imageXToComponentSpace(double x);

    double imageYToComponentSpace(double y);

    Rectangle2D componentToImageSpace(Rectangle input);

    Rectangle imageToComponentSpace(Rectangle2D input);

    AffineTransform getImageToComponentTransform();

    AffineTransform getComponentToImageTransform();

    void repaint();
}