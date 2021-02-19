package com.example.maimyou.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.maimyou.Classes.ActionListener;
import com.example.maimyou.Classes.Trimester;
import com.example.maimyou.Classes.UriUtils;
import com.example.maimyou.Classes.subjects;
import com.example.maimyou.Fragments.FragmentEdit;
import com.example.maimyou.Fragments.FragmentHome;
import com.example.maimyou.Fragments.FragmentProfile;
import com.example.maimyou.Fragments.FragmentCamsys;
import com.example.maimyou.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import im.delight.android.webview.AdvancedWebView;
import ir.sohreco.androidfilechooser.ExternalStorageNotAvailableException;
import ir.sohreco.androidfilechooser.FileChooser;
import maes.tech.intentanim.CustomIntent;

import static android.widget.Toast.LENGTH_SHORT;
import static com.example.maimyou.Activities.RegisterActivity.SHARED_PREFS;
import static com.example.maimyou.Fragments.FragmentCamsys.webView;

public class DashBoardActivity extends AppCompatActivity implements AdvancedWebView.Listener {
    boolean doubleBackToExitPressedOnce = false, updateFromCamsys = false;
    FragmentProfile fragmentProfile;
    FragmentEdit fragmentEdit;
    public static String Intake = "";
    public static TextView IntakeView;
    public static ActionListener actionListener = new ActionListener();
    ArrayList<Trimester> trimesters = new ArrayList<>();
    ArrayList<subjects> subjects = new ArrayList<>();

    DashBoardActivity dashBoardActivity = this;
    Context context = this;
    public static boolean InfoAvail = true;
    public static int fragmentIndex = 0;
    public static BottomNavigationView bottomNav;

    public void DashBack(View view) {
        onBackPressed();
    }

