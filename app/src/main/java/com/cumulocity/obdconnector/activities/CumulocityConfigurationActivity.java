package com.cumulocity.obdconnector.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.cumulocity.obdconnector.ApplicationProperties;
import com.cumulocity.obdconnector.R;
import com.cumulocity.obdconnector.activities.ObdConfigurationActivity;

import static com.cumulocity.obdconnector.ApplicationProperties.CUMULOCITY_PASSWORD_SETTING;
import static com.cumulocity.obdconnector.ApplicationProperties.CUMULOCITY_SSL_SETTING;
import static com.cumulocity.obdconnector.ApplicationProperties.CUMULOCITY_TENANT_SETTING;
import static com.cumulocity.obdconnector.ApplicationProperties.CUMULOCITY_URI_SETTING;
import static com.cumulocity.obdconnector.ApplicationProperties.CUMULOCITY_USER_SETTING;
import static com.cumulocity.obdconnector.ApplicationProperties.GPS_ACTIVE_SETTING;
import static com.cumulocity.obdconnector.ApplicationProperties.GPS_MIN_DISTANCE_SETTING;
import static com.cumulocity.obdconnector.ApplicationProperties.GPS_MIN_INTERVAL_SETTING;

public class CumulocityConfigurationActivity extends AppCompatActivity {

    private static final String TAG = "C8YConfigActivity";

    private ApplicationProperties properties;

    private String cumulocityUri;
    private String cumulocityTenant;
    private String cumulocityUser;
    private String cumulocityPassword;
    private boolean cumulocitySslActive;

    private boolean gpsActive;
    private long gpsMinInterval;
    private float gpsMinDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cumulocity_settings);
        properties = new ApplicationProperties(this);

        this.cumulocityUri = properties.getUri();
        this.cumulocityTenant = properties.getTenant();
        this.cumulocityUser = properties.getUsername();
        this.cumulocityPassword = properties.getPassword();
        this.cumulocitySslActive = properties.useSsl();

        this.gpsActive = properties.useGps();
        this.gpsMinInterval = properties.getMinInterval();
        this.gpsMinDistance = properties.getMinDistance();

        final EditText uriSelector = findViewById(R.id.c8y_uri_setting);
        uriSelector.setText(cumulocityUri);
        final EditText tenantSelector = findViewById(R.id.c8y_tenant_setting);
        tenantSelector.setText(cumulocityTenant);
        final EditText usernameSelector = findViewById(R.id.c8y_username_setting);
        usernameSelector.setText(cumulocityUser);
        final EditText passwordSelector = findViewById(R.id.c8y_password_setting);
        passwordSelector.setText(cumulocityPassword);
        final Switch sslSelector = findViewById(R.id.c8y_ssl_setting);
        sslSelector.setChecked(cumulocitySslActive);


        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1337);
        }

        final Button saveButton = findViewById(R.id.next_screen);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences.Editor editor = properties.edit();
                editor.putString(CUMULOCITY_URI_SETTING, uriSelector.getEditableText().toString());
                editor.putString(CUMULOCITY_TENANT_SETTING, tenantSelector.getEditableText().toString());
                editor.putString(CUMULOCITY_USER_SETTING, usernameSelector.getEditableText().toString());
                editor.putString(CUMULOCITY_PASSWORD_SETTING, passwordSelector.getEditableText().toString());
                editor.putBoolean(CUMULOCITY_SSL_SETTING, sslSelector.isChecked());
                editor.commit();

                Log.d(TAG, "intent for obd screen");
                Intent intent = new Intent(getApplicationContext(), ObdConfigurationActivity.class);
                startActivity(intent);
            }
        });

    }
}