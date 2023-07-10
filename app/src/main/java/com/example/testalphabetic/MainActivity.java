package com.example.testalphabetic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);

    navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId())
            {
                case R.id.menu_exam:
                {
                    Intent intent = new Intent(MainActivity.this, test.class);
                    startActivity(intent);
                    break;


                }
                case R.id.menu_github:
                {
                    String url = "https://github.com/anss0005/Alpha-Test";
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW);
                    websiteIntent.setData(Uri.parse(url));
                    startActivity(websiteIntent);
                    break;
                }
            }




            return false;
        }
    });




    }


}