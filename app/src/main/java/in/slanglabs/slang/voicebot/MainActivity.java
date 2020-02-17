package in.slanglabs.slang.voicebot;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;
import in.slanglabs.platform.SlangLocale;

public class MainActivity extends AppCompatActivity {

    private static final String SLANG_BUDDY_ID = "ed6454d6c63b4addbe77dce70e1172fd";
    private static final String SLANG_API_KEY = "0889ab1a2c014638b6ef9ca6188c2224";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SlangInterface.init(
                this.getApplication(),
                SLANG_BUDDY_ID,
                SLANG_API_KEY,
                SlangLocale.LOCALE_HINDI_IN,
                new SlangInterface.ActionHandler() {
                    @Override
                    public void onReady() {
                        SlangInterface.show(MainActivity.this);
                    }

                    @Override
                    public void onAction(String spokenText, String englishText) {
                        TextView resultView = findViewById(R.id.result);
                        resultView.setText(spokenText + ":" + englishText);

                        //To speak out prompt to user.
                        SlangInterface.notifyUser("Please find the original and translated text on your screen");
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(MainActivity.this, "Error:" + errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
}
