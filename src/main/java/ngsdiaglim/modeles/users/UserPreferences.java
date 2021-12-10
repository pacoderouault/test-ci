package ngsdiaglim.modeles.users;

import java.util.Properties;
import java.util.StringJoiner;

public class UserPreferences extends Properties {

    public UserPreferences() {
        initDefaultPreferences();
    }

    public UserPreferences(String preferenceString) {
        this();
        setUserPreferences(preferenceString);
    }

    private void setUserPreferences(String preferenceString) {
        for (String pref : preferenceString.split(";")) {
            String[] prefsTks = pref.split(":");
            if (prefsTks.length == 2) {
                try {
                    DefaultPreferencesEnum p = DefaultPreferencesEnum.valueOf(prefsTks[0]);
                    this.put(p.name(), prefsTks[1]);
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    private void initDefaultPreferences() {
        for (DefaultPreferencesEnum pref : DefaultPreferencesEnum.values()) {
            this.put(pref.name(), pref.getValue());
        }
    }

    public String getPreferencesAsString() {
        StringJoiner joiner = new StringJoiner(";");
        for (String key : stringPropertyNames()) {
            joiner.add(key+":"+getProperty(key));
        }
        return joiner.toString();
    }

    public String getPreference(DefaultPreferencesEnum pref) {
        return getProperty(pref.name(), null);
    }
}
