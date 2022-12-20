package com.thegoldensource;

public class RuntimeArguments
{
    public static final String ENABLE_POSTGRES = "enable.postgres";
    public static final String ENABLE_GSO_FIELD_REORDER = "enable.gso.field.reorder";
    public static final String IGNORE_GSO_VALIDATION = "ignore.gso.validation";

    public static boolean isEnablePostGres()
    {
        if ("true".equals(System.getProperty(ENABLE_POSTGRES)))
        {
            return true;
        }
        return false;
    }
    
    public static boolean isEnableGSOFieldReorder()
    {
        if ("false".equals(System.getProperty(ENABLE_GSO_FIELD_REORDER)))
        {
            return false;
        }
        return true;
    }
    
    public static boolean isIgnoreGSOValidation()
    {
        if ("true".equals(System.getProperty(IGNORE_GSO_VALIDATION)))
        {
            return true;
        }
        return false;
    }
}
