package com.example.electricity;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText editName, editPrev, editCurrent, editMobile;
    Button btnCalculate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editName = findViewById(R.id.editName);
        editPrev = findViewById(R.id.editPrev);
        editCurrent = findViewById(R.id.editCurrent);
        editMobile = findViewById(R.id.editMobile);
        btnCalculate = findViewById(R.id.btnCalculate);

        if (checkSelfPermission(Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1);
        }

        btnCalculate.setOnClickListener(v -> processBill());
    }

    private void processBill() {
        String name = editName.getText().toString();
        String prevStr = editPrev.getText().toString();
        String currStr = editCurrent.getText().toString();
        String mobile = editMobile.getText().toString();

        if (name.isEmpty() || prevStr.isEmpty() || currStr.isEmpty() || mobile.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int prev = Integer.parseInt(prevStr);
        int curr = Integer.parseInt(currStr);

        if (curr < prev) {
            Toast.makeText(this, "Current reading must be greater!", Toast.LENGTH_SHORT).show();
            return;
        }

        int units = curr - prev;

        double total = calculateBill(units);

        String msg = "🔌 TNEB Electricity Bill 🔌\n\n" +
                "Name: " + name + "\n" +
                "Previous: " + prev + "\n" +
                "Current: " + curr + "\n" +
                "Units Used: " + units + "\n\n" +
                "Total Bill: ₹" + total + "\n" +
                "Thank you!";

        sendSMS(mobile, msg);
    }

    private double calculateBill(int u) {
        double amt;

        if (u <= 100) {
            amt = 0;
        } else if (u <= 200) {
            amt = (u - 100) * 2.25;
        } else if (u <= 500) {
            amt = (100 * 2.25) + (u - 200) * 4.50;
        } else if (u <= 600) {
            amt = (100 * 2.25) + (300 * 4.50) + (u - 500) * 6.30;
        } else if (u <= 650) {
            amt = (100 * 2.25) + (300 * 4.50) + (100 * 6.30) + (u - 600) * 8.40;
        } else {
            amt = (100 * 2.25) + (300 * 4.50) + (100 * 6.30)
                    + (50 * 8.40) + (u - 650) * 9.45;
        }

        return amt;
    }

    private void sendSMS(String number, String message) {
        try {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(number, null, message, null, null);
            Toast.makeText(this, "Detailed SMS Sent!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "SMS Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
