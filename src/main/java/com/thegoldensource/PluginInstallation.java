package com.thegoldensource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thegoldensource.utils.FileUtils;

public class PluginInstallation
{
    public static final String BASE_COMPONENT = "baseComponent";
    public static final String MESSAGE_TRANSLATOR = "messageTranslator";
    private String strBCStartwith = "com.thegoldensource.basecomponents.oracle.win32_";
    private String strMTStartwith = "com.thegoldensource.translator.win32_";
    private File filePackage;
    private String product;
    private static final Logger LOGGER = LogManager.getLogger(PluginInstallation.class);

    public PluginInstallation(File filePackage, String product)
    {
        this.filePackage = filePackage;
        this.product = product;
    }

    public void install() throws Exception
    {/*
        try
        {
            if (product == BASE_COMPONENT)
            {
                // Base_Components
                LOGGER.info("\n** Installing Base Components !!!!");
               // String versionBC = unzipAndCopy(filePackage, GSOMapper.getBaseComponentsLibDirectory(), strBCStartwith);

                // Update Base Components version in Properties file
                if (versionBC != null)
                    updateVersion(GSOMapper.getBaseComponentsLibDirectory(), "baseComponent.version", versionBC);
            }

            // Message_Translator
            if (product == MESSAGE_TRANSLATOR)
            {
                LOGGER.info("** Installing Message Translator !!!!");
                String versionMT = unzipAndCopy(filePackage, GSOMapper.getTranslatorLibDirectory(), strMTStartwith);

                // Update Message Translator version in Properties file
                if (versionMT != null)
                    updateVersion(GSOMapper.getTranslatorLibDirectory(), "translator.version", versionMT);
            }

        }
        catch (Exception e)
        {
            LOGGER.error("Error while installing the package", e);
            throw e;
        }
    */}

    private static String unzipAndCopy(File pkgMain, File dirDestination, String strStartwith) throws Exception
    {
        String version = null;
        File dirMain = new File(pkgMain.getAbsolutePath().replaceAll(".zip", ""));

        // UnZip main file
        FileUtils.unZip(pkgMain.getAbsolutePath(), dirMain.getAbsolutePath());
        LOGGER.info("File unzip is successful !!!!");

        // Navigate to plug-ins folder
        File dirPlugin = Arrays.stream(dirMain.listFiles()).filter(f -> f.getName().equalsIgnoreCase("plugins"))
                .findFirst().get();
        if (dirPlugin.exists())
        {
            for (File filePlugin : dirPlugin.listFiles())
            {
                // Check if jar is available
                if (filePlugin.getName().startsWith(strStartwith) && filePlugin.getName().endsWith(".jar"))
                {
                    // Extract Version
                    version = filePlugin.getName().replace(strStartwith, "").replace(".jar", "");
                    File dirJar = new File(filePlugin.getAbsolutePath().replaceAll(".jar", ""));

                    // UnZip Jar File
                    FileUtils.unZip(filePlugin.getAbsolutePath(), dirJar.getAbsolutePath());
                    LOGGER.info(version + " Jar unzip is successful !!!!");

                    // Navigate to lib folder
                    File dirLib = Arrays.stream(dirJar.listFiles()).filter(f -> f.getName().equalsIgnoreCase("lib"))
                            .findFirst().get();

                    if (dirLib.exists())
                    {
                        for (File fileLib : dirLib.listFiles())
                        {
                            // Check if .dll file is available
                            if (fileLib.getName().endsWith(".dll"))
                            {
                                File destFile = new File(dirDestination, fileLib.getName());

                                // copy source file to destination file
                                FileUtils.copyFile(fileLib, destFile);
                                LOGGER.info("Files Copied at loc :- " + destFile.getAbsolutePath());
                            }
                        }
                        return version;
                    }
                }
            }
            LOGGER.error("Incorrect package :- " + pkgMain.getAbsolutePath());
            throw new Exception("Incorrect package :- " + pkgMain.getAbsolutePath());
        }
        return version;
    }

    private static void updateVersion(File dirRoot, String key, String value) throws Exception
    {
        if (dirRoot.exists())
        {
            File versionPropertiesFile = new File(dirRoot, "version.properties");

            // Load properties
            Properties properties = new Properties();
            FileInputStream in = new FileInputStream(versionPropertiesFile);
            properties.load(in);
            in.close();

            // Save properties
            FileOutputStream out = new FileOutputStream(versionPropertiesFile);
            properties.setProperty(key, value);
            properties.store(out, null);
            out.close();

            LOGGER.info("\n ** Version updated in file :- " + dirRoot + "\\version.properties");
        }
    }
}
