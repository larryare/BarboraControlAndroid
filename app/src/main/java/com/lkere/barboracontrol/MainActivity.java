package com.lkere.barboracontrol;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    //TODO notifications
    FirebaseDatabase database;

    DatabaseReference overRideDbRef;
    DatabaseReference baskingTimeDbRef;
    DatabaseReference startMinuteDbRef;
    DatabaseReference startHourDbRef;
    DatabaseReference uvStatusDbRef;
    DatabaseReference irStatusDbRef;
    DatabaseReference temperatureDbRef;
    DatabaseReference humidityDbRef;

    int morningHour;
    int morningMinute;
    int baskTime;

    TextView temperatureTextView;
    TextView humidityTextView;

    public String morningTime() {
        if (String.valueOf(morningHour).length() == 1 && String.valueOf(morningMinute).length() == 1) {
            return "0" + morningHour + ":" + "0" + morningMinute;
        } else if (String.valueOf(morningHour).length() == 2 && String.valueOf(morningMinute).length() == 1) {
            return morningHour + ":" + "0" + morningMinute;
        } else if (String.valueOf(morningHour).length() == 1 && String.valueOf(morningMinute).length() == 2) {
            return "0" + morningHour + ":" + morningMinute;
        } else {
            return morningHour + ":" + morningMinute;
        }
    }

    public String eveningTime() {
        int finalTime = baskTime + morningHour;
        if (finalTime >= 24) {
            finalTime = -24;
        }
        if (String.valueOf(finalTime).length() == 1 && String.valueOf(morningMinute).trim().length() == 1) {
            return "0" + finalTime + ":" + morningMinute + "0";
        } else if (String.valueOf(finalTime).length() == 2 && String.valueOf(morningMinute).trim().length() == 1) {
            return finalTime + ":" + morningMinute + "0";
        } else if (String.valueOf(finalTime).length() == 1 && String.valueOf(morningMinute).trim().length() == 2) {
            return "0" + finalTime + ":" + morningMinute;
        } else {
            return finalTime + ":" + morningMinute;
        }
    }

    //TODO replace with switches
    public void uvOn(View view) {
        overRideDbRef.setValue(true);
        uvStatusDbRef.setValue(true);
    }

    public void uvOff(View view) {
        overRideDbRef.setValue(true);
        uvStatusDbRef.setValue(false);
    }

    public void irOn(View view) {
        overRideDbRef.setValue(true);
        irStatusDbRef.setValue(true);
    }

    public void irOff(View view) {
        overRideDbRef.setValue(true);
        irStatusDbRef.setValue(false);
    }

    public void endOverride(View view) {
        overRideDbRef.setValue(false);
    }

    public void changeStartTime(View view) {
        EditText editHour = findViewById(R.id.editHour);
        EditText editMinute = findViewById(R.id.editMinute);
        EditText basking = findViewById(R.id.baskingTime);
        if (TextUtils.isEmpty(editHour.getText().toString().trim()) ||
                TextUtils.isEmpty(editMinute.getText().toString().trim())) {
            Toast.makeText(this, "Enter something lol", Toast.LENGTH_SHORT).show();
        } else {
            int hour = Integer.parseInt(editHour.getText().toString());
            int minute = Integer.parseInt(editMinute.getText().toString());
            int baskTime = Integer.parseInt(basking.getText().toString());
            overRideDbRef.setValue(false);
            startHourDbRef.setValue(hour);
            startMinuteDbRef.setValue(minute);
            baskingTimeDbRef.setValue(baskTime);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance();
        irStatusDbRef = database.getReference("control/irStatus");
        uvStatusDbRef = database.getReference("control/uvStatus");
        startHourDbRef = database.getReference("control/startHour");
        startMinuteDbRef = database.getReference("control/startMinute");
        baskingTimeDbRef = database.getReference("control/baskingTime");
        overRideDbRef = database.getReference("control/overRide");
        temperatureDbRef = database.getReference("temperature");
        humidityDbRef = database.getReference("humidity");

        temperatureTextView = findViewById(R.id.temperatureTextView);
        humidityTextView = findViewById(R.id.humidityTextView);

        final TextView overRideStatus = findViewById(R.id.overRideStatusTextView);
        final TextView irStatusText = findViewById(R.id.irStatus);
        final TextView uvStatusText = findViewById(R.id.uvStatus);
        final TextView startHourText = findViewById(R.id.startHourText);
        final TextView baskingTimeText = findViewById(R.id.baskingTimeText);
        final Button button = findViewById(R.id.button);

        //TODO only 1 event listener is needed
        humidityDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double humidity = dataSnapshot.getValue(double.class);
                humidityTextView.setText(String.valueOf(humidity) + "%");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        temperatureDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double temperature = dataSnapshot.getValue(double.class);
                temperatureTextView.setText(String.valueOf(temperature) + "\u00B0" + "C");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        overRideDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean status = dataSnapshot.getValue(boolean.class);
                if (status) {
                    overRideStatus.setText(R.string.overRidden);
                    overRideStatus.setTextColor(getColor(R.color.colorAccent));
                    button.setAlpha(1);
                } else {
                    overRideStatus.setText(R.string.operatingNormally);
                    overRideStatus.setTextColor(getColor(R.color.colorPrimary));
                    button.setAlpha(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, R.string.dbReadError, Toast.LENGTH_SHORT).show();
            }
        });
        irStatusDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean status = dataSnapshot.getValue(boolean.class);
                if (status) {
                    irStatusText.setText(R.string.irBulbOn);
                    irStatusText.setTextColor(getColor(R.color.colorPrimary));
                } else {
                    irStatusText.setText(R.string.irBulbOff);
                    irStatusText.setTextColor(getColor(R.color.colorAccent));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, R.string.dbReadError, Toast.LENGTH_SHORT).show();
            }
        });
        uvStatusDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean status = dataSnapshot.getValue(boolean.class);
                if (status) {
                    uvStatusText.setText(R.string.uvLightOn);
                    uvStatusText.setTextColor(getColor(R.color.colorPrimary));
                } else {
                    uvStatusText.setText(R.string.uvLightOff);
                    uvStatusText.setTextColor(getColor(R.color.colorAccent));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, R.string.dbReadError, Toast.LENGTH_SHORT).show();
            }
        });
        startHourDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                morningHour = dataSnapshot.getValue(Integer.class);
                baskingTimeText.setText(eveningTime());
                startHourText.setText(morningTime());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        startMinuteDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                morningMinute = dataSnapshot.getValue(Integer.class);
                baskingTimeText.setText(eveningTime());
                startHourText.setText(morningTime());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        baskingTimeDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                baskTime = dataSnapshot.getValue(Integer.class);
                baskingTimeText.setText(eveningTime());
                startHourText.setText(morningTime());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
