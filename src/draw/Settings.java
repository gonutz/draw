package draw;

public interface Settings {

	int getInt(String id, int defaultIfNotFound);

	void setInt(String id, int value);

	void save();

}
