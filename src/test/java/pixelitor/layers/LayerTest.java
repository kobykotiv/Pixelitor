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

package pixelitor.layers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import pixelitor.Canvas;
import pixelitor.Composition;
import pixelitor.TestHelper;
import pixelitor.history.AddToHistory;
import pixelitor.history.History;
import pixelitor.testutils.WithMask;
import pixelitor.utils.UpdateGUI;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertSame;

/**
 * Tests the functionality common to all Layer subclasses
 */
@RunWith(Parameterized.class)
public class LayerTest {
    private Composition comp;
    private Layer layer1;

    @Parameter
    public Class layerClass;

    @Parameter(value = 1)
    public WithMask withMask;

    @Parameters(name = "{index}: {0}, mask = {1}")
    public static Collection<Object[]> instancesToTest() {
        return Arrays.asList(new Object[][]{
                {ImageLayer.class, WithMask.NO},
                {ImageLayer.class, WithMask.YES},
                {TextLayer.class, WithMask.NO},
                {TextLayer.class, WithMask.YES},
                {AdjustmentLayer.class, WithMask.NO},
                {AdjustmentLayer.class, WithMask.YES},
        });
    }

    @Before
    public void setUp() {
        comp = TestHelper.createEmptyComposition();
        // make sure each test runs with a fresh Layer
        layer1 = TestHelper.classToLayer(layerClass, comp);

        comp.addLayerNoGUI(layer1);

        ImageLayer layer2 = TestHelper.createImageLayer("layer 2", comp);
        comp.addLayerNoGUI(layer2);

        withMask.init(layer1);

        assert comp.getNrLayers() == 2 : "found " + comp.getNrLayers() + " layers";
        History.clear();
    }

    @Test
    public void testSetVisible() {
        LayerButton layerButton = layer1.getLayerButton();
        assertThat(layer1.isVisible()).isTrue();
        assertThat(layerButton.isVisibilityChecked()).isTrue();

        layer1.setVisible(false, AddToHistory.YES);
        assertThat(layer1.isVisible()).isFalse();
        assertThat(layerButton.isVisibilityChecked()).isFalse();

        History.undo();
        assertThat(layer1.isVisible()).isTrue();
        assertThat(layerButton.isVisibilityChecked()).isTrue();

        History.redo();
        assertThat(layer1.isVisible()).isFalse();
        assertThat(layerButton.isVisibilityChecked()).isFalse();
    }

    @Test
    public void testDuplicate() {
        Layer copy = layer1.duplicate();
        assertThat(copy.getName()).isEqualTo("layer 1 copy");
        assertThat(copy.getClass()).isEqualTo(layer1.getClass());

        Layer copy2 = copy.duplicate();
        assertThat(copy2.getName()).isEqualTo("layer 1 copy 2");

        Layer copy3 = copy2.duplicate();
        assertThat(copy3.getName()).isEqualTo("layer 1 copy 3");
    }

    @Test
    public void testOpacityChange() {
        assertThat(layer1.getOpacity()).isEqualTo(1.0f);

        layer1.setOpacity(0.7f, UpdateGUI.YES, AddToHistory.YES, true);
        assertThat(layer1.getOpacity()).isEqualTo(0.7f);

// TODO WTF: fails when all unit tests are run
//        History.undo();
//        assertThat(layer1.getOpacity()).isEqualTo(1.0f);
//
//        History.redo();
//        assertThat(layer1.getOpacity()).isEqualTo(0.7f);
    }

    @Test
    public void testBlendingModeChange() {
        assertSame(BlendingMode.NORMAL, layer1.getBlendingMode());

        layer1.setBlendingMode(BlendingMode.DIFFERENCE, UpdateGUI.YES, AddToHistory.YES, true);
        assertSame(BlendingMode.DIFFERENCE, layer1.getBlendingMode());

        History.undo();
        assertSame(BlendingMode.NORMAL, layer1.getBlendingMode());

        History.redo();
        assertSame(BlendingMode.DIFFERENCE, layer1.getBlendingMode());
    }

