package org.ies.fenix.client.gui.model.script;

import java.io.File;

public class ImageFileModel extends BaseBlockModel {

    private File image;

    public ImageFileModel(String type) {
        super(type);
    }

    public File getImage() {
        return image;
    }

    public void setImage(File image) {
        this.image = image;
    }
}

