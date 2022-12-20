package com.thegoldensoure.mapping.test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thegoldensource.mapping.MappingSpecification;
import com.thegoldensource.mapping.cache.ExpressionCache;
import com.thegoldensource.parser.MsfParser;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestMapping extends TestCase {

	private static final Logger LOGGER = LogManager.getLogger(TestMapping.class);

	
	
	public void testGetMappingSpecification() throws IOException {
		try {
			//File file = new File("C:\\GoldenSource\\Workbench_8.7.2\\workspace1\\TestBB\\Bloomberg\\BBGlobalEquity-dash.mdx");
			File file = new File("db://resource/mapping/Bloomberg/BBGeneric.mdx");
			String datasourcePropertyPath="D:\\inputFiles\\datasource-oracle.properties";
			MsfParser msfParser = new MsfParser(datasourcePropertyPath,file);
			msfParser.initialize();
			MappingSpecification mappingSpecification = msfParser.parse(file.toURI());
			Map map=ExpressionCache.mdx_ExpressionMap.get(file.toURI().getPath());
			
			Assert.assertNotNull(mappingSpecification);
		} catch (Exception e) {
			LOGGER.error("Exception occurred while getting mapping specification ", e);
		}

	}

}
