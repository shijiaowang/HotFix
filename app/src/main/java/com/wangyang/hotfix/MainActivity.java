package com.wangyang.hotfix;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.wangyangLibrary.hotfix.HotFixDirManager;
import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void textClick(View view) throws Exception {
        Toast.makeText(this,"我是有bug 的版本", Toast.LENGTH_SHORT).show();
    }

    public void putClick(View view) {
        File file = new File(Environment.getExternalStorageDirectory(),"classes.dex");
        HotFixDirManager.inputDex(file,this);
    }
}
