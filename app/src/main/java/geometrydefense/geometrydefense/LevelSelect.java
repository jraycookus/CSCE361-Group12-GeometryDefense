package geometrydefense.geometrydefense;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;


public class LevelSelect extends AppCompatActivity {


    //define and create the list of levels, which will be assigned to the buttons
    private Level[] levelList = new Level[1];
    //levelList1 = new Level();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_select_layout);


        //back button-returns to main menu
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LevelSelect.this.finish();
            }
        });

        //get grid layout to add buttons to
        GridLayout grid = ((GridLayout) findViewById(R.id.level_list));

        //loop to add level buttons-each button has listener to start its level
        for (int i = 1; i <= levelList.length; i++) {
            Button btn = new Button(this);
            btn.setId(i);
            final int id = btn.getId();
            btn.setText(id + "");
            btn.setBackgroundColor(Color.rgb(70, 80, 90));
            grid.addView(btn);
            btn = ((Button) findViewById(id));
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent myIntent = new Intent(LevelSelect.this, LevelActivity.class);
                    LevelSelect.this.startActivity(myIntent);
                }
            });
        }

    }



}


