package com.example.flowlayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.flowlayoutlib.FlowLayout;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private FlowLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        layout = findViewById(R.id.flowlayout);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View firstChild = layout.getChildAt(0);
                if(firstChild != null)
                    layout.removeView(firstChild);
            }
        });
    }
}
