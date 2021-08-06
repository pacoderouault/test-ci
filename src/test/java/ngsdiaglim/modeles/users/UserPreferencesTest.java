package ngsdiaglim.modeles.users;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserPreferencesTest {

    @Test
    void getPreferences() {
        UserPreferences prefs1 = new UserPreferences();
        assertTrue(prefs1.stringPropertyNames().containsAll(Arrays.stream(DefaultPreferencesEnum.values()).map(Enum::name).collect(Collectors.toList())));

        String prefs2String = DefaultPreferencesEnum.FULL_SCREEN.name() + ":" + false + ";" + DefaultPreferencesEnum.INITIAL_DIR.name() + ":/path/to/dir";
        UserPreferences prefs2 = new UserPreferences(prefs2String);
        assertTrue(prefs2.containsKey(DefaultPreferencesEnum.FULL_SCREEN.name()));
        assertEquals("false", prefs2.getProperty(DefaultPreferencesEnum.FULL_SCREEN.name()));
        assertTrue(prefs2.containsKey(DefaultPreferencesEnum.INITIAL_DIR.name()));
        assertEquals("/path/to/dir", prefs2.getProperty(DefaultPreferencesEnum.INITIAL_DIR.name()));

    }

    @Test
    void getPreferencesAsString() {
        String prefs2String = DefaultPreferencesEnum.FULL_SCREEN.name() + ":" + false + ";" + DefaultPreferencesEnum.INITIAL_DIR.name() + ":/path/to/dir";
        UserPreferences prefs2 = new UserPreferences(prefs2String);
        assertEquals(prefs2String, prefs2.getPreferencesAsString());
    }
}