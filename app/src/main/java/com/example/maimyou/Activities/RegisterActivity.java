package com.example.maimyou.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.maimyou.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import maes.tech.intentanim.CustomIntent;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    long maxId = 0;
    LinearLayout linearLayout, forLogin;
    TextView enterTitle, RegistrationTitle2,forgotText;
    EditText nameE, emailE, passE, confirmPassE, focused;
    FrameLayout name, email, pass, confirmPass, forReg;
    ScrollView scrollView;
    ImageView back;

    //    ImageView background;
    DatabaseReference reff;
    Boolean register = false;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;
    boolean fail = false;
    public static final String SHARED_PREFS = "sharedPrefs";
    DocumentReference noteRef;
    Activity activity=this;

    public void onback(View view) {
        onBackPressed();
    }

    public void Forgot(View view){
        Toast.makeText(activity, "Under maintenance!", Toast.LENGTH_SHORT).show();
    }

    public void Register(View view) {
        if (register) {
            final String name = nameE.getText().toString().trim();
            final String email = emailE.getText().toString().trim();
            String password = passE.getText().toString().trim();
            String ConfinrmPassword = confirmPassE.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                nameE.setError("Id is Required.");
                return;
            }

            if (TextUtils.isEmpty(email)) {
                emailE.setError("Email address is Required.");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                passE.setError("Password is Required.");
                return;
            }

            if (password.length() < 6) {
                passE.setError("Password Must be >= 6 Characters");
                return;
            }

            if (password.compareTo(ConfinrmPassword) != 0) {
                confirmPassE.setError("Password is not similar.");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            // register the user in firebase

            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "User Created.", Toast.LENGTH_SHORT).show();
                    userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                    DocumentReference documentReference = fStore.collection("users").document(userID);
                    Map<String, Object> user = new HashMap<>();
                    reff.child(String.valueOf(maxId + 1)).child("Id").setValue(name);
                    reff.child(String.valueOf(maxId + 1)).child("email").setValue(email);

                    user.put("Id", Long.toString(maxId + 1));
                    saveData(Long.toString(maxId + 1), "Id");

                    documentReference.set(user).addOnSuccessListener(aVoid -> {
                        fail = false;
                        Log.d(TAG, "onSuccess: user Profile is created for " + userID);
                    }).addOnFailureListener(e -> {
                        Log.d(TAG, "onFailure: " + e.toString());
                        progressBar.setVisibility(View.GONE);
                        fail = true;
                    });
                    if (!fail) {
                        saveData("0", "Selection");
                        startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
                        startActivity(new Intent(getApplicationContext(), ScanMarksActivity.class));
                        finish();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Error ! " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {

            final String email = emailE.getText().toString().trim();
            String password = passE.getText().toString().trim();


            if (TextUtils.isEmpty(email)) {
                emailE.setError("Email is Required.");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                passE.setError("Password is Required.");
                return;
            }

            if (password.length() < 6) {
                passE.setError("Password Must be >= 6 Characters");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            // authenticate the user

            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    saveId();
                } else {
                    Toast.makeText(RegisterActivity.this, "Error ! " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }

            });
        }
    }

    @SuppressLint("SetTextI18n")
    public void Login(View view) {
        clearFocus();
        if (register) {
            name.setVisibility(View.GONE);
            confirmPass.setVisibility(View.GONE);
            forgotText.setVisibility(View.VISIBLE);
            emailE.setText("");
            passE.setText("");
            emailE.setError(null);
            passE.setError(null);
            emailE.clearFocus();
            passE.clearFocus();
            passE.setHint("Password");
            enterTitle.setText("LOGIN");
            enterTitle.setShadowLayer(5, 3, 3, Color.BLACK);
            linearLayout.setVisibility(View.VISIBLE);
            forReg.setVisibility(View.GONE);
            forLogin.setVisibility(View.VISIBLE);
            register = false;

        } else {
            name.setVisibility(View.VISIBLE);
            confirmPass.setVisibility(View.VISIBLE);
            forgotText.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);

            nameE.setText("");
            emailE.setText("");
            passE.setText("");
            confirmPassE.setText("");
            nameE.setError(null);
            emailE.setError(null);
            passE.setError(null);
            confirmPassE.setError(null);
            nameE.clearFocus();
            emailE.clearFocus();
            passE.clearFocus();
            confirmPassE.clearFocus();

            passE.setHint("Camsys Password");
            forReg.setVisibility(View.VISIBLE);
            forLogin.setVisibility(View.GONE);
            enterTitle.setText("CREATE");
            enterTitle.setShadowLayer(5, 3, 3, Color.BLACK);
            register = true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registter);
