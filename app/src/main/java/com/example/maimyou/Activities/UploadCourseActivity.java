package com.example.maimyou.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maimyou.Adapters.AdapterTrimesterCourse;
import com.example.maimyou.Classes.FileUtils;
import com.example.maimyou.Libraries.PdfBoxFinder;
import com.example.maimyou.R;
import com.example.maimyou.Classes.Trimester;
import com.example.maimyou.Classes.TrimesterCourse;
import com.example.maimyou.Adapters.simpleAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.awt.geom.Rectangle2D;
import com.itextpdf.text.pdf.parser.RenderFilter;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.text.PDFTextStripperByArea;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.example.maimyou.Activities.RegisterActivity.SHARED_PREFS;

public class UploadCourseActivity extends AppCompatActivity {
    UploadCourseActivity uploadCourseActivity = this;
    TextView text;
    TrimesterCourse trimesterCourse = new TrimesterCourse();
    Context context = this;
    ImageView imageSelectDate;
    ArrayList<Trimester> trimesters;
    ProgressBar progressBar;
    FrameLayout frame;
    ListView CourseStructureList, Dates;
    TextView chooseDateText;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE;

    static {
        PERMISSIONS_STORAGE = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
    }

    public static class Touch {
        public boolean setIstouched = false;

        public Touch(boolean setIstouched) {
            this.setIstouched = setIstouched;
        }

        public boolean isSetIstouched() {
            return setIstouched;
        }
    }

    public static Touch touchClass = new Touch(false);

    public void download(View view) {
        openCustomTab("http://foe.mmu.edu.my/v3/main/undergrad/previous_structure.html");
    }

    public void popup(View view) {
//        if (!popupSelectDates) {
//            imageSelectDate.setImageResource(R.drawable.arrow_up_float);
//            exit.setVisibility(View.VISIBLE);
//            int size = ToPrint.size();
//            if (size > 5) {
//                size = 5;
//            }
//            int height = size * dpToPx(41);
//            slideView(FrameLayout, FrameLayout.getLayoutParams().height, height, 300L);
////            FrameLayout.setVisibility(View.VISIBLE);
////            FrameLayout.animate()
////                    .alpha(1.0f)
////                    .setDuration(300)
////                    .setListener(new AnimatorListenerAdapter() {
////                @Override
////                public void onAnimationEnd(Animator animation) {
////                    super.onAnimationEnd(animation);
////                    FrameLayout.setVisibility(View.VISIBLE);
////                }
////            });
//            popupSelectDates = true;
//        } else {
//            imageSelectDate.setImageResource(R.drawable.arrow_down_float);
//            slideView(FrameLayout, FrameLayout.getLayoutParams().height, 0, 300L);
//            exit.setVisibility(View.GONE);
//
////            FrameLayout.animate()
////                    .alpha(0.0f)
////                    .setDuration(300)
////                    .setListener(new AnimatorListenerAdapter() {
////                        @Override
////                        public void onAnimationEnd(Animator animation) {
////                            super.onAnimationEnd(animation);
////                            FrameLayout.setVisibility(View.GONE);
////                        }
////                    });
//            popupSelectDates = false;
//        }
    }

    public void back(View view) {
        onBackPressed();
    }

    public void electronics(View view) {
        electronics.setImageResource(R.drawable.electronics_c);
        Computer.setImageResource(R.drawable.computer);
        tele.setImageResource(R.drawable.tele);
        electrical.setImageResource(R.drawable.electrical);
        nano.setImageResource(R.drawable.nano);
        getCourse("el");
    }

    public void Computer(View view) {
        electronics.setImageResource(R.drawable.electronics);
        Computer.setImageResource(R.drawable.computer_c);
        tele.setImageResource(R.drawable.tele);
        electrical.setImageResource(R.drawable.electrical);
        nano.setImageResource(R.drawable.nano);
        getCourse("ce");
    }

    public void electrical(View view) {
        electronics.setImageResource(R.drawable.electronics);
        Computer.setImageResource(R.drawable.computer);
        tele.setImageResource(R.drawable.tele);
        electrical.setImageResource(R.drawable.electrical_c);
        nano.setImageResource(R.drawable.nano);
        getCourse("el");
    }

