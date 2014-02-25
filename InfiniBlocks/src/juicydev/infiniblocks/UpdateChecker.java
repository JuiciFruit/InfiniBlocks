package juicydev.infiniblocks;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class UpdateChecker {

	public InfiniBlocks plugin;
	public URL filesFeed;
	public boolean enabled = false;

	public String latestVer = "";
	public String url = "";

	public UpdateChecker(InfiniBlocks plugin, String url, boolean enabled) {
		this.plugin = plugin;
		this.enabled = enabled;

		try {
			this.filesFeed = new URL(url);
		} catch (MalformedURLException e) {
			plugin.log(e);
		}
	}

	public boolean isUpdateNeeded() {
		try {
			InputStream stream = this.filesFeed.openConnection()
					.getInputStream();
			Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(stream);

			Node latest = doc.getElementsByTagName("item").item(0);
			NodeList chidren = latest.getChildNodes();

			latestVer = chidren.item(1).getTextContent().split(" v")[1];
			url = chidren.item(3).getTextContent();

			if (latestVer.contains("-")) {
				if (latestVer.split("-")[1].equalsIgnoreCase("SNAPSHOT")) {
					return false;
				}
			}

			int latestVerInt = Integer.parseInt(latestVer.replace(".", "")
					.replace("-", ""));

			String currentVer = plugin.getVersion();
			int currentVerInt = Integer.parseInt(currentVer.replace(".", "")
					.replace("-", ""));

			if (latestVerInt > currentVerInt) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			plugin.log(e);
		}

		return false;
	}

	public void logUpdateInfo(boolean available) {
		if (available) {
			plugin.log("A new version of the plugin is available for download.");
			plugin.log(latestVer + " of the plugin can be downloaded at: "
					+ url);
		} else {
			plugin.log("The plugin is up to date.");
		}
	}
}
