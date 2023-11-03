package com.socia1ca3t.timepillbackup.util;


import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;
import java.io.IOException;

public class CompressUtil {


    public static void zipWithPassword(File sourceFolder, File targetZipFile, String password) throws IOException {

        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setEncryptFiles(true);
        zipParameters.setEncryptionMethod(EncryptionMethod.AES);
        zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
        ZipFile zipFile = new ZipFile(targetZipFile, password.toCharArray());

        zipFile.addFolder(sourceFolder, zipParameters);

    }
}
