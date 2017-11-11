// Copyright (C) 2016 Benoît Moreau (ben.12)
//
// This file is part of HABFX-UI (openHAB javaFX User Interface).
//
// HABFX-UI is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// HABFX-UI is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with HABFX-UI.  If not, see <http://www.gnu.org/licenses/>.
package com.ben12.openhab.control;

import com.ben12.openhab.control.skin.ColorPickerSkin;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;

/**
 * @author Benoît Moreau (ben.12)
 */
public class ColorPicker extends Control
{
    private final ObjectProperty<Color> color = new SimpleObjectProperty<>(this, "value");

    public ColorPicker()
    {
        this(Color.RED);

        setPrefSize(200, 200);
    }

    public ColorPicker(final Color c)
    {
        setColor(c);
    }

    @Override
    protected Skin<?> createDefaultSkin()
    {
        return new ColorPickerSkin(this);
    }

    public final ObjectProperty<Color> colorProperty()
    {
        return color;
    }

    public final Color getColor()
    {
        return colorProperty().get();
    }

    public final void setColor(final Color color)
    {
        colorProperty().set(color);
    }

}
