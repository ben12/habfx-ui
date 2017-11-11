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

package com.ben12.openhab.rest;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicReference;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import jfxtras.util.PlatformUtil;

@Provider
@Consumes("image/*")
public class ImageProvider implements MessageBodyReader<Image>
{
    private final double imageMinSize;

    private final double imageMaxSize;

    public ImageProvider(final double imgMinSize, final double imgMaxSize)
    {
        imageMinSize = imgMinSize;
        imageMaxSize = imgMaxSize;
    }

    @Override
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations,
            final MediaType mediaType)
    {
        return Image.class == type;
    }

    @Override
    public Image readFrom(final Class<Image> type, final Type genericType, final Annotation[] annotations,
            final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream)
    {
        final AtomicReference<Image> image = new AtomicReference<>(new Image(entityStream));
        final double size = Math.max(image.get().getWidth(), image.get().getHeight());

        if (size < imageMinSize || size > imageMaxSize)
        {
            PlatformUtil.runAndWait(() -> {
                final ImageView view = new ImageView(image.get());
                view.setPreserveRatio(true);
                view.setFitWidth(size < imageMinSize ? imageMinSize : imageMaxSize);
                view.setFitHeight(size < imageMinSize ? imageMinSize : imageMaxSize);
                final SnapshotParameters params = new SnapshotParameters();
                params.setFill(Color.TRANSPARENT);
                image.set(view.snapshot(params, null));
            });
        }

        return image.get();
    }
}
