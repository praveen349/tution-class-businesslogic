package com.tution.server;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.naming.ConfigurationException;

import com.tution.common.Utils;




public abstract class ServerConfig {

public static <T extends ServerConfig> T fromDefault(Class<T> type,
		String config) throws FileNotFoundException, IOException, ConfigurationException {
	return from(type, config);
}

public static <T extends ServerConfig> T load(String[] args, Class<T> type,
		String config) throws FileNotFoundException, IOException, ConfigurationException {
	return from(type, config);
}

public static <T extends ServerConfig> T from(Class<T> type, String config)
		throws FileNotFoundException, IOException, ConfigurationException {
	T instance = newEnvironment(type);
	instance.load(config);
	return instance;
}

protected void load(String filename) throws ConfigurationException,
		FileNotFoundException, IOException {
	Properties p = Utils.readProperties(ServerConfig.class, filename);
	fillValues(p);
}

protected abstract void fillValues(Properties p)
		throws ConfigurationException;

protected Properties readProperties(String environmentFile)
		throws FileNotFoundException, IOException {
	Properties properties = new Properties();
	boolean wasLoadedFromClasspath = loadFromClasspath(environmentFile,
			properties);
	FileInputStream fis = null;

	try {
		File envFile = new File(environmentFile);
		fis = new FileInputStream(envFile);
		properties.load(fis);
		return properties;
	} catch (FileNotFoundException e) {
		// log.error("could not find properties file: " +
		// e.getLocalizedMessage());
		if (!wasLoadedFromClasspath) {
			throw e;
		}

		return properties;
	} catch (IOException e) {
		// log.error("error reading properties file: ", e);
		throw e;
	} finally {
		try {
			if (fis != null) {
				fis.close();
			}
		} catch (IOException e) {
			// too late to care at this point
		}
	}
}

private boolean loadFromClasspath(String environmentFile,
		Properties properties) throws IOException {
	String modified = (environmentFile.startsWith("/") ? environmentFile
			: "/" + environmentFile);
	InputStream cpis = getClass().getResourceAsStream(modified);

	try {
		if (cpis != null) {
			properties.load(cpis);
			return true;
		}

		return false;
	} finally {
		try {
			if (cpis != null) {
				cpis.close();
			}
		} catch (IOException e) {
			// too late to care at this point
		}
	}
}

private static <T> T newEnvironment(Class<T> type) {
	T instance = null;

	try {
		instance = type.newInstance();
	} catch (InstantiationException e) {
		e.printStackTrace();
	} catch (IllegalAccessException e) {
		e.printStackTrace();
	}

	return instance;
}
}

