package config;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ReadProperties {

	private static final String FILENAME = "config.configs";
	private static ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(FILENAME);

	public static String getProperty(final String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		}
		catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

}
