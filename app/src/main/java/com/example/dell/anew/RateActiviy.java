package com.example.dell.anew;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Documented;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RateActiviy extends AppCompatActivity implements Runnable {
    EditText rmb;
    TextView showout;
    private  final String TAG="Rate";
    float dollarrate;
    float eurorate;
    float wonrate;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_activiy);
        rmb=findViewById(R.id.rmb);
        showout=findViewById(R.id.showout);
        SharedPreferences sharedPreferences=getSharedPreferences("myrate",Activity.MODE_PRIVATE);
        dollarrate=sharedPreferences.getFloat("dollar_rate",0.0f);
        eurorate=sharedPreferences.getFloat("euro_rate",0.0f);
        wonrate= sharedPreferences.getFloat("won_rate",0.0f);

        Log.i(TAG, "onCreate:dollar_rate= "+dollarrate);
        Log.i(TAG, "onCreate:euro_rate= "+eurorate);
        Log.i(TAG, "onCreate:won_rate= "+wonrate);
        Thread t=new Thread(this);
        t.start();

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==5){
                    Bundle bdl= (Bundle) msg.obj;
                   dollarrate=bdl.getFloat("dollar-rate",0.0f);
                    eurorate=bdl.getFloat("euro-rate",0.0f);
                    wonrate=bdl.getFloat("won-rate",0.0f);
                }

                super.handleMessage(msg);
            }
        };


    }
    public  void onClick(View btn){
        String str=rmb.getText().toString();
        float r=0;

        if(str.length()>0){
            r=Float.parseFloat(str);
        }
        else if(str.length()==0){
            Toast.makeText(this,"请输入金额",Toast.LENGTH_SHORT);
        }
        if(btn.getId()==R.id.btn_dollar){

            showout.setText(String.format("%.2f",r*dollarrate));
        }
        else if(btn.getId()==R.id.btn_euro){
            showout.setText(String.format("%.2f",r*eurorate));
        }
        else if(btn.getId()==R.id.btn_won){
            showout.setText(String.format("%.2f",r*wonrate));
        }

    }
    public  void openOne(View btn){
        openconfig();

    }

    private void openconfig() {
        Intent config=new Intent(this,ConfigActivity.class);

        config.putExtra("dollar_rate_key",dollarrate);
        config.putExtra("euro_rate_key",eurorate);
        config.putExtra("won_rate_key",wonrate);
        Log.i(TAG, "dollar_rate_key: "+dollarrate);
        Log.i(TAG, "euro_rate_key: "+eurorate);
        Log.i(TAG, "won_rate_key: "+wonrate);
        startActivityForResult(config,1);
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        getMenuInflater().inflate(R.menu.rate,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        openconfig();
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==1&&resultCode==2){

            Bundle bundle=data.getExtras();
            dollarrate=bundle.getFloat("key_dollar",0f);
            eurorate=bundle.getFloat("key_euro",0f);
            wonrate=bundle.getFloat("key_won",0f);
            Log.i(TAG, "onActivityResult: dollarrate"+dollarrate);
            Log.i(TAG, "onActivityResult: eurorate"+eurorate);
            Log.i(TAG, "onActivityResult: wonrate"+wonrate);

            SharedPreferences sharedPreferences=getSharedPreferences("myrate",Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putFloat("dollar_rate",dollarrate);
            editor.putFloat("euro_rate",eurorate);
            editor.putFloat("won_rate",wonrate);
            editor.commit();
            Log.i(TAG, "onActivityResult: 数据已保存至sp");

        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void run() {
        Log.i(TAG, "run: ......");
        /*for(int i=0;i<3;i++){
            Log.i(TAG, "run: i="+i);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        Bundle bundle=new Bundle();

       /* try {
            URL url=new URL("http://www.usd-cny.com/bankofchina.htm");
            HttpURLConnection httpURLConnection= (HttpURLConnection) url.openConnection();
            InputStream in=httpURLConnection.getInputStream();
            String html=inputString2String(in);
            Document doc=Jsoup.parse(html);
            Log.i(TAG, "run: " + doc.title());
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        Document doc = null;

        try {
            doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();
            Log.i(TAG, "run: " + doc.title());
            doc.getElementsByTag("table");
            Elements tables=doc.getElementsByTag("table");
            for (Element table:tables){
                Log.i(TAG, "run: table="+table);
            }
            Element table6=tables.get(0);
            /*Log.i(TAG, "run: table6"+table6);*/
            Elements tds=table6.getElementsByTag("td");
            /*for (Element td:tds){
                Log.i(TAG, "run: table="+tds);
            }*/
            for (int i=0;i<tds.size();i+=6){
                Element td1=tds.get(i);
                Element td2=tds.get(i+5);
                Log.i(TAG, "run: "+td1.text()+"==>"+td2.text());
                if("美元".equals((td1.text()))){
                    bundle.putFloat("dollar-rate",100f/Float.parseFloat(td2.text()));
                }
                else if("欧元".equals((td1.text()))){
                    bundle.putFloat("euro-rate",100f/Float.parseFloat(td2.text()));
                }
                else if("韩元".equals((td1.text()))){
                    bundle.putFloat("won-rate",100f/Float.parseFloat(td2.text()));
                }
                Message msg=handler.obtainMessage(5);
                msg.obj=bundle;
                handler.sendMessage(msg);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }




    }
    private String inputString2String(InputStream inputStream) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(inputStream); ByteArrayOutputStream buf = new ByteArrayOutputStream(); int result = bis.read(); while(result != -1) { buf.write((byte) result); result = bis.read(); } String str = buf.toString(); return str;

    }
}
