package com.example.maimyou.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.maimyou.Fragments.FragmentEdit;
import com.example.maimyou.Fragments.FragmentHome;
import com.example.maimyou.Fragments.FragmentProfile;
import com.example.maimyou.Fragments.FragmentCamsys;
import com.example.maimyou.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import maes.tech.intentanim.CustomIntent;

import static android.widget.Toast.LENGTH_SHORT;
import static com.example.maimyou.Activities.RegisterActivity.SHARED_PREFS;

public class DashBoardActivity extends AppCompatActivity {
    boolean doubleBackToExitPressedOnce = false;
    FragmentProfile fragmentProfile;
    DashBoardActivity dashBoardActivity=this;
    Context context = this;
    public static BottomNavigationView bottomNav;

    public void FragEdit(View view){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new FragmentEdit()).commit();
    }

    public void setManually(View view){

    }

    public void empty(View view){

    }

    public void edit(View view){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new FragmentCamsys(dashBoardActivity)).commit();
    }

    public void Subjects(View view) {
        startActivity(new Intent(getApplicationContext(), SubjectsActivity.class));
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide0);
    }

    public void UploadCourse(View view) {
        startActivity(new Intent(getApplicationContext(), CourseStructure.class));
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide0);
    }

    public void Progress(View view) {
        startActivity(new Intent(getApplicationContext(), ProgressActivity.class));
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide0);
    }

    public void UpdateMarks(View view) {
        startActivity(new Intent(getApplicationContext(), ScanMarksActivity.class));
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide0);
    }

    public void Calculator(View view) {
        startActivity(new Intent(getApplicationContext(), calculatorActivity.class));
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide0);
    }

    public void logout(View view) {
        saveData("", "Id");
        FirebaseAuth.getInstance().signOut();//logout
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        finish();
        CustomIntent.customType(this, "right-to-left");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int height = displayMetrics.heightPixels;
//        int width = displayMetrics.widthPixels;
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        fragmentProfile = new FragmentProfile(loadData("Id"),context,dashBoardActivity);

        if (savedInstanceState == null) {
            if (loadData("Selection").compareTo("0") == 0) {
                bottomNav.setSelectedItemId(R.id.profile);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragmentProfile).commit();
            } else if (loadData("Selection").compareTo("1") == 0) {
                bottomNav.setSelectedItemId(R.id.home);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FragmentHome()).commit();
            } else {
                bottomNav.setSelectedItemId(R.id.home);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FragmentHome()).commit();
            }
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.profile:
                            saveData("0", "Selection");
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    fragmentProfile).commit();
                            break;
                        case R.id.home:
                            saveData("1", "Selection");
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new FragmentHome()).commit();
                            break;
                    }
                    return true;
                }
            };

    public String loadData(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if (sharedPreferences == null) {
            return "";
        }
        return sharedPreferences.getString(name, "");
    }

    public void saveData(String data, String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, data);
        editor.apply();
    }
    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }
}