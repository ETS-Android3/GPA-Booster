package com.example.maimyou.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.maimyou.Classes.FileUtils;
import com.example.maimyou.Classes.subjects;
import com.example.maimyou.R;
import com.example.maimyou.Classes.Trimester;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;


import java.io.File;
import java.util.ArrayList;

import static com.example.maimyou.Activities.DashBoardActivity.bottomNav;
import static com.example.maimyou.Activities.RegisterActivity.SHARED_PREFS;

public class ScanMarksActivity extends AppCompatActivity {
    private static final String TAG = "TAG";
    Context context = this;
    private Rect rect;    // Variable rect to hold the bounds of the view
    ScrollView scrole;
    ScanMarksActivity scanMarks = this;
    ImageButton camsys;
    ArrayList<Trimester> trimesters = new ArrayList<>();
    ArrayList<subjects> subjects = new ArrayList<>();


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public boolean verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
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


    public void skip(View view) {
        onBackPressed();
    }

    public void Upload(View view) {
        if (verifyStoragePermissions(this)) {
            Intent intent = new Intent();
            intent.setType("application/pdf");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            String[] mimetypes = {"application/pdf"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
            startActivityForResult(Intent.createChooser(intent, "Choose Pdf"), 1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_marks);

        scrole = findViewById(R.id.scrole);
        camsys = findViewById(R.id.CamsysButton);
        camsys.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    camsys.setImageResource(R.drawable.camsys_c);
                    rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    camsys.setImageResource(R.drawable.camsys);
                    if (rect.contains(view.getLeft() + (int) event.getX(), view.getTop() + (int) event.getY())) {
                        openCustomTab();
                    }
                }
                return true;
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrole.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    camsys.setImageResource(R.drawable.camsys);
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

            String path = FileUtils.getPath(context, data.getData());
            String FileName = "";
            if (new File(data.getData().getPath()).getAbsolutePath() != null) {
                Uri uri = data.getData();

                String filename;
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);

                if (cursor == null) filename = uri.getPath();
                else {
                    cursor.moveToFirst();
                    int idx = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
                    filename = cursor.getString(idx);
                    cursor.close();
                }

                String[] arr = filename.split("[.]");
                if (arr.length > 0) {
                    FileName = arr[0];
                }
            }

//            System.out.println("PAAAAAAAAAAAAAAAAAAAAAATTTTTTHHHHHHHHHHHHHHHHH:     "+path);
            if (path != null && !path.isEmpty() && !FileName.isEmpty()) {
                try {
                    StringBuilder parsedText = new StringBuilder();
                    PdfReader reader = new PdfReader(path);
                    int n = reader.getNumberOfPages();

                    for (int i = 0; i < n; i++) {
                        parsedText.append(PdfTextExtractor.getTextFromPage(reader, i + 1).trim()).append("\n"); //Extracting the content from the different pages
                    }
                    reader.close();
                    scan(parsedText.toString());
                    saveData("0", "Selection");
                    bottomNav.setSelectedItemId(R.id.profile);
                    finish();
                } catch (Exception ignored) {
                }

            }
        } else {
            Toast.makeText(getApplicationContext(), "Please select a file.", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void scan(String res) {
        trimesters.clear();
        subjects.clear();
        String Name = "", Id = "", Degree = "";
        String[] lines = res.split("\n", -1);
        for (int i = 0; i < lines.length; i++) {
            if (isFound("id", lines[i].toLowerCase())) {
                Id = getString(":", lines[i]).trim();
            } else if (isFound("name", lines[i].toLowerCase())) {
                Name = getString(":", lines[i]).trim();
            } else if (isFound("degree", lines[i].toLowerCase())) {
                Degree = getString(":", lines[i]).trim();
            } else if (isFound("trimester", lines[i].toLowerCase())) {
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
                        if (isFound("Code", lines[x])) {
                            x++;
                            for (int y = x; y < lines.length; y++) {

                                if (isFound("Trimester", lines[y])) {
                                    breakk = true;
                                }
                                if (lines[y].length() < 8) {
                                    breakk = true;
                                } else if (lines[y].substring(7, 8).compareTo(" ") != 0) {
                                    breakk = true;
                                }
                                if (breakk) {
                                    break;
                                }
                                String code = lines[y].substring(0, 7).trim();
                                String name = lines[y].substring(7, lines[y].length() - 2).trim();
                                String grade = lines[y].substring(lines[y].length() - 2).trim();
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
        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("CamsysInfo").child("Name").setValue(Name);
        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("CamsysInfo").child("Id").setValue(Id);
        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("CamsysInfo").child("Degree").setValue(Degree);
        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("CamsysInfo").child("Trimesters").setValue(trimesters);
        SetSubjectsReviews();
    }

    public void saveData(String data, String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, data);
        editor.apply();
    }

    public void SetSubjectsReviews() {
        FirebaseDatabase.getInstance().getReference().child("Subjects").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (subjects subjects : subjects) {
                    if(snapshot.child(subjects.getCode()).exists()){
                        FirebaseDatabase.getInstance().getReference().child("Subjects").child(subjects.getCode()).child("Grades").child(loadData("Id")).setValue(subjects.getGrade());
                    }else{

                    }

//                    else{
//                        FirebaseDatabase.getInstance().getReference().child("Subjects").child(subjects.getCode()).child("Subject Name").setValue(subjects.getName());
//                        FirebaseDatabase.getInstance().getReference().child("Subjects").child(subjects.getCode()).child("Grades").child(loadData("Id")).setValue(subjects.getGrade());
//                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    void openCustomTab() {
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        builder.addDefaultShareMenuItem();
//        builder.setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left);
//        builder.setExitAnimations(this, android.R.anim.slide_in_left,
//                android.R.anim.slide_out_right);
        builder.addDefaultShareMenuItem();
//
//        builder.setCloseButtonIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_share));
//
//        CustomTabsIntent anotherCustomTab = new CustomTabsIntent.Builder().build();
//
//        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_share);
////        builder.setCloseButtonIcon(icon);
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_file_upload);
//
//        int requestCode = 100;
//        Intent intent = anotherCustomTab.intent;
//        intent.setData(Uri.parse("http://www.journaldev.com/author/anupam"));
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        builder.setActionButton(bitmap, "Android", pendingIntent, true);
        builder.setShowTitle(true);
        builder.setStartAnimations(this, R.anim.load_up_anim, R.anim.stable);
        builder.setExitAnimations(this, R.anim.load_down_anim, R.anim.stable);

        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(this, Uri.parse("https://cms.mmu.edu.my/psc/csprd/EMPLOYEE/HRMS/c/N_SR_STUDENT_RECORDS.N_ON_RSLT_PNL.GBL?PORTALPARAM_PTCNAV=ONLINE_RESULT&amp;EOPP.SCNode=HRMS&amp;EOPP.SCPortal=EMPLOYEE&amp;EOPP.SCName=CO_EMPLOYEE_SELF_SERVICE&amp;EOPP.SCLabel=Self%20Service&amp;EOPP.SCPTfname=CO_EMPLOYEE_SELF_SERVICE&amp;FolderPath=PORTAL_ROOT_OBJECT.CO_EMPLOYEE_SELF_SERVICE.HCCC_ACADEMIC_RECORDS.ONLINE_RESULT&amp;IsFolder=false&amp;PortalActualURL=https%3a%2f%2fcms.mmu.edu.my%2fpsc%2fcsprd%2fEMPLOYEE%2fHRMS%2fc%2fN_SR_STUDENT_RECORDS.N_ON_RSLT_PNL.GBL&amp;PortalContentURL=https%3a%2f%2fcms.mmu.edu.my%2fpsc%2fcsprd%2fEMPLOYEE%2fHRMS%2fc%2fN_SR_STUDENT_RECORDS.N_ON_RSLT_PNL.GBL&amp;PortalContentProvider=HRMS&amp;PortalCRefLabel=Academic%20Achievement&amp;PortalRegistryName=EMPLOYEE&amp;PortalServletURI=https%3a%2f%2fcms.mmu.edu.my%2fpsp%2fcsprd%2f&amp;PortalURI=https%3a%2f%2fcms.mmu.edu.my%2fpsc%2fcsprd%2f&amp;PortalHostNode=HRMS&amp;NoCrumbs=yes&amp;PortalKeyStruct=yes"));
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

    public String loadData(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if (sharedPreferences == null) {
            return "";
        }
        return sharedPreferences.getString(name, "");
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide0, R.anim.slide_in_top);
    }

    public boolean isFound(String p, String hph) {
        return hph.contains(p);
    }
}