    public void tele(View view) {
        electronics.setImageResource(R.drawable.electronics);
        Computer.setImageResource(R.drawable.computer);
        tele.setImageResource(R.drawable.tele_c);
        electrical.setImageResource(R.drawable.electrical);
        nano.setImageResource(R.drawable.nano);
        getCourse("te");
    }

    public void nano(View view) {
        electronics.setImageResource(R.drawable.electronics);
        Computer.setImageResource(R.drawable.computer);
        tele.setImageResource(R.drawable.tele);
        electrical.setImageResource(R.drawable.electrical);
        nano.setImageResource(R.drawable.nano_c);
        getCourse("nano");
    }

    public void Upload(View view) {
        if (!busy) {
            if (verifyStoragePermissions(this)) {
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                String[] mimetypes = {"application/pdf"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                startActivityForResult(Intent.createChooser(intent, "Choose Pdf"), 1);
            }
        } else {
            Toast.makeText(this, "loading", Toast.LENGTH_SHORT).show();
        }
    }

    public void dragged(int margin) {
        if (draggerOpened) {
            if (margin > dpToPx(160)) {
                slideView(linearLayout, dpToPx(160), 0, 300);
            } else if (dpToPx(160) >= margin && margin > 0) {
                slideView(linearLayout, margin, 0, 300);
            } else {
                slideView(linearLayout, 0, 0, 300);
            }
            close();
        } else {
            if (margin > dpToPx(160)) {
                slideView(linearLayout, dpToPx(160), dpToPx(160), 300);
            } else if (dpToPx(160) >= margin && margin > 0) {
                slideView(linearLayout, margin, dpToPx(160), 300);
            } else {
                slideView(linearLayout, 0, dpToPx(160), 300);
            }
            open();
        }
    }

    public void open() {
        rotate(dragger, true);
//        dragger.setImageResource(R.drawable.ic_chevron_left_black_24dp);
        draggerOpened = true;
    }

    public void close() {
        rotate(dragger, false);
//        dragger.setImageResource(R.drawable.ic_chevron_right_black_24dp);
        draggerOpened = false;
    }

    int height, width;
    LinearLayout linearLayout;
    ImageView dragger, electronics, Computer, tele, electrical, nano;
    boolean draggerOpened = false;
    private Rect rect;    // Variable rect to hold the bounds of the view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_course);

        TypedValue tv = new TypedValue();
        LinearLayout toolbar = findViewById(R.id.select);
        LinearLayout dates = findViewById(R.id.selectDatesArrow);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) toolbar.getLayoutParams();
        LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) dates.getLayoutParams();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            params.height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics()) + dpToPx(4);
            params2.height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        dates.setLayoutParams(params2);
        toolbar.setLayoutParams(params);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        imageSelectDate = findViewById(R.id.imageSelectDate);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        linearLayout = findViewById(R.id.sec);
        dragger = findViewById(R.id.dragger);
        chooseDateText = findViewById(R.id.chooseDateText);
        electronics = findViewById(R.id.electronics);
        Computer = findViewById(R.id.Computer);
        tele = findViewById(R.id.tele);
        frame = findViewById(R.id.fram);
        electrical = findViewById(R.id.electrical);
        nano = findViewById(R.id.nano);
        progressBar = findViewById(R.id.progressBar);
        Dates = findViewById(R.id.Dates);
        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progg));
        CourseStructureList = findViewById(R.id.CourseStructureList);
        final View.OnTouchListener touch = new View.OnTouchListener() {

            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                final RelativeLayout.LayoutParams par = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
                int margin = (int) event.getRawX();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_DOWN: {
                        if (0 < margin && margin < dpToPx(160)) {
                            touchClass.setIstouched = true;
                            par.width = margin;
                            linearLayout.setLayoutParams(par);
                        }
                        return true;
                    }
                    case MotionEvent.ACTION_UP: {
                        touchClass.setIstouched = true;
                        dragged(margin);
                        return true;
                    }
                }
                return false;
            }
        };

        Dates.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                printGrades((String) Dates.getItemAtPosition(position));
            }
        });
        findViewById(R.id.first).setOnTouchListener(touch);
        findViewById(R.id.list).setOnTouchListener(touch);

        text = findViewById(R.id.text);

        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("CamsysInfo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Trimesters").getValue() != null) {
                    trimesters = new ArrayList<>();
                    Iterable<DataSnapshot> children = snapshot.child("Trimesters").getChildren();
                    for (DataSnapshot child : children) {
                        if (child.getValue() != null) {
                            trimesters.add(getTrim(child));
                        }
                    }
                    if (trimesters.size() > 0) {
                        grades = true;
                        printYourMajor();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void printYourMajor() {
        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("CamsysInfo").child("Degree").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null && trimesters.size() > 0) {
                    String major = getMajor(snapshot.getValue().toString()).trim().toLowerCase();
                    String year = between(trimesters.get(0).semesterName, "-", "/").trim().toLowerCase();
                    String trim = trimesters.get(0).semesterName.trim().substring(0, 1);
                    searchYourCourse(major, year, trim);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public String getMajor(String major) {
        if (isFound("computer", major.toLowerCase())) {
            return "ce";
        } else if (isFound("electronics", major.toLowerCase())) {
            return "ee";
        } else if (isFound("telecommunications", major.toLowerCase())) {
            return "te";
        } else if (isFound("electrical", major.toLowerCase())) {
            return "le";
        } else if (isFound("nanotechnology", major.toLowerCase())) {
            return "nano";
        }
        return "";
    }

    public Trimester getTrim(DataSnapshot dataSnapshot) {
        String semesterName = "", GPA = "", CGPA = "", academicStatus = "", hours = "", totalHours = "", totalPoint = "";
        if (dataSnapshot.child("semesterName").getValue() != null) {
            semesterName = dataSnapshot.child("semesterName").getValue().toString();
        }
        if (dataSnapshot.child("gpa").getValue() != null) {
            GPA = dataSnapshot.child("gpa").getValue().toString();
        }
        if (dataSnapshot.child("cgpa").getValue() != null) {
            CGPA = dataSnapshot.child("cgpa").getValue().toString();
        }
        if (dataSnapshot.child("academicStatus").getValue() != null) {
            academicStatus = dataSnapshot.child("academicStatus").getValue().toString();
        }
        if (dataSnapshot.child("hours").getValue() != null) {
            hours = dataSnapshot.child("hours").getValue().toString();
        }
        if (dataSnapshot.child("totalHours").getValue() != null) {
            totalHours = dataSnapshot.child("totalHours").getValue().toString();
        }
        if (dataSnapshot.child("totalPoint").getValue() != null) {
            totalPoint = dataSnapshot.child("totalPoint").getValue().toString();
        }
        Trimester trimester = new Trimester(semesterName, GPA, CGPA, academicStatus, hours, totalHours, totalPoint);
        Iterable<DataSnapshot> subjectCodes = dataSnapshot.child("subjects").getChildren();
        for (DataSnapshot child : subjectCodes) {
            if (child.child("subjectCodes").getValue() != null && child.child("subjectNames").getValue() != null && child.child("subjectGades").getValue() != null) {
                trimester.addSubject(child.child("subjectCodes").getValue().toString(), child.child("subjectNames").getValue().toString(), child.child("subjectGades").getValue().toString());
            }
        }
        return trimester;
    }

    public void searchYourCourse(final String major, final String year, final String trim) {
        FirebaseDatabase.getInstance().getReference().child("Course Structure").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean found = false;
                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child.getKey() != null) {
                        if (isFound(major, child.getKey().toLowerCase()) && isFound(year, child.getKey().toLowerCase())) {
                            if (trim.compareTo("1") == 0) {
                                if (isFound("june", child.getKey().toLowerCase())) {
                                    printGrades(child.getKey());
                                    found = true;
                                }
                            } else if (trim.compareTo("2") == 0) {
                                if (isFound("october", child.getKey().toLowerCase()) || isFound("november", child.getKey().toLowerCase())) {
                                    printGrades(child.getKey());
                                    found = true;
                                }
                            } else if (trim.compareTo("3") == 0) {
                                if (isFound("february", child.getKey().toLowerCase()) || isFound("march", child.getKey().toLowerCase())) {
                                    printGrades(child.getKey());
                                    found = true;
                                }
                            }
                        }
                    }
                }
                if (!found) {
                    Toast.makeText(getApplicationContext(), "Couldn't find your course structure on firebase", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getCourse(final String major) {
        FirebaseDatabase.getInstance().getReference().child("Course Structure").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> structures = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child.getKey() != null) {
                        if (isFound(major, child.getKey())) {
                            structures.add(child.getKey());
                        }
                    }
                }
                rotate(imageSelectDate, true);

                simpleAdapter adapter = new simpleAdapter(context, R.layout.file_name, structures);
                Dates.setAdapter(adapter);
                expand(frame);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getCourses(View view) {
        if (frame.getHeight() == 0) {
            FirebaseDatabase.getInstance().getReference().child("Course Structure").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<String> structures = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        if (child.getKey() != null) {
                            structures.add(child.getKey());
                        }
                    }
                    rotate(imageSelectDate, true);


                    simpleAdapter adapter = new simpleAdapter(context, R.layout.file_name, structures);
                    Dates.setAdapter(adapter);
                    expand(frame);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            rotate(imageSelectDate, false);
            slideViewHeihgt(frame, frame.getHeight(), 0, 300);
        }
    }

    public void rotate(View view, boolean up) {
        RotateAnimation rotate;
        if (up) {
            rotate = new RotateAnimation(360, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        } else {
            rotate = new RotateAnimation(180, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        }
        rotate.setDuration(300);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setFillAfter(true);
        view.startAnimation(rotate);
    }

    public static void expand(final View view) {
        view.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = view.getMeasuredHeight();

        // Set initial height to 0 and show the view
        view.getLayoutParams().height = 0;
        view.setVisibility(View.VISIBLE);

        ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredHeight(), targetHeight);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setDuration(300);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = (int) (targetHeight * animation.getAnimatedFraction());
                view.setLayoutParams(layoutParams);
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // At the end of animation, set the height to wrap content
                // This fix is for long views that are not shown on screen
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
        });
        anim.start();
    }

    public int setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return 0;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight() + dpToPx(20);
        }
        return totalHeight;
    }

    public RectF fillBounds(Rectangle2D rect) {
        RectF bounds = new RectF();
        bounds.left = (float) rect.getMinX();
        bounds.right = (float) rect.getMaxX();
        bounds.top = (float) rect.getMinY();
        bounds.bottom = (float) rect.getMaxY();
        return bounds;
    }

