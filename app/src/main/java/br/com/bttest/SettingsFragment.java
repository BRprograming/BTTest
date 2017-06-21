package br.com.bttest;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;


public class SettingsFragment extends PreferenceFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);


        EditTextPreference LCDoff = (EditTextPreference) findPreference("LCD_off");
        LCDoff.setSummary(LCDoff.getText() + "x1min");

        EditTextPreference EEPROMdelay = (EditTextPreference) findPreference("EEPROM_delay");
        EEPROMdelay.setSummary(EEPROMdelay.getText() + "x100ms");

        EditTextPreference JazdaTechniczna = (EditTextPreference) findPreference("jazda_techniczna");
        JazdaTechniczna.setSummary(JazdaTechniczna.getText() + "x15min");
        //TODO odswiezanie po zmianie (OnSharedPreferenceChangeListener)

        /*MultiSelectListPreference multiSelectPref = new MultiSelectListPreference(getActivity());
        multiSelectPref.setKey("multi_pref");
        multiSelectPref.setTitle("Choix des sources");
        multiSelectPref.setEntries(R.array.Funkcje_nadzrou_drzwi_20);
        multiSelectPref.setEntryValues(R.array.Ilość_przystanków_01);
        getPreferenceScreen().addPreference(multiSelectPref);

        Preference myPref = (Preference) findPreference("check_box_preference_1");
        myPref.setSummary("opis z javy");
        */

    }

}