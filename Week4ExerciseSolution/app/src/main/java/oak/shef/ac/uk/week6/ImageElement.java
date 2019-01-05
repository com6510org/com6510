/*
 * Copyright (c) 2017. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package oak.shef.ac.uk.week6;

import java.io.File;

import oak.shef.ac.uk.week6.database.FotoData;

class ImageElement {
    int image=-1;
    File file=null;
    String path=null;
    FotoData fotodata= null;

    public ImageElement(int image) {
        this.image = image;

    }

    public ImageElement(File fileX) {
        file= fileX;
    }
    public ImageElement(String path) {
        this.path= path;
    }
    public ImageElement(FotoData fotodata) {
        this.fotodata= fotodata;
    }
}
