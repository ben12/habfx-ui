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

package com.ben12.openhab.ui;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.layout.TilePane;

public class FullWidthTilePane extends TilePane
{
	public FullWidthTilePane(final Node... nodes)
	{
		this(4, 4, nodes);
	}

	public FullWidthTilePane(final double hgap, final double vgap, final Node... nodes)
	{
		super(Orientation.HORIZONTAL, hgap, vgap, nodes);
	}

	@Override
	public void resize(final double width, final double height)
	{
		setPrefTileWidth(USE_COMPUTED_SIZE);

		super.resize(width, height);

		final double left = snapSpace(getInsets().getLeft());
		final double right = snapSpace(getInsets().getRight());
		final double hgap = snapSpace(getHgap());
		final double insideWidth = snapSpace(width) - left - right;

		double tileWidth = getTileWidth();
		tileWidth = tileWidth > insideWidth ? insideWidth : tileWidth;

		final int actualColumns = Math.max(1,
				Math.min((int) ((insideWidth + hgap) / (tileWidth + hgap)), getManagedChildren().size()));

		final double tilePrefWidth = Math.floor(((insideWidth + hgap) / actualColumns) - hgap);

		setPrefTileWidth(tilePrefWidth);
	}
}
