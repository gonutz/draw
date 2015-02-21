package draw;

import java.util.prefs.Preferences;

public class JavaPreferences implements Settings {

	private Preferences prefs;

	public JavaPreferences() {
		this.prefs = Preferences.userRoot().node(this.getClass().getName());
	}

	@Override
	public int getInt(String id, int defaultIfNotFound) {
		return prefs.getInt(id, defaultIfNotFound);
	}

	@Override
	public void setInt(String id, int value) {
		prefs.putInt(id, value);
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub

	}

}