    @Test
    public void testNameChange() {
        assertThat(layer1.getName()).isEqualTo("layer 1");

        layer1.setName("newName", AddToHistory.YES);
        assertThat(layer1.getName()).isEqualTo("newName");
        assertThat(layer1.getLayerButton().getLayerName()).isEqualTo("newName");

        History.undo();
        assertThat(layer1.getName()).isEqualTo("layer 1");
        assertThat(layer1.getLayerButton().getLayerName()).isEqualTo("layer 1");

        History.redo();
        assertThat(layer1.getName()).isEqualTo("newName");
        assertThat(layer1.getLayerButton().getLayerName()).isEqualTo("newName");
    }

    @Test
    public void testMergeDownOn() {
        ImageLayer lower = TestHelper.createImageLayer("lower", comp);
        layer1.mergeDownOn(lower);
    }

    @Test
    public void testMakeActive() {
        assertThat(layer1.isActive()).isFalse();
        layer1.makeActive(AddToHistory.YES);
        assertThat(layer1.isActive()).isTrue();

        History.undo();
        assertThat(layer1.isActive()).isFalse();
        History.redo();
        assertThat(layer1.isActive()).isTrue();
    }

    @Test
    public void testResize() {
        Canvas canvas = layer1.getComp().getCanvas();
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        layer1.resize(canvasWidth, canvasHeight, true);

        layer1.resize(30, 25, true);
        layer1.resize(25, 30, false);

        layer1.resize(canvasWidth, canvasHeight, true);
    }

    @Test
    public void testCrop() {
        layer1.crop(new Rectangle(3, 3, 5, 5));
    }

    @Test
    public void testDragFinished() {
        assertThat(comp.getLayerIndex(layer1)).isEqualTo(0);
        layer1.dragFinished(1);
        assertThat(comp.getLayerIndex(layer1)).isEqualTo(1);
    }

    @Test
    public void testAddMask() {
        if (withMask == WithMask.NO) {
            assertThat(layer1.hasMask()).isFalse();
            layer1.addMask(LayerMaskAddType.REVEAL_ALL);
            assertThat(layer1.hasMask()).isTrue();

            History.undo();
            assertThat(layer1.hasMask()).isFalse();

            History.redo();
            assertThat(layer1.hasMask()).isTrue();
        }
    }

    @Test
    public void testDeleteMask() {
        if (withMask == WithMask.YES) {
            assertThat(layer1.hasMask()).isTrue();
            layer1.deleteMask(AddToHistory.YES, false);
            assertThat(layer1.hasMask()).isFalse();

            History.undo();
            assertThat(layer1.hasMask()).isTrue();

            History.redo();
            assertThat(layer1.hasMask()).isFalse();
        }
    }

    @Test
    public void testSetMaskEnabled() {
        if (withMask == WithMask.YES) {
            assertThat(layer1.hasMask()).isTrue();
            assertThat(layer1.isMaskEnabled()).isTrue();

            layer1.setMaskEnabled(false, AddToHistory.YES);
            assertThat(layer1.isMaskEnabled()).isFalse();

            History.undo();
            assertThat(layer1.isMaskEnabled()).isTrue();

            History.redo();
            assertThat(layer1.isMaskEnabled()).isFalse();
        }
    }

    @Test
    public void testMaskLinking() {
        if (withMask == WithMask.YES) {
            assertThat(layer1.hasMask()).isTrue();
            LayerMask mask = layer1.getMask();
            assertThat(mask.isLinked()).isTrue();

            mask.setLinked(false, AddToHistory.YES);
            assertThat(mask.isLinked()).isFalse();

            History.undo();
            assertThat(mask.isLinked()).isTrue();

            History.redo();
            assertThat(mask.isLinked()).isFalse();
        }
    }
}
