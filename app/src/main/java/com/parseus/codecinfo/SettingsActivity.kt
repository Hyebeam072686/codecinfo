package com.parseus.codecinfo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.settings_main.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_main)
        setSupportActionBar(toolbar)
        supportFragmentManager.beginTransaction().replace(R.id.content, SettingsFragment()).commit()
    }

    override fun finish() {
        val intent = Intent()
        intent.putExtra(EXTRA_THEME_CHANGED, themeChanged)
        setResult(Activity.RESULT_CANCELED, intent)
        super.finish()
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            val darkModeSwitch = findPreference<SwitchPreferenceCompat>("darkmode")
            darkModeSwitch.setOnPreferenceChangeListener { _, newValue ->
                if (newValue as Boolean) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }

                SettingsActivity.themeChanged = true
                requireActivity().recreate()

                true
            }
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.preferences_screen)
        }

        @SuppressLint("InflateParams")
        override fun onPreferenceTreeClick(preference: Preference): Boolean {
            return when (preference.key) {
                "feedback" -> {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:${getString(R.string.feedback_email)}")
                        putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
                    }
                    if (intent.resolveActivity(requireActivity().packageManager) != null) {
                        startActivity(Intent.createChooser(intent, getString(R.string.choose_email)))
                    } else {
                        Snackbar.make(requireActivity().findViewById<View>(android.R.id.content),
                                R.string.no_email_apps, Snackbar.LENGTH_LONG).show()
                    }
                    true
                }

                "help" -> {
                    val builder = AlertDialog.Builder(requireActivity())
                    val dialogView = layoutInflater.inflate(R.layout.about_app_dialog, null)
                    builder.setView(dialogView)
                    val alertDialog = builder.create()

                    dialogView.findViewById<View>(R.id.ok_button).setOnClickListener { alertDialog.dismiss() }

                    try {
                        val versionTextView: TextView = dialogView.findViewById(R.id.version_text_view)
                        versionTextView.text = getString(R.string.app_version,
                                requireActivity().packageManager.getPackageInfo(requireActivity().packageName, 0).versionName)
                    } catch (e : Exception) {}

                    alertDialog.show()
                    true
                }

                else -> super.onPreferenceTreeClick(preference)
            }
        }

    }

    companion object {
        var themeChanged = false
        const val EXTRA_THEME_CHANGED = "theme_changed"
    }

}