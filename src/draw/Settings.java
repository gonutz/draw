package draw;

public interface Settings {

	int getInt(String id, int defaultIfNotFound);

	void setInt(String id, int value);

	boolean getBoolean(String id, boolean defaultIfNotFound);

	void setBoolean(String id, boolean value);

	void save();

}