//        saveData("", "Id");
//        FirebaseAuth.getInstance().signOut();//logout
//        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
//        finish();
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        iniFunc();
        initBackground();

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
            finish();
        }

//            if (!loadData("log").isEmpty()) {
//                startActivity(new Intent(getApplicationContext(), DashBoard.class));
//                finish();
//            }
    }

//    public String loadData(String name) {
//        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
//        if (sharedPreferences == null) {
//            return "";
//        }
//        return sharedPreferences.getString(name, "");
//    }

    @SuppressLint("ClickableViewAccessibility")
    public void initBackground() {
        back = findViewById(R.id.scrollView2);
        scrollView = findViewById(R.id.scrole);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        scrollView.setOnTouchListener((v, event) -> {
            clearFocus();
            return false;
        });
        back.setOnTouchListener((v, event) -> {
            clearFocus();
            return false;
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void clearFocus() {
        if (focused != null) {
            focused.clearFocus();
            hideKeyboard(focused);
        }
    }

    public void iniFunc() {
        RegistrationTitle2 = findViewById(R.id.RegistrationTitle2);
        RegistrationTitle2.setShadowLayer(5, 3, 3, Color.BLACK);
        forgotText = findViewById(R.id.forgotText);
        forLogin = findViewById(R.id.forLoginTitle);
        forReg = findViewById(R.id.forRegisTiltle);
        enterTitle = findViewById(R.id.enterTitle);
        enterTitle.setShadowLayer(5, 3, 3, Color.BLACK);
        linearLayout = findViewById(R.id.forLogin);
        name = findViewById(R.id.Id);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        confirmPass = findViewById(R.id.conPass);
        nameE = findViewById(R.id.studentId);
        nameE.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                focused = nameE;
            }
        });
        emailE = findViewById(R.id.emailadress);
        emailE.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                focused = emailE;
            }
        });
        passE = findViewById(R.id.password);
        passE.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                focused = passE;
            }
        });
        confirmPassE = findViewById(R.id.confirmpassword);
        confirmPassE.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                focused = confirmPassE;
            }
        });
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);
        reff = FirebaseDatabase.getInstance().getReference().child("Member");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                maxId = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void saveData(String data, String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, data);
        editor.apply();
    }

    public void saveId() {
        String userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        noteRef = fStore.collection("users").document(userID);
        noteRef.addSnapshotListener(this, (documentSnapshot, e) -> {
            if (e != null) {
                Toast.makeText(RegisterActivity.this, "Error while loading!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, e.toString());
                FirebaseAuth.getInstance().signOut();//logout
                progressBar.setVisibility(View.GONE);
                return;
            }

            if (Objects.requireNonNull(documentSnapshot).exists()) {
                String Id = documentSnapshot.getString("Id");
                saveData(Id, "Id");
                Toast.makeText(RegisterActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
                finish();
            }
        });
    }

//    public int dpToPx(int dip) {
//        Resources r = getResources();
//        float px = TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_DIP,
//                dip,
//                r.getDisplayMetrics()
//        );
//        return (int) px;
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "left-to-right");
    }

    @Override
    public void onBackPressed() {
        if (register) {
            Login(null);
        }
    }
}
