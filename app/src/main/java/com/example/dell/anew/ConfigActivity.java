package com.example.dell.anew;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class ConfigActivity extends AppCompatActivity {
    public final String TAG="ConfigActivity";
    EditText dollartext;
    EditText eurotext;
    EditText wontext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        Intent intent=getIntent();
        float dollar2=intent.getFloatExtra("dollar_rate_key",0.3f);
        float euro2=intent.getFloatExtra("euro_rate_key",0.0f);
        float won2=intent.getFloatExtra("won_rate_key",0.0f);
        Log.i(TAG, "dollar_rate_key: "+dollar2);
        Log.i(TAG, "euro_rate_key: "+euro2);
        Log.i(TAG, "won_rate_key: "+won2);

        dollartext=findViewById(R.id.dollar_rate);
        eurotext=findViewById(R.id.euro_rate);
        wontext=findViewById(R.id.won_rate);

        dollartext.setText(String.valueOf(dollar2));
        eurotext.setText(String.valueOf(euro2));
        wontext.setText(String.valueOf(won2));

    }
    public  void save(View btn){
        float newdollar=Float.parseFloat(dollartext.getText().toString());
        float neweuro=Float.parseFloat(eurotext.getText().toString());
        float newwon=Float.parseFloat(wontext.getText().toString());
        Log.i(TAG, "newdollar: "+newdollar);
        Log.i(TAG, "neweuro: "+neweuro);
        Log.i(TAG, "newwon: "+newwon);

        Intent intent=getIntent();
        Bundle bdl=new Bundle();
        bdl.putFloat("key_dollar",newdollar);
        bdl.putFloat("key_euro",neweuro);
        bdl.putFloat("key_won",newwon);
        intent.putExtras(bdl);
        setResult(2,intent);
        finish();

    }
}
