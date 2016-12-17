package com.ben12.openhab.ui;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.layout.TilePane;

public class FullWidthTilePane extends TilePane
{
	public FullWidthTilePane(final Node... nodes)
	{
		this(5, 5, nodes);
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
