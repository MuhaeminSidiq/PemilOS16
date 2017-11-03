package in.muhaem.app.pemilos16;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
public EditText etNISN;
private Button btnPilih;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNISN = this.findViewById(R.id.etNISN);
        btnPilih = this.findViewById(R.id.btn_pilih);
        btnPilih.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (etNISN.getText().toString().length() != 0) {
            String sNISN = etNISN.getText().toString();

            String sURLValidate = "http://muhaem.in/pemilos16.php?nisn="+sNISN;
            String rsltVal = "";
            ConnectivityManager cmCheckConn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cmCheckConn.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    cmCheckConn.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                try {
                    rsltVal = new postVal().execute(sURLValidate).get().toString().replace("<reply>","").replace("</reply>","");
                } catch (Exception e) {
                    rsltVal = "Error";
                }
                if (rsltVal.contains("2")) {
                    Intent intent = new Intent(MainActivity.this,KandidatActivity.class);
                    intent.putExtra("inisn",sNISN);
                    startActivity(intent);

                    Toast.makeText(this, "Silahkan pilih kandidat sesuai hati nurani Anda!",
                                Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    if (rsltVal.contains("1")) {
                        Toast.makeText(this, "Anda tidak terdaftar sebagai pemilih atau bukan jadwal pemilihan, atau sudah pernah memilih",
                                Toast.LENGTH_LONG).show();
                    } else {
                        if (rsltVal.contains("0")) {
                        Toast.makeText(this, "Parameter NISN salah",
                                Toast.LENGTH_LONG).show();
                        } else {
                        Toast.makeText(this, "Tidak bisa memilih, tanya Panitia PemilOS16!",
                                Toast.LENGTH_LONG).show();
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Anda tidak bisa memilih karena tidak terhubung ke jaringan",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Isi kolom NISN terlebih dahulu!",
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
