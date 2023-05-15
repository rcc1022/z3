package org.lpw.photon.util;

import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Component("photon.util.zipper")
public class ZipperImpl implements Zipper {
    @Inject
    private Io io;

    @Override
    public void zip(List<File> files, OutputStream outputStream) throws IOException {
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        for (File file : files) {
            zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
            FileInputStream fileInputStream = new FileInputStream(file);
            io.copy(fileInputStream, zipOutputStream);
            fileInputStream.close();
        }
        zipOutputStream.close();
        outputStream.close();
    }

    @Override
    public void zip(Map<String, File> files, OutputStream outputStream) throws IOException {
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        for (String name : files.keySet()) {
            zipOutputStream.putNextEntry(new ZipEntry(name));
            FileInputStream fileInputStream = new FileInputStream(files.get(name));
            io.copy(fileInputStream, zipOutputStream);
            fileInputStream.close();
        }
        zipOutputStream.close();
        outputStream.close();
    }

    @Override
    public void zip(File file, OutputStream outputStream) throws IOException {
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        zip(zipOutputStream, "", file);
        zipOutputStream.close();
        outputStream.close();
    }

    private void zip(ZipOutputStream zipOutputStream, String parent, File file) throws IOException {
        if (file.isFile()) {
            zipOutputStream.putNextEntry(new ZipEntry(parent + file.getName()));
            FileInputStream fileInputStream = new FileInputStream(file);
            io.copy(fileInputStream, zipOutputStream);
            fileInputStream.close();
            zipOutputStream.closeEntry();

            return;
        }

        if (!file.isDirectory())
            return;

        File[] fs = file.listFiles();
        if (fs == null || fs.length == 0)
            return;

        String p = parent + file.getName() + "/";
        zipOutputStream.putNextEntry(new ZipEntry(p));
        for (File f : fs)
            zip(zipOutputStream, p, f);
        zipOutputStream.closeEntry();
    }

    @Override
    public void unzip(File input, Charset charset, File output) throws IOException {
        unzip(new FileInputStream(input), charset, output);
    }

    @Override
    public void unzip(InputStream inputStream, Charset charset, File output) throws IOException {
        unzip(new ZipInputStream(inputStream, charset == null ? Charset.defaultCharset() : charset), output);
    }

    private void unzip(ZipInputStream zipInputStream, File output) throws IOException {
        String path = output.getAbsolutePath() + "/";
        for (ZipEntry zipEntry; (zipEntry = zipInputStream.getNextEntry()) != null; ) {
            if (zipEntry.isDirectory())
                continue;

            File file = new File(path + zipEntry.getName());
            io.mkdirs(file.getParentFile());
            OutputStream outputStream = new FileOutputStream(file);
            io.copy(zipInputStream, outputStream);
            outputStream.close();
            zipInputStream.closeEntry();
        }
        zipInputStream.close();
    }
}
