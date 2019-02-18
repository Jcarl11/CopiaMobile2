package com.example.copia;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.util.Size;

import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;

public class FileCompressor
{
    public File compressImage(File originalFile, Context context)
    {
        File compressed = null;
        try {
            compressed = new Compressor(context)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .compressToFile(originalFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return compressed;
    }
}
