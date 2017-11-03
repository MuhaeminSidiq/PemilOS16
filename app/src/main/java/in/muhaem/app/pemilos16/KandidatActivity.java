package in.muhaem.app.pemilos16;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class KandidatActivity extends AppCompatActivity implements View.OnClickListener {
public ImageView btn1, btn2, btn3;
public String snisn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kandidat);
        btn1 = this.findViewById(R.id.pilihk1);
        btn1.setOnClickListener(this);
        btn2 = this.findViewById(R.id.pilihk2);
        btn2.setOnClickListener(this);
        btn3 = this.findViewById(R.id.pilihk3);
        btn3.setOnClickListener(this);
        Intent intent = getIntent();
        snisn = intent.getStringExtra("inisn");
    }

    public void onClick(View v) {
        String sURLValidate = "";
        switch (v.getId()) {
            case R.id.pilihk1:
                sURLValidate = "http://muhaem.in/pemilos16.php?nisn="+snisn+"&pilih=1";
                break;
            case R.id.pilihk2:
                sURLValidate = "http://muhaem.in/pemilos16.php?nisn="+snisn+"&pilih=2";
                break;
            case R.id.pilihk3:
                sURLValidate = "http://muhaem.in/pemilos16.php?nisn="+snisn+"&pilih=3";
                break;
        }

        String rsltVal = "";

        ConnectivityManager cmCheckConn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cmCheckConn.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                cmCheckConn.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            try {
                rsltVal = new postVal().execute(sURLValidate).get().toString().replace("<reply>","").replace("</reply>","");
            } catch (Exception e) {
                rsltVal = "Error";
            }
            if (rsltVal.contains("1")) {
                Toast.makeText(this, "Terima kasih telah memilih",
                        Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "Gagal mengirimkan pilihan Anda, coba lagi!",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Anda tidak bisa memilih karena tidak terhubung ke jaringan",
                    Toast.LENGTH_LONG).show();
        }
    }



    private class postVal extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                return getVal(params[0]);
            } catch (IOException e) {
                return "<Failed>";
            }
        }
    }

    private String getVal (String myurl) throws IOException {
        InputStream is = null;
        int length = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            is = conn.getInputStream();
            String contentAsString = convertInputStreamToString(is, length);
            if (contentAsString.contains("<reply>") && contentAsString.contains("</reply>")) {
                return contentAsString;
            } else {
                return "<Failed>";
            }
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public String convertInputStreamToString(InputStream stream, int length) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[length];
        reader.read(buffer);
        return new String(buffer);
    }
}