//    String parsedText = "";
//    int loc = 100;
//    PdfReader reader;
    String path;
//    int n = 0;
//    TrimesterCourse trimesterCourseChild = new TrimesterCourse();
//    TextExtractionStrategy strategy;
//    FontRenderFilter fontFilter;
//    int l;
//    ArrayList<RegionTextRenderFilter> regionTextRenderFilter = new ArrayList<>();
//    Double reg = 20d;
//    Double adder = 30d;
    String FileName = "";
    boolean busy = false;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

                progressBar.setVisibility(View.VISIBLE);

                busy = true;
                path = FileUtils.getPath(context, data.getData());
                FileName = "";
//            new File(data.getData().getPath()).getAbsolutePath();
                Uri uri = data.getData();

                String filename;

                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor == null) {
                    filename = uri.getPath();
                } else {
                    cursor.moveToFirst();
                    int idx = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
                    filename = cursor.getString(idx);
                    cursor.close();
                }

                String[] arr = filename.split("[.]");
                if (arr.length > 0) {
                    FileName = arr[0];
                }
                Toast.makeText(getApplicationContext(), "It will take a minute to scan " + FileName + " please be patient.", Toast.LENGTH_LONG).show();

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try (PDDocument document = PDDocument.load(new File(path))) {
                            for (PDPage page : document.getDocumentCatalog().getPages()) {
                                PdfBoxFinder boxFinder = new PdfBoxFinder(page);
                                boxFinder.processPage(page);

                                PDFTextStripperByArea stripperByArea = new PDFTextStripperByArea();
                                for (Map.Entry<String, Rectangle2D> entry : boxFinder.getRegions().entrySet()) {
                                    stripperByArea.addRegion(entry.getKey(), new RectF(fillBounds(entry.getValue())));
                                }

                                stripperByArea.extractRegions(page);
                                List<String> names = stripperByArea.getRegions();
                                Collections.sort(names, (s1, s2) -> {
                                    int s1int = ((int) s1.charAt(0)) * 100 + Integer.parseInt(s1.substring(1));
                                    int s2int = ((int) s2.charAt(0)) * 100 + Integer.parseInt(s2.substring(1));
                                    return s1int - s2int;
                                });

//                                Collections.sort(names);
//                                names.sort(null);

                                String codeIndex = "", SubjectNameIndex = "", PreIndex = "";
                                boolean elective = false;
                                int i = 0;
                                for (String name : names) {
//                                    System.out.println(name + ": " + stripperByArea.getTextForRegion(name));
                                    if (stripperByArea.getTextForRegion(name).toLowerCase().contains("code") && codeIndex.isEmpty()) {
                                        codeIndex = name.substring(1);
                                    } else if (stripperByArea.getTextForRegion(name).toLowerCase().contains("subject") && SubjectNameIndex.isEmpty()) {
                                        SubjectNameIndex = name.substring(1);
                                    } else if (stripperByArea.getTextForRegion(name).toLowerCase().contains("requisite") && PreIndex.isEmpty()) {
                                        PreIndex = name.substring(1);
                                    } else if (stripperByArea.getTextForRegion(name).toLowerCase().contains("total")) {
                                        if (!codeIndex.isEmpty() && !SubjectNameIndex.isEmpty() && !PreIndex.isEmpty()) {
                                            for (int s = 1; s <= 12; s++) {
                                                FirebaseDatabase.getInstance().getReference().child("UNDERGRADUATE PROGRAMMES").child(FileName).child("Trimesters").child("" + s).child("TotalHours").setValue(stripperByArea.getTextForRegion(name.charAt(0) + getPos(SubjectNameIndex, s)).replaceAll("\n", ""));
                                            }
                                            FirebaseDatabase.getInstance().getReference().child("UNDERGRADUATE PROGRAMMES").child(FileName).child("TotalHours").setValue(stripperByArea.getTextForRegion(name.charAt(0) + PreIndex).replaceAll("\n", ""));
                                        }
                                        break;
                                    }
                                    if (!codeIndex.isEmpty() && !SubjectNameIndex.isEmpty() && !PreIndex.isEmpty()) {
                                        if (elective && name.substring(1).compareTo(codeIndex) == 0 && stripperByArea.getTextForRegion(name).replaceAll("\n", "").isEmpty()) {
                                            elective = false;
                                        }
                                        if (stripperByArea.getTextForRegion(name).toLowerCase().contains("elective") && name.contains(SubjectNameIndex)) {
                                            elective = true;
                                        }
                                        if (checkHours(SubjectNameIndex, stripperByArea.getTextForRegion(name).replaceAll("\n", ""),name.substring(1), PreIndex)) {
                                            String SubjectCode = stripperByArea.getTextForRegion(name.charAt(0) + codeIndex).replaceAll("\n", "").replaceAll("/", " ");
                                            String SubjectName = stripperByArea.getTextForRegion(name.charAt(0) + SubjectNameIndex).replaceAll("\n", "");
                                            String SubjectHours = stripperByArea.getTextForRegion(name).replaceAll("\n", "");
                                            String PreRequisite = stripperByArea.getTextForRegion(name.charAt(0) + PreIndex).replaceAll("\n", "");
                                            String trimester = getTrimester(name.substring(1), SubjectNameIndex);
                                            if (!SubjectName.toLowerCase().contains("total")) {
                                                if (!SubjectCode.isEmpty()) {
                                                    FirebaseDatabase.getInstance().getReference().child("UNDERGRADUATE PROGRAMMES").child(FileName).child("Trimesters").child(trimester).child(SubjectCode).child("SubjectName").setValue(SubjectName);
                                                    FirebaseDatabase.getInstance().getReference().child("UNDERGRADUATE PROGRAMMES").child(FileName).child("Trimesters").child(trimester).child(SubjectCode).child("SubjectHours").setValue(SubjectHours);
                                                    FirebaseDatabase.getInstance().getReference().child("UNDERGRADUATE PROGRAMMES").child(FileName).child("Trimesters").child(trimester).child(SubjectCode).child("Elective").setValue(elective);
                                                    FirebaseDatabase.getInstance().getReference().child("UNDERGRADUATE PROGRAMMES").child(FileName).child("Trimesters").child(trimester).child(SubjectCode).child("PreRequisite").setValue(PreRequisite);

                                                    FirebaseDatabase.getInstance().getReference().child("Subjects").child(SubjectCode).child("SubjectName" ).setValue(SubjectName);
                                                    FirebaseDatabase.getInstance().getReference().child("Subjects").child(SubjectCode).child("SubjectHours").setValue(SubjectHours);
                                                    FirebaseDatabase.getInstance().getReference().child("Subjects").child(SubjectCode).child("Elective"    ).child(FileName).setValue(elective);
                                                    FirebaseDatabase.getInstance().getReference().child("Subjects").child(SubjectCode).child("PreRequisite").child(FileName).setValue(PreRequisite);
                                                    FirebaseDatabase.getInstance().getReference().child("Subjects").child(SubjectCode).child("Major").setValue(FileName.substring(0,2));
                                                } else {
                                                    FirebaseDatabase.getInstance().getReference().child("UNDERGRADUATE PROGRAMMES").child(FileName).child("Trimesters").child(trimester).child("" + i).child("SubjectName").setValue(SubjectName);
                                                    FirebaseDatabase.getInstance().getReference().child("UNDERGRADUATE PROGRAMMES").child(FileName).child("Trimesters").child(trimester).child("" + i).child("SubjectHours").setValue(SubjectHours);
                                                    FirebaseDatabase.getInstance().getReference().child("UNDERGRADUATE PROGRAMMES").child(FileName).child("Trimesters").child(trimester).child("" + i).child("Elective").setValue(elective);
                                                    FirebaseDatabase.getInstance().getReference().child("UNDERGRADUATE PROGRAMMES").child(FileName).child("Trimesters").child(trimester).child("" + i).child("PreRequisite").setValue(PreRequisite);
                                                    i++;
                                                }
                                            }
                                        }
                                    }
                                }

//                                for (int x = 65; x < 250; x++) {
//                                    ArrayList<String> line = new ArrayList<>();
//                                    for (int y = 0; y < 20; y++) {
//                                        try {
//                                            line.add(stripperByArea.getTextForRegion(String.valueOf((char) x) + y).replaceAll("\n", "").trim());
//                                        } catch (Exception ignored) {
//                                            line.add("");
//                                        }
//                                    }
//                                    lines.add(line);
//                                }
//                                if (lines.size() > 0) {
//                                    FirebaseDatabase.getInstance().getReference().child("Course Structure").child(FileName).setValue(lines);
//                                    printGrades(FileName);
//                                }
//                                if (courseSubjects.size() > 0) {
//                                }

//                            trimesterCourse = new TrimesterCourse();
//                            boolean start = true, elective = false;
//                            int firstSem = 0, currSem = 0;
//                            stripperByArea.getTextForRegion(String.valueOf((char) x) + i).replaceAll("\n", "")
//                            (int) stripperByArea.getRegions().get(stripperByArea.getRegions().size() - 1).charAt(0)
//                            for (int x = 0; x < lines.size(); x++) {
//                                String code = "", subjectName = "", hours = "", prerequest = "";
//                                for (int i = 0; i < lines.get(x).size(); i++) {
//                                    try {
//                                        String element = lines.get(x).get(i);
//
//                                        if (isCode(element) && i < 8) {
//                                            code = element;
//                                        } else if (element.trim().length()==1&&hours.isEmpty()) {
//
//                                            hours = element;
//                                            System.out.println("HHHHHHHHHHHHHHHHHHH:" + hours);
//                                            currSem = i;
//                                            if (start) {
//                                                firstSem = i;
//                                                start = false;
//                                            }
//                                        } else if (i < 8 && !element.isEmpty()) {
//                                            subjectName = element;
//                                            if (isFound("elective", subjectName.toLowerCase())) {
//                                                elective = true;
//                                                i = -1;
//                                                x++;
//                                            }
//                                        } else if (i > 8 && !element.isEmpty()) {
//                                            prerequest = element;
//                                        }
//
//
//                                    } catch (Exception ignored) {
//
//                                    }
//                                }
//                                System.out.println(code + "   " + subjectName + "   " + hours + "   " + Integer.toString(currSem - firstSem) + "   " + elective + "   " + prerequest);
//
//                                if (!trimesterCourse.addAll(code, subjectName, hours, Integer.toString(currSem - firstSem), elective, prerequest)) {
//                                    elective = false;
//                                }
//
//                            }
                            }
                            progressBar.post(() -> {
//                                    if (lines.size() > 0) {
//                                        Toast.makeText(getApplicationContext(), "Course structure was uploaded successfully!", Toast.LENGTH_LONG).show();
//                                    } else {
//                                        Toast.makeText(getApplicationContext(), "Error scanning the file!", Toast.LENGTH_LONG).show();
//                                    }
                                progressBar.setVisibility(View.INVISIBLE);
                                busy = false;
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();


//            if (path != null && !path.isEmpty()&&!FileName.isEmpty()) {
////                try {
////                    PdfReader reader = new PdfReader(path);
////
//                reg = 20d;
//                adder = 20d;
//                semName = 0;
//                parsedText = "";
//                fontFilter = new FontRenderFilter();
//                strategy = new LocationTextExtractionStrategy();
//                loc = 250;
//
//                try {
//                    n = new PdfReader(path).getNumberOfPages();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                l = 0;
//                Thread thread = new Thread() {
//                    @Override
//                    public void run() {
//                        try {
//                            trimesterCourse = new TrimesterCourse();
//                            Thread.sleep(100);
//                            String parsedText = "";
//                            semName = 0;
//                            for (loc = 200; loc < 500; loc += adder) {
//                                PdfReader reader = new PdfReader(path);
//                                strategy = new LocationTextExtractionStrategy();
//                                if (semName > 11) {
//                                    loc = 800;
//                                }
//                                for (int i = 0; i < n; i++) {
//                                    parsedText += PdfTextExtractor.getTextFromPage(reader, i + 1, new FilteredTextRenderListener(strategy, new RegionTextRenderFilter(new Rectangle(0, 0, loc, 800)), fontFilter)).trim() + "\n"; //Extracting the content from the different pages
//                                }
//                                reader.close();
//                                scan(parsedText);
//                                parsedText = "";
//                                if (loc > 450) {
//                                    if (semName != 12 && adder > 5) {
//                                        adder -= 1;
//                                        run();
//                                    }
//                                }
//                            }
//                            if(!FileName.isEmpty()) {
//                                FirebaseDatabase.getInstance().getReference().child("Course Structure").child(FileName).setValue(trimesterCourse);
//                            }
//                            busy=false;
//                            printGrades(FileName);
//                            progressBar.setVisibility(View.GONE);
//
//                        } catch (Exception ignored) {
//                            busy=false;
//                            progressBar.setVisibility(View.GONE);
//
//                        }
//                    }
//                };
//                thread.start();
//            }
            } else {
                Toast.makeText(getApplicationContext(), "Please select a file.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Your device is too old to do the task", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getPos(String subIn,int trim){
        try{
            return Integer.toString(Integer.parseInt(subIn)+trim);
        }catch (Exception ignored){
            return "";
        }
    }

    public boolean checkHours(String firstIndex, String hours,String hoursIndex, String secondIndex) {
        try {
            Integer.parseInt(hours.trim());
            return (Integer.parseInt(firstIndex.trim()) < Integer.parseInt(hoursIndex.trim()) && Integer.parseInt(hoursIndex.trim()) < Integer.parseInt(secondIndex.trim()));
        } catch (Exception ignored) {
            return false;
        }
    }

    public String getTrimester(String nameIndex, String hoursIndex) {
        try {
            return Integer.toString(Integer.parseInt(nameIndex) - Integer.parseInt(hoursIndex));
        } catch (Exception ignored) {
            return "";
        }
    }

    class FontRenderFilter extends RenderFilter {
        public boolean allowText(TextRenderInfo renderInfo) {
            return true;
        }
    }

    int semName = 0;
    boolean grades = false;

    public void printGrades(final String fileName) {
        FirebaseDatabase.getInstance().getReference().child("Course Structure").child(fileName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    trimesterCourse = new TrimesterCourse();
                    boolean start = true, elective = false, skip = false;
                    int firstSem = 0, currSem = 0, col;
                    for (DataSnapshot linee : snapshot.getChildren()) {
                        col = 0;
                        String code = "", subjectName = "", hours = "", prerequest = "";
                        for (DataSnapshot colom : linee.getChildren()) {
                            String element = colom.getValue().toString();
                            if (!element.isEmpty()) {
                                if (isCode(element) && code.isEmpty()) {
                                    code = element;
                                } else if (subjectName.isEmpty()) {
                                    subjectName = element;
                                    if (isFound("elective", subjectName.toLowerCase())) {
                                        elective = true;
                                        skip = true;
                                    }
                                } else if (isNumeric(element.trim()) && hours.isEmpty()) {
                                    hours = element;
                                    currSem = col;
                                    if (start) {
                                        firstSem = col;
                                        start = false;
                                    }
                                } else if (col > 8 && prerequest.isEmpty()) {
                                    prerequest = element;
                                }
                            }
                            col++;
                        }
                        if (!skip) {
                            if (!hours.isEmpty() && !subjectName.isEmpty()) {
                                trimesterCourse.addAll(code, subjectName, hours, Integer.toString(currSem - firstSem), elective, prerequest);
                            } else {
                                elective = false;
                            }
                        }
                        skip = false;
                    }
                    chooseDateText.setText(fileName);
                    trimesterCourse.sort();
                    AdapterTrimesterCourse adapter = new AdapterTrimesterCourse(context, R.layout.trimester_course, trimesterCourse.getTrimName());
                    adapter.setTrimesterCourseToPrint(trimesterCourse);
                    adapter.setUploadCourseActivity(uploadCourseActivity);
                    adapter.setTrimesters(trimesters);
                    CourseStructureList.setAdapter(adapter);

                    rotate(imageSelectDate, false);
                    slideViewHeihgt(frame, frame.getHeight(), 0, 300);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public String loadData(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if (sharedPreferences == null) {
            return "";
        }
        return sharedPreferences.getString(name, "");
    }

    public void scan(String st) {
        boolean semester = false, elective = false;
        String[] lines = st.split("\n", -1);
        for (int s = 0; s < lines.length; s++) {
            if (isFound("core", lines[s].toLowerCase())) {
                for (int t = s + 1; t < lines.length; t++) {
                    if (isFound("total", lines[t].toLowerCase())) {
                        break;
                    }

                    if (isFound("elective", lines[t].toLowerCase())) {
                        elective = true;
                        t++;
                    }
                    String[] arr = lines[t].split("\\s+");
                    if (arr.length > 2) {
                        StringBuilder middle = new StringBuilder();
                        String end = arr[arr.length - 1].trim();
                        if (isNumeric(end)) {
                            for (int u = 1; u < arr.length - 1; u++) {
                                middle.append(arr[u]).append(" ");
                            }
                            if (trimesterCourse.addVal(arr[0], middle.toString(), end, Integer.toString(semName), elective)) {
                                semester = true;
                            } else {
                                elective = false;
                            }
                        } else {
                            if (semName > 11) {
                                boolean addValll = false;
                                for (int u = 1; u < arr.length; u++) {
                                    if (addValll) {
                                        middle.append(arr[u]).append(" ");
                                    }
                                    if (isNumeric(arr[u]) && !isFound("credit", arr[u + 1])) {
                                        middle = new StringBuilder();
                                        addValll = true;
                                    }
                                }
                                trimesterCourse.addPreRequest(arr[0], middle.toString());
                            }
                            elective = false;
                        }
                    } else {
                        elective = false;
                    }
                }
                break;
            }
        }
        if (semester) {
            semName++;
        }

//        if(semName>11){
//            loc=300;
//        }
    }

    public boolean isCode(String string) {
        try {
            return isNumeric(string.substring(3, 7));
        } catch (Exception ignored) {
            return false;
        }
    }

    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

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

    public int dpToPx(int dip) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );
        return (int) px;
    }

    public boolean isFound(String p, String hph) {
        return hph.contains(p);
    }

    void openCustomTab(String url) {
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
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }

    public void slideViewHeihgt(final View view, int currentHeight, int newHeight, long duration) {

        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(currentHeight, newHeight)
                .setDuration(duration);

        /* We use an update listener which listens to each tick
         * and manually updates the height of the view  */

        slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation1) {
                Integer value = (Integer) animation1.getAnimatedValue();
                view.getLayoutParams().height = value.intValue();
                view.requestLayout();
            }
        });
        /*  We use an animationSet to play the animation  */

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.play(slideAnimator);
        animationSet.start();
    }

    public void slideView(final View view, int currentWidth, int newWidth, long duration) {

        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(currentWidth, newWidth)
                .setDuration(duration);

        /* We use an update listener which listens to each tick
         * and manually updates the height of the view  */

        slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation1) {
                Integer value = (Integer) animation1.getAnimatedValue();
                view.getLayoutParams().width = value.intValue();
                view.requestLayout();
            }
        });

        /*  We use an animationSet to play the animation  */

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.play(slideAnimator);
        animationSet.start();
        animationSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                touchClass.setIstouched = false;
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        if (busy) {
            Toast.makeText(this, "scanning has been cancelled", Toast.LENGTH_LONG).show();
        }
        overridePendingTransition(R.anim.slide0, R.anim.slide_in_top);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
