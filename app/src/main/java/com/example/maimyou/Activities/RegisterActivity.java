package com.example.maimyou.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maimyou.Classes.UriUtils;
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

import static android.widget.Toast.LENGTH_SHORT;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    long maxId = 0;
    LinearLayout linearLayout, forLogin, Container;
    TextView enterTitle, RegistrationTitle2, forgotText;
    EditText idE, CamsysPassE, emailE, passE, confirmPassE, focused;
    FrameLayout id, CamsysPass, email, pass, confirmPass, forReg;
    ScrollView scrollView;
    ImageView back, logoImg;

    //    ImageView background;
    DatabaseReference reff;
    Boolean register = false;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;
    boolean fail = false, doubleBackToExitPressedOnce = false;
    public static final String SHARED_PREFS = "sharedPrefs";
    DocumentReference noteRef;
    public static final int STARTUP_DELAY = 300;
    public static final int ANIM_ITEM_DURATION = 1000;
    public static final int EDITTEXT_DELAY = 300;
    public static final int BUTTON_DELAY = 300;
    public static final int VIEW_DELAY = 400;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public void onback(View view) {
        onBackPressed();
    }

    public void Forgot(View view) {
        Intent intent = new Intent(getBaseContext(), ForgotPasswordActivity.class);
        intent.putExtra("EmailAddress", emailE.getText().toString());
        startActivity(intent);
    }

    public void Register(View view) {
        register();
    }

    public void register() {
        if (register) {
            String id = idE.getText().toString().trim();
            String camsysPassword = CamsysPassE.getText().toString().trim();
            String email = emailE.getText().toString().trim();
            String password = passE.getText().toString().trim();
            String ConfinrmPassword = confirmPassE.getText().toString().trim();

            if (TextUtils.isEmpty(id)) {
                idE.setError("Id is Required.");
                return;
            }

            if (TextUtils.isEmpty(camsysPassword)) {
                idE.setError("Camsys password is Required.");
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
            if (checkPermission(this)) {

                progressBar.setVisibility(View.VISIBLE);

                // register the user in firebase

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "User Created.", Toast.LENGTH_SHORT).show();
                        userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                        DocumentReference documentReference = fStore.collection("users").document(userID);
                        Map<String, Object> user = new HashMap<>();
                        reff.child(String.valueOf(maxId + 1)).child("camsysId").setValue(id);
                        reff.child(String.valueOf(maxId + 1)).child("camsysPassword").setValue(camsysPassword);
                        reff.child(String.valueOf(maxId + 1)).child("email").setValue(email);

                        user.put("Id", Long.toString(maxId + 1));
                        saveData(Long.toString(maxId + 1), "Id");
                        saveData(camsysPassword, "camsysPassword");
                        saveData(id, "camsysId");

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
                            Intent intent = new Intent(getBaseContext(), DashBoardActivity.class);
                            intent.putExtra("InfoAvail", false);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Error ! " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
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
            if (checkPermission(this)) {

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
    }

    @SuppressLint("SetTextI18n")
    public void Login(View view) {
        clearFocus();
        if (register) {
            id.setVisibility(View.GONE);
            CamsysPass.setVisibility(View.GONE);
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
            id.setVisibility(View.VISIBLE);
            CamsysPass.setVisibility(View.VISIBLE);
            confirmPass.setVisibility(View.VISIBLE);
            forgotText.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);

            idE.setText("");
            CamsysPassE.setText("");
            emailE.setText("");
            passE.setText("");
            confirmPassE.setText("");
            idE.setError(null);
            CamsysPassE.setError(null);
            emailE.setError(null);
            passE.setError(null);
            confirmPassE.setError(null);
            idE.clearFocus();
            CamsysPassE.clearFocus();
            emailE.clearFocus();
            passE.clearFocus();
            confirmPassE.clearFocus();

            passE.setHint("Password");
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
        setTheme(R.style.CustomTheme1);
        setContentView(R.layout.activity_registter);
//        saveData("", "Id");
//        FirebaseAuth.getInstance().signOut();//logout
//        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
//        finish();
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        iniFunc();
        initBackground();
        animationFunc();
        if (fAuth.getCurrentUser() != null) {
            if (checkPermission(this)) {
                startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
                finish();
            }
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

    public void iniFunc() {
        RegistrationTitle2 = findViewById(R.id.RegistrationTitle2);
        RegistrationTitle2.setShadowLayer(5, 3, 3, Color.BLACK);
        logoImg = findViewById(R.id.logoImg);
        Container = findViewById(R.id.Container);
        forgotText = findViewById(R.id.forgotText);
        forLogin = findViewById(R.id.forLoginTitle);
        forReg = findViewById(R.id.forRegisTiltle);
        enterTitle = findViewById(R.id.enterTitle);
        enterTitle.setShadowLayer(5, 3, 3, Color.BLACK);
        linearLayout = findViewById(R.id.forLogin);
        id = findViewById(R.id.Id);
        CamsysPass = findViewById(R.id.CamsysPass);
        CamsysPassE = findViewById(R.id.CamsysPassE);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        confirmPass = findViewById(R.id.conPass);
        idE = findViewById(R.id.studentId);
        idE.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                focused = idE;
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

    public void animationFunc() {
        logoImg.post(() -> {
            Animation animation2 = new TranslateAnimation(0, 0, logoImg.getHeight(), 0);
            animation2.setStartTime(STARTUP_DELAY);
            animation2.setDuration(ANIM_ITEM_DURATION);
            animation2.setInterpolator(new DecelerateInterpolator(1.2f));
            animation2.setFillAfter(true);
            logoImg.startAnimation(animation2);
        });
        RegistrationTitle2.post(() -> {
            Animation animation2 = new TranslateAnimation(0, 0, -RegistrationTitle2.getHeight(), 0);
            animation2.setStartTime(STARTUP_DELAY);
            animation2.setDuration(ANIM_ITEM_DURATION);
            animation2.setInterpolator(new DecelerateInterpolator(1.2f));
            animation2.setFillAfter(true);
            RegistrationTitle2.startAnimation(animation2);
        });

        for (int i = 0; i < Container.getChildCount(); i++) {
            View v = Container.getChildAt(i);
            ViewPropertyAnimatorCompat viewAnimator;

            if (v instanceof FrameLayout) {
                viewAnimator = ViewCompat.animate(v)
                        .scaleY(1).scaleX(1)
                        .setStartDelay((EDITTEXT_DELAY * i) + 500)
                        .setDuration(500);
            } else if (v instanceof RelativeLayout) {
                viewAnimator = ViewCompat.animate(v)
                        .scaleY(1).scaleX(1)
                        .setStartDelay((BUTTON_DELAY * i) + 500)
                        .setDuration(500);
            } else {
                viewAnimator = ViewCompat.animate(v)
                        .translationY(0).alpha(1)
                        .setStartDelay((VIEW_DELAY * i) + 500)
                        .setDuration(1000);
            }

            viewAnimator.setInterpolator(new DecelerateInterpolator()).start();
        }


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
                Error(e.toString());
                return;
            }
            if (Objects.requireNonNull(documentSnapshot).exists()) {

                String Id = documentSnapshot.getString("Id");

                if (Id != null) {
                    reff.child(Id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            saveData(Id, "Id");
                            if (snapshot.child("camsysPassword").exists()) {
                                saveData(snapshot.child("camsysPassword").getValue().toString(), "camsysPassword");
                            } else {
                                Error("Camsys Password not found!");
                                return;
                            }
                            if (snapshot.child("camsysId").exists()) {
                                saveData(snapshot.child("camsysId").getValue().toString(), "camsysId");
                            } else {
                                Error("Camsys Id not found!");
                                return;
                            }
                            saveData("0", "Selection");
                            Toast.makeText(RegisterActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    Error("Something went Wrong!");
                }
            } else {
                Error("Something went Wrong!");
            }
        });
    }

    public void Error(String msg) {
        Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
        FirebaseAuth.getInstance().signOut();//logout
        progressBar.setVisibility(View.GONE);
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

    public boolean checkPermission(Activity activity) {
        // Check if we have write permission
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission accepted", Toast.LENGTH_SHORT).show();
                if (fAuth.getCurrentUser() != null) {
                    startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
                    finish();
                } else {
                    register();
                }
            } else {
                Toast.makeText(this, "permissions are required", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "permissions are required", Toast.LENGTH_SHORT).show();
        }
    }


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
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", LENGTH_SHORT).show();

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    }
}
