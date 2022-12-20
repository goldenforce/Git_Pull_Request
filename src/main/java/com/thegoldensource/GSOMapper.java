package com.thegoldensource;

/**
 * This is main entry point single-ton class of core jar which hold application context object
 * @author SVishwasrao
 */

public class GSOMapper
{/*
    private static final GSOMapper instance = new GSOMapper();
    private static final String DIR_WORKSPACE = "workspace";
    private final static String DIR_TMP = "tmp";
    private final static String DIR_LIB_TRNS = "translator";
    private final static String DIR_LIB_BASE = "baseComponent";
    private final static String DIR_LIB_LINUX = "linux_all";
    private final static String DIR_LIB = "lib";
    private final static String DIR_ENGINE_LOG = "engineLogs";
    private final static String DIR_PROP = "properties";
    private final static String DIR_TMP_INSTALL = "tmpInstall";
    private static final String DIR_SPLITERS = "Splitters";
    private final static String DIR_EXTERNALS = "externals";
    private static final String DIR_OUTPUT = "Output";

    private static final JdbcTemplatesFactory JDBC_TEMPLATES_FACTORY = new JdbcTemplatesFactory();

    public static final String FILE_CONN_DETAIL = "connectionDetail.xml";
    private static ConnectionDetail connectionDetail;
    private static CopyPasteOutputElementService copyPasteOutputElementService = new CopyPasteOutputElementServiceImpl();
    private LoadGSOContentService loadGSOContentService = new LoadGSOContentService();
    private static boolean showTechnicalDetail = true;
    private static final Logger LOGGER = LogManager.getLogger(GSOMapper.class);
    private static GSOContentDataProvider dbGSOContentDataProvider, fileGSOContentDataProvider;
    //private static GSOMetadata gsoMetadata;
    private static File fileMetadata;
    private static Properties preferences;

    public static final String PREF_GSO_META = "gso.metadata";

    private GSOMapper()
    {

    }

    public static GSOMapper getInstance()
    {
        return instance;
    }

    public synchronized static File getWorkspaceDirectory() throws Exception
    {
        File dirRoot = new File(System.getProperty("user.dir"));
        if (dirRoot.exists())
        {
            return getDirectory(new File(dirRoot, DIR_WORKSPACE));
        }
        return null;
    }

    public synchronized static File getRootDirectory() throws Exception
    {
        File dirRoot = new File(System.getProperty("user.dir"));
        if (dirRoot.exists())
        {
            return dirRoot;
        }
        return null;
    }

    public synchronized static File getTempDirectory() throws Exception
    {
        return getDirectory(new File(getWorkspaceDirectory(), DIR_TMP));
    }

    public synchronized static File getEngineLogDirectory() throws Exception
    {
        return getDirectory(new File(getWorkspaceDirectory(), DIR_ENGINE_LOG));
    }

    public synchronized static File getLibDirectory() throws Exception
    {
        File dirRoot = new File(System.getProperty("user.dir"));
        if (dirRoot.exists())
        {
            return getDirectory(new File(dirRoot, DIR_LIB));
        }
        return null;
    }

    public synchronized static File getPropertiesDirectory() throws Exception
    {
        File dirRoot = new File(System.getProperty("user.dir"));
        if (dirRoot.exists())
        {
            return getDirectory(new File(dirRoot, DIR_PROP));
        }
        return null;
    }

    public synchronized static File getTranslatorLibDirectory() throws Exception
    {
        return getDirectory(new File(getLibDirectory(), DIR_LIB_TRNS));
    }

    public synchronized static File getBaseComponentsLibDirectory() throws Exception
    {
        return getDirectory(new File(getLibDirectory(), DIR_LIB_BASE));
    }
    
    public synchronized static File getLinuxAllLibDirectory() throws Exception
    {
        return getDirectory(new File(getLibDirectory(), DIR_LIB_LINUX));
    }

    public File getTempInstallDirectory() throws Exception
    {
        return getDirectory(new File(getWorkspaceDirectory(), DIR_TMP_INSTALL));
    }

    public synchronized static File getDirectory(File directory) throws Exception
    {
        if (!directory.exists())
        {
            if (!directory.mkdir())
            {
                throw new Exception("Unable to create directory " + directory.getAbsolutePath());
            }
        }
        return directory;
    }

    public synchronized static File getSplittersDir() throws Exception
    {
        return getDirectory(new File(getWorkspaceDirectory(), DIR_SPLITERS));
    }

    public synchronized static File getOutputDir() throws Exception
    {
        return getDirectory(new File(getWorkspaceDirectory(), DIR_OUTPUT));
    }

    public static JdbcTemplatesFactory getJdbcTemplatesFactory()
    {
        return JDBC_TEMPLATES_FACTORY;
    }

    public static SessionFactory getSessionFactory() throws Exception
    {
        if (connectionDetail != null)
        {
            return SessionFactoryProviderImpl.getInstance().getSessionFactory(connectionDetail);
        }
        return null;
    }

    public static ConnectionDetail getConnectionDetail()
    {
        return connectionDetail;
    }

    public static void setConnectionDetail(ConnectionDetail connectionDetail)
    {
        GSOMapper.connectionDetail = connectionDetail;
    }

    public synchronized static void initialize() throws Exception
    {
        getSessionFactory();
    }

    public static void clearCache()
    {
        if (GSOMapper.getConnectionDetail() != null)
        {
            getGSOContentDataProvider().setConnectionDetail(connectionDetail);
            getGSOContentDataProvider().refresh();
            GSOReferenceDataProviderImpl.getInstance().refresh();
            MatchKeyProviderImpl.getInstance().refresh();
        }
    }

    public static CopyPasteOutputElementService getCopyPasteOutputElementService()
    {
        return copyPasteOutputElementService;
    }

    public void loadGSOContents(Set<String> names)
    {
        if (!loadGSOContentService.isRunning())
        {
            loadGSOContentService.setGsoNames(names);
            if (connectionDetail != null)
            {
                Thread threadLoadGSOContent = new Thread(loadGSOContentService);
                threadLoadGSOContent.start();
            }
        }
    }

    private class LoadGSOContentService implements Runnable
    {
        private boolean running = false;
        private Set<String> gsoNames = new HashSet<String>();

        @Override
        public void run()
        {
            running = true;

            // Checking for GSO Metadata File set in the preferences.
            String filePath = null;
            try
            {
                filePath = GSOMapper.getPreferences(GSOMapper.PREF_GSO_META);
            }
            catch (Exception e1)
            {
                LOGGER.error("Error while loading preferences", e1);
            }

            if (filePath != null && !(filePath.trim().isEmpty()))
            {
                // Loading GSO Content from Metadata file
                if (GSOMapper.getFileMetadata() == null)
                {
                    try
                    {
                        LOGGER.info("Metadata loading from file ...");
                        GSOMapper.setOfflineMetadata(new File(filePath));
                        LOGGER.info("Metadata loaded successfully.");
                    }
                    catch (Exception e)
                    {
                        LOGGER.error("Error while loading GSO Content from Metadata file", e);
                    }
                }
            }
            else
            {
                for (String gsoName : gsoNames)
                {
                    try
                    {
                        LOGGER.info("Loading .." + gsoName);
                        getGSOContentDataProvider().getGSOContent(gsoName);
                        getGSOContentDataProvider().getAllGSOFieldParticipants(gsoName);
                        LOGGER.info("Metadata loaded successfully.");
                    }
                    catch (Exception e)
                    {
                        LOGGER.error("Error while loading GSO Content from schema " + gsoName, e);
                    }
                }
            }
            running = false;

        }

        public boolean isRunning()
        {
            return running;
        }

        public void setGsoNames(Set<String> gsoNames)
        {
            this.gsoNames = gsoNames;
        }

    }

    public boolean isLoadGSOContentServiceRunning()
    {
        return loadGSOContentService.isRunning();
    }

    public static boolean isShowTechnicalDetail()
    {
        return showTechnicalDetail;
    }

    public static void setShowTechnicalDetail(boolean showTechnicalDetail)
    {
        GSOMapper.showTechnicalDetail = showTechnicalDetail;
    }

    public synchronized static File getExternalDirectory() throws Exception
    {
        File dirRoot = new File(System.getProperty("user.dir"));
        if (dirRoot.exists())
        {
            return getDirectory(new File(dirRoot, DIR_EXTERNALS));
        }
        return null;
    }

    public static GSOContentDataProvider getGSOContentDataProvider()
    {
        if (gsoMetadata != null)
        {
            if (fileGSOContentDataProvider == null)
            {
                fileGSOContentDataProvider = new FileGSOContentDataProvider(connectionDetail, gsoMetadata);
            }
            return fileGSOContentDataProvider;
        }
        else
        {
            if (dbGSOContentDataProvider == null && connectionDetail != null)
            {
                dbGSOContentDataProvider = new DBGSOContentDataProvider(connectionDetail);
            }
            return dbGSOContentDataProvider;
        }
    }

    public static void setOfflineMetadata(File file) throws JAXBException, Exception
    {
        if (file != null)
        {
            fileGSOContentDataProvider = null; // Reset the instance since its holding the metadata
            fileMetadata = file;
            GSOMetadataParser parser = new GSOMetadataParser();
            parser.setResourceFile(fileMetadata);
            parser.unmarshal();
            gsoMetadata = parser.getParseObject();
            gsoMetadata.assignParent();
        }
        else
        {
            fileMetadata = null;
            gsoMetadata = null;
        }

    }

    public static GSOMetadata getGSOMetadata()
    {
        return gsoMetadata;
    }

    public static File getFileMetadata()
    {
        return fileMetadata;
    }

    public static void setPreferences(String key, String value) throws Exception
    {
        loadPreferences();
        preferences.put(key, value);
        savePreferences();

    }

    public static String getPreferences(String key) throws Exception
    {
        loadPreferences();
        return (String) preferences.get(key);

    }

    private static void loadPreferences() throws Exception
    {
        if (preferences == null)
        {
            preferences = new Properties();
            File filePreferences = new File(GSOMapper.getWorkspaceDirectory(), "preferences.properties");
            if (filePreferences.exists())
            {
                FileInputStream inputStream = null;
                try
                {
                    inputStream = new FileInputStream(filePreferences);
                    preferences = new Properties();
                    preferences.load(inputStream);
                }
                catch (Exception e)
                {
                    throw e;
                }
                finally
                {
                    inputStream.close();
                }

            }
        }
    }

    private static void savePreferences() throws Exception, IOException
    {
        File filePreferences = new File(GSOMapper.getWorkspaceDirectory(), "preferences.properties");
        FileOutputStream outputStrem = null;
        try
        {
            outputStrem = new FileOutputStream(filePreferences.getAbsolutePath());
            preferences.store(outputStrem, "GSO Mapper Preferences Properties");
        }
        catch (Exception e)
        {
            throw e;
        }
        finally
        {
            outputStrem.close();
        }
    }

   public static HibernateTemplate getHibernateTemplate() throws Exception
    {
        return new HibernateTemplate(getSessionFactory());
    }
   public static SessionFactory getSessionFactory() throws Exception
   {
       if (connectionDetail != null)
       {
           return SessionFactoryProviderImpl.getInstance().getSessionFactory(connectionDetail);
       }
       return null;
   }

*/}