    public void FragEdit(View view) {
        if (fragmentEdit.finishedLoading) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    fragmentEdit).commit();
        } else {
            Toast.makeText(context, "Loading!", LENGTH_SHORT).show();
        }
    }

    public void setManually(View view) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new FragmentEdit(loadData("Id"), context, dashBoardActivity)).commit();
    }

    public void empty(View view) {

    }

    public void OpenCamsys(View view) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new FragmentCamsys(this)).commit();
    }

    public void uploadPdf(View view) {
        if (verifyStoragePermissions(this)) {

//            new MaterialFilePicker()
//                    // Pass a source of context. Can be:
//                    //    .withActivity(Activity activity)
//                    //    .withFragment(Fragment fragment)
//                    //    .withSupportFragment(androidx.fragment.app.Fragment fragment)
//                    .withActivity(this)
//                    // With cross icon on the right side of toolbar for closing picker straight away
//                    // Entry point path (user will start from it)
//                    // Root path (user won't be able to come higher than it)
//                    // Showing hidden files
//                    .withHiddenFiles(true)
//                    // Want to choose only jpg images
//                    .withFilter(Pattern.compile(".*\\.(jpg|jpeg)$"))
//                    // Don't apply filter to directories names
//                    .withFilterDirectories(false)
//                    .withTitle("Sample title")
//                    .withRequestCode(1)
//                    .start();

//            Intent intent = new Intent();
//            intent.setType("application/pdf");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            String[] mimetypes = {"application/pdf"};
//            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
//            startActivityForResult(Intent.createChooser(intent, "Choose Pdf"), 1212);
//

            FileChooser.Builder builder = new FileChooser.Builder(FileChooser.ChooserType.FILE_CHOOSER, (FileChooser.ChooserListener) path -> {
                String[] selectedFilePaths = path.split(FileChooser.FILE_NAMES_SEPARATOR);
                scanResFromPath(selectedFilePaths[0]);
                // Do whatever you want to do with selected files
            }).setInitialDirectory(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
                    .setFileFormats(new String[]{".pdf"})
                    .setListItemsTextColor(R.color.colorPrimary)
                    .setMultipleFileSelectionEnabled(false)
                    .setSelectMultipleFilesButtonText("Select Files");
            try {
                FileChooser fileChooserFragment = builder.build();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fileChooserFragment).commit();
                fragmentIndex = 4;
            } catch (ExternalStorageNotAvailableException e) {
                e.printStackTrace();
            }
        }
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
        fragmentProfile = new FragmentProfile(loadData("Id"), context, dashBoardActivity);
        fragmentEdit = new FragmentEdit(loadData("Id"), context, dashBoardActivity);

        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("Profile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    InfoAvail = true;
                } else {
                    if (!updateFromCamsys) {
                        InfoAvail = false;
                        if (fragmentIndex != 0) {
                            try {
                                bottomNav.setSelectedItemId(R.id.profile);
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                        new FragmentCamsys(dashBoardActivity)).commit();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        updateFromCamsys = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (savedInstanceState == null) {
            if (loadData("Selection").compareTo("0") == 0) {
                bottomNav.setSelectedItemId(R.id.profile);
                if (InfoAvail) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            fragmentProfile).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new FragmentCamsys(this)).commit();
                }
            } else if (loadData("Selection").compareTo("1") == 0) {
                bottomNav.setSelectedItemId(R.id.home);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FragmentHome()).commit();
            } else {
                bottomNav.setSelectedItemId(R.id.profile);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FragmentCamsys(this)).commit();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1212 && resultCode == RESULT_OK && data != null) {
            String path = UriUtils.getPathFromUri(context, data.getData());

            scanResFromPath(path);
        } else {
            Toast.makeText(getApplicationContext(), "Please select the pdf file.", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (webView != null) {

            webView.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void scanResFromPath(String path) {
        try {
            StringBuilder parsedText = new StringBuilder();
            PdfReader reader = new PdfReader(path);

            for (int i = 0; i < reader.getNumberOfPages(); i++) {
                parsedText.append(PdfTextExtractor.getTextFromPage(reader, i + 1).trim()).append("\n"); //Extracting the content from the different pages
            }
            reader.close();
            scan(parsedText.toString());

        } catch (Exception e) {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new FragmentCamsys(this)).commit();
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {

            webView.onResume();
        }
        // ...
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        if (webView != null) {

            webView.onPause();
        }
        // ...
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {

            webView.onDestroy();
        }
        // ...
        super.onDestroy();
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
    }

    @Override
    public void onPageFinished(String url) {
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
    }

    @Override
    public void onExternalPageRequest(String url) {
    }

    public void scan(String res) {
        trimesters.clear();
        subjects.clear();
        String Name = "", Id = "", Degree = "";
        String[] lines = res.split("\n", -1);
        for (int i = 0; i < lines.length; i++) {
            if (Id.isEmpty() && lines[i].toLowerCase().contains("id")) {
                Id = getString(":", lines[i]).trim();
            } else if (Name.isEmpty() && lines[i].toLowerCase().contains("name")) {
                Name = getString(":", lines[i]).trim();
            } else if (Degree.isEmpty() && lines[i].toLowerCase().contains("degree")) {
                Degree = getString(":", lines[i]).trim();
            } else if (lines[i].toLowerCase().contains("trimester")) {
                String trim = getString("ster", lines[i]).trim();
                if (i + 4 < lines.length) {
                    String gpa = between(lines[i + 1], "GPA", "CGPA").trim();
                    String Cgpa = between(lines[i + 1], "CGPA", "Academic").trim();
                    String status = getString("Status", lines[i + 1]).trim();
                    if (gpa.isEmpty() || Cgpa.isEmpty() || status.isEmpty()) {
                        String[] split = lines[i + 1].split(" ", 3);
                        if (split.length > 2) {
                            gpa = split[0];
                            Cgpa = split[1];
                            status = split[2];
                            i++;
                        }
                    }
                    String Hours = between(lines[i + 2], "Hours", "Total Hours").trim();
                    String TotalHours = between(lines[i + 2], "Total Hours", "Total Point").trim();
                    String TotalPoint = getString("Point", lines[i + 2]).trim();
                    if (Hours.isEmpty() || TotalHours.isEmpty() || TotalPoint.isEmpty()) {
                        String[] split = lines[i + 2].split(" ", 3);
                        if (split.length > 2) {
                            Hours = split[0];
                            TotalHours = split[1];
                            TotalPoint = split[2];
                            i++;
                        }
                    }
                    Trimester trimester = new Trimester(trim, gpa, Cgpa, status, Hours, TotalHours, TotalPoint);
                    boolean breakk = false;
                    for (int x = i + 2; x < lines.length; x++) {
                        if (lines[x].toLowerCase().contains("code")) {
                            x++;
                            for (int y = x; y < lines.length; y++) {

                                if (!containCode(lines[y])) {
                                    breakk = true;
                                }
                                if (breakk) {
                                    break;
                                }
                                String code = getCode(lines[y]);
                                String name = getName(lines[y]);
                                String grade = getGrade(lines[y]);
                                trimester.addSubject(code, name, grade);
                                subjects.add(new subjects(code, name, grade));
                            }
                        }
                        if (breakk) {
                            break;
                        }
                    }
                    trimesters.add(trimester);
                }
            }
        }

        if (Name.isEmpty() || Id.isEmpty() || Degree.isEmpty()) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new FragmentCamsys(this)).commit();
            Toast.makeText(context, "Couldn't recognise pdf!", Toast.LENGTH_LONG).show();

        } else {
            SaveTo("CamsysInfo", Name, Id, Degree);
            SaveTo("Profile", Name, Id, Degree);
            SetSubjectsReviews(Id);
            InfoAvail = true;

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new FragmentProfile(loadData("Id"), context, dashBoardActivity)).commit();
            Toast.makeText(context, "Your profile has been updated successfully!", Toast.LENGTH_LONG).show();
        }
    }

    public void SaveTo(String slotName, String Name, String Id, String Degree) {
        updateFromCamsys = true;
        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child(slotName).removeValue();
        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child(slotName).child("Name").setValue(Name);
        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child(slotName).child("Id").setValue(Id);
        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child(slotName).child("Degree").setValue(Degree);
        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child(slotName).child("UpdatedFrom").setValue("Camsys");
        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child(slotName).child("Trimesters").setValue(trimesters);
    }

    public void SetSubjectsReviews(final String Id) {
        FirebaseDatabase.getInstance().getReference().child("Subjects").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (subjects subjects : subjects) {
                    if (snapshot.child(subjects.getCode()).exists()) {
                        FirebaseDatabase.getInstance().getReference().child("Subjects").child(subjects.getCode()).child("Grades").child(Id).setValue(subjects.getGrade());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public boolean containCode(String subject) {
        Pattern p = Pattern.compile("^([a-zA-Z]{3}[0-9]{4})");
        Matcher n = p.matcher(subject);
        return n.find();
    }

    public String getCode(String subject) {
        Pattern p = Pattern.compile("^([a-zA-Z]{3}[0-9]{4})");
        Matcher n = p.matcher(subject);
        if (n.find()) {
            return n.group(1); // Prints 123456
        } else {
            return "";
        }
    }

    public String getName(String subject) {
        String[] arr = subject.split(" ", -1);
        StringBuilder name = new StringBuilder();
        if (arr.length > 0) {
            arr[0] = "";
            arr[arr.length - 1] = "";
            for (String word : arr) {
                name.append(word);
            }
        }
        return name.toString();
    }

    public String getGrade(String subject) {
        String[] arr = subject.split(" ", -1);
        if (arr.length > 2) {
            return arr[arr.length - 1];
        } else {
            return "";
        }
    }

    public String between(String value, String a, String b) {
        // Return a substring between the two strings.
        int posA = value.indexOf(a);
        if (posA == -1) {
            return "";
        }
        int posB = value.lastIndexOf(b);
        if (posB == -1) {
            return "";
        }
        int adjustedPosA = posA + a.length();
        if (adjustedPosA >= posB) {
            return "";
        }
        return value.substring(adjustedPosA, posB);
    }

    public String getString(String sep, String line) {
        String[] split = line.split(sep, 2);
        if (split.length > 1) {
            return split[1];
        } else {
            return "";
        }
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public boolean verifyStoragePermissions(Activity activity) {
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

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.profile:
                            saveData("0", "Selection");
                            if (InfoAvail) {
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                        fragmentProfile).commit();
                            } else {
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                        new FragmentCamsys(dashBoardActivity)).commit();
                            }
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
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
            return;
        }
        if ((fragmentIndex == 1 || fragmentIndex == 0) && InfoAvail) {
            bottomNav.setSelectedItemId(R.id.profile);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    fragmentProfile).commit();
        } else if (fragmentIndex == 4) {
            bottomNav.setSelectedItemId(R.id.profile);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new FragmentCamsys(this)).commit();
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