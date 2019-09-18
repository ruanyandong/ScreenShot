package com.example.screenshot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class testActicity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        findViewById(R.id.btn_jp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wake("qtt://invite?need_login=0&url=https%3A%2F%2Fh5ssl.1sapp.com%2Factivity_dest%2Fyq%2Findex.html%3Fishidestatus%3D3");
            }
        });

    }

    private void wake(String url) {
        Uri uri = Uri.parse(url);     //浏览器(网址必须带http)
        Intent it  = new Intent(Intent.ACTION_VIEW,uri);  //Intent.ACTION_VIEW不带引号
        startActivity(it);
    }
}
