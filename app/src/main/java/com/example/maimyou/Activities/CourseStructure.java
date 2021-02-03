package com.example.maimyou.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maimyou.Adapters.AdapterDisplayCourse;
import com.example.maimyou.Classes.ActionListener;
import com.example.maimyou.Classes.DisplayCourse;
import com.example.maimyou.Classes.FileUtils;
import com.example.maimyou.Libraries.PdfBoxFinder;
import com.example.maimyou.R;
import com.example.maimyou.RecycleViewMaterials.Child;
import com.example.maimyou.RecycleViewMaterials.ChildAdapter;
import com.example.maimyou.RecycleViewMaterials.Parent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.awt.geom.Rectangle2D;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.text.PDFTextStripperByArea;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.maimyou.Activities.DashBoardActivity.Intake;
import static com.example.maimyou.Activities.DashBoardActivity.actionListener;

public class CourseStructure extends AppCompatActivity {

    //Views
    RelativeLayout RelEE, RelCE, RelTE, RelEL, RelNA;
    RecyclerView RecEE, RecCE, RecTE, RecEL, RecNA;
    ImageView ArrEE, ArrCE, ArrTE, ArrEL, ArrNA;
    ListView CourseStructureList;
    ProgressBar progressBar;
    LinearLayout title;
    TextView Title;
//    View temp;

    //Vars-
    Context context = this;
    String FileName = "", path = "";
    CourseStructure courseStructure = this;
    Boolean ExpandEE = false, ExpandCE = false, ExpandTE = false, ExpandEL = false, ExpandNA = false, busy = false;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE;

    static {
        PERMISSIONS_STORAGE = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void Menu(View view) {
        PopupMenu popup = new PopupMenu(CourseStructure.this, view);
        popup.getMenuInflater().inflate(R.menu.course_structure_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            if (item.getTitle().toString().toLowerCase().contains("download")) {
                openCustomTab();
            } else if (item.getTitle().toString().toLowerCase().contains("upload course structure")) {
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
            } else if (item.getTitle().toString().toLowerCase().contains("upload syllabus")) {
                if (!busy) {
                    if (verifyStoragePermissions(this)) {
                        Intent intent = new Intent();
                        intent.setType("application/pdf");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        String[] mimetypes = {"application/pdf"};
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                        startActivityForResult(Intent.createChooser(intent, "Choose Pdf"), 2);
                    }
                } else {
                    Toast.makeText(this, "loading", Toast.LENGTH_SHORT).show();
                }
            } else if (item.getTitle().toString().toLowerCase().contains("help")) {
                Toast.makeText(context, "Help", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
        popup.show();
    }

    public void EE(View view) {
        ExpandEE = ExpandView(ArrEE, RelEE, ExpandEE);
    }

    public void CE(View view) {
        ExpandCE = ExpandView(ArrCE, RelCE, ExpandCE);
    }

    public void TE(View view) {
        ExpandTE = ExpandView(ArrTE, RelTE, ExpandTE);
    }

    public void EL(View view) {
        ExpandEL = ExpandView(ArrEL, RelEL, ExpandEL);
    }

    public void NA(View view) {
        ExpandNA = ExpandView(ArrNA, RelNA, ExpandNA);
    }

    public void back(View view) {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_structure);

        ArrEE = findViewById(R.id.ArrEE);
        RelEE = findViewById(R.id.RelEE);
        RecEE = findViewById(R.id.RecEE);
        InflateRec(RecEE, "ee");

        ArrCE = findViewById(R.id.ArrCE);
        RelCE = findViewById(R.id.RelCE);
        RecCE = findViewById(R.id.RecCE);
        InflateRec(RecCE, "ce");

        ArrTE = findViewById(R.id.ArrTE);
        RelTE = findViewById(R.id.RelTE);
        RecTE = findViewById(R.id.RecTE);
        InflateRec(RecTE, "te");

        ArrEL = findViewById(R.id.ArrEL);
        RelEL = findViewById(R.id.RelEL);
        RecEL = findViewById(R.id.RecEL);
        InflateRec(RecEL, "le");

        ArrNA = findViewById(R.id.ArrNA);
        RelNA = findViewById(R.id.RelNA);
        RecNA = findViewById(R.id.RecNA);
        InflateRec(RecNA, "na");

        actionListener.setOnActionPerformed(() -> viewCourse(Intake));

        CourseStructureList = findViewById(R.id.CourseStructureList);
        progressBar = findViewById(R.id.progressBar);
        title = findViewById(R.id.title);
        Title = findViewById(R.id.Title);
    }

    public void InflateRec(RecyclerView recyclerView, String Major) {
        FirebaseDatabase.getInstance().getReference().child("UNDERGRADUATE PROGRAMMES").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ArrayList<Child> ChildTrim1 = new ArrayList<>();
                ArrayList<Child> ChildTrim2 = new ArrayList<>();
                ArrayList<Child> ChildTrim3 = new ArrayList<>();
                ArrayList<Parent> parent = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child.getKey() != null) {
                        if (child.getKey().toLowerCase().contains(Major)) {
                            int trim = getTrim(child.getKey().toLowerCase());
                            if (trim == 1) {
                                ChildTrim1.add(new Child(child.getKey()));
                            } else if (trim == 2) {
                                ChildTrim2.add(new Child(child.getKey()));
                            } else if (trim == 3) {
                                ChildTrim3.add(new Child(child.getKey()));
                            }
                        }
                    }
                }
                if (ChildTrim1.size() > 0) {
                    parent.add(new Parent("Trimester 1 (june)", ChildTrim1));
                }
                if (ChildTrim2.size() > 0) {
                    parent.add(new Parent("Trimester 2 (october-november)", ChildTrim2));
                }
                if (ChildTrim3.size() > 0) {
                    parent.add(new Parent("Trimester 3 (february-march)", ChildTrim3));
                }
                if (parent.size() > 0) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(new ChildAdapter(parent));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public int getTrim(String Course) {
        if (Course.contains("jun")) {
            return 1;
        } else if (Course.contains("oct") || Course.contains("nov")) {
            return 2;
        } else if (Course.contains("feb") || Course.contains("mar")) {
            return 3;
        }
        return 1;
    }

    public void viewCourse(String str) {
        Title.setText(str);
        FirebaseDatabase.getInstance().getReference().child("UNDERGRADUATE PROGRAMMES").child(str).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Trimesters").exists()) {
                    title.setVisibility(View.VISIBLE);
                    ArrayList<DisplayCourse> displayCourses = new ArrayList<>();
                    int firstTrim = getTrim(str);
                    for (DataSnapshot trimester : snapshot.child("Trimesters").getChildren()) {
                        displayCourses.add(new DisplayCourse(getTitle(firstTrim), 0));
                        firstTrim++;
                        for (DataSnapshot subject : trimester.getChildren()) {
                            if (subject.child("Elective").exists() && subject.child("PreRequisite").exists() && subject.child("SubjectHours").exists() && subject.child("SubjectName").exists()) {
                                displayCourses.add(new DisplayCourse("A", subject.getKey(), Objects.requireNonNull(subject.child("SubjectName").getValue()).toString(), Objects.requireNonNull(subject.child("SubjectHours").getValue()).toString(), Objects.requireNonNull(subject.child("PreRequisite").getValue()).toString()));
                            }
                        }
                        if (trimester.child("TotalHours").exists()) {
                            displayCourses.add(new DisplayCourse(Objects.requireNonNull(trimester.child("TotalHours").getValue()).toString()));
                        }
                    }
                    displayCourses.add(new DisplayCourse());
                    displayCourses.add(new DisplayCourse());
                    AdapterDisplayCourse adapter = new AdapterDisplayCourse(context, R.layout.display_course, displayCourses);
                    adapter.setCourseStructure(courseStructure);
                    CourseStructureList.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public String getTitle(int trimInt) {
        while (trimInt > 3) {
            trimInt -= 3;
        }
        return "Trimester " + trimInt;
    }

    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public boolean ExpandView(ImageView imageView, RelativeLayout relativeLayout, boolean expanded) {
        if (!expanded) {
            expand(relativeLayout, dpToPx(50), 300);
            rotate(imageView, true);
            return true;
        } else {
            slideView(relativeLayout, relativeLayout.getHeight(), dpToPx(50), 300);
            rotate(imageView, false);
            return false;
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

    public void expand(final View view, int startHeight, int duration) {
        view.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = view.getMeasuredHeight();

        view.getLayoutParams().height = 0;
        view.setVisibility(View.VISIBLE);

        ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredHeight(), targetHeight);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setDuration(duration);
        anim.addUpdateListener(animation -> {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = (int) ((targetHeight - startHeight) * animation.getAnimatedFraction()) + startHeight;
            view.setLayoutParams(layoutParams);
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

    public void slideView(final View view, int currentHeight, int newHeight, long duration) {

        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(currentHeight, newHeight)
                .setDuration(duration);

        slideAnimator.addUpdateListener(animation1 -> {
            view.getLayoutParams().height = (Integer) animation1.getAnimatedValue();
            view.requestLayout();
        });

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.play(slideAnimator);
        animationSet.start();
        animationSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {


            }
        });
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

    void openCustomTab() {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();

        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        builder.addDefaultShareMenuItem();
        builder.addDefaultShareMenuItem();
        builder.setShowTitle(true);
        builder.setStartAnimations(this, R.anim.load_up_anim, R.anim.stable);
        builder.setExitAnimations(this, R.anim.load_down_anim, R.anim.stable);

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse("http://foe.mmu.edu.my/v3/main/undergrad/previous_structure.html"));
    }

    public boolean verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

                progressBar.setVisibility(View.VISIBLE);
                busy = true;
                path = FileUtils.getPath(context, data.getData());
                FileName = "";
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
                Toast.makeText(getApplicationContext(), "It will take a while to scan " + FileName + ".\n please be patient!", Toast.LENGTH_LONG).show();
                FileName = FileName.substring(0, FileName.length() - 1);
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
                                String codeIndex = "", SubjectNameIndex = "", PreIndex = "";
                                boolean elective = false;
                                int i = 0;
                                for (String name : names) {
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
                                        if (checkHours(SubjectNameIndex, stripperByArea.getTextForRegion(name).replaceAll("\n", ""), name.substring(1), PreIndex)) {
                                            String SubjectCode = stripperByArea.getTextForRegion(name.charAt(0) + codeIndex).replaceAll("\n", "").replaceAll("/", " ");

                                            Pattern p = Pattern.compile("([a-zA-Z]{3}[0-9]{4})");
                                            Matcher n = p.matcher(SubjectCode);
                                            ArrayList<String> codes = new ArrayList<>();
                                            while (n.find()) {
                                                codes.add(n.group(1)); // Prints 123456
                                            }

                                            String SubjectName = stripperByArea.getTextForRegion(name.charAt(0) + SubjectNameIndex).replaceAll("\n", "");
                                            String SubjectHours = stripperByArea.getTextForRegion(name).replaceAll("\n", "");
                                            String PreRequisite = stripperByArea.getTextForRegion(name.charAt(0) + PreIndex).replaceAll("\n", "");
                                            String trimester = getTrimester(name.substring(1), SubjectNameIndex);
                                            if (!SubjectName.toLowerCase().contains("total")) {
                                                if (!SubjectCode.isEmpty()) {
                                                    if (codes.size() > 1) {
                                                        FirebaseDatabase.getInstance().getReference().child("UNDERGRADUATE PROGRAMMES").child(FileName).child("Trimesters").child(trimester).child(codes.get(0).substring(0, 3)).child("SubjectName").setValue(SubjectName);
                                                        FirebaseDatabase.getInstance().getReference().child("UNDERGRADUATE PROGRAMMES").child(FileName).child("Trimesters").child(trimester).child(codes.get(0).substring(0, 3)).child("SubjectHours").setValue(SubjectHours);
                                                        FirebaseDatabase.getInstance().getReference().child("UNDERGRADUATE PROGRAMMES").child(FileName).child("Trimesters").child(trimester).child(codes.get(0).substring(0, 3)).child("Elective").setValue(elective);
                                                        FirebaseDatabase.getInstance().getReference().child("UNDERGRADUATE PROGRAMMES").child(FileName).child("Trimesters").child(trimester).child(codes.get(0).substring(0, 3)).child("PreRequisite").setValue(PreRequisite);
                                                    } else if (codes.size() > 0) {
                                                        FirebaseDatabase.getInstance().getReference().child("UNDERGRADUATE PROGRAMMES").child(FileName).child("Trimesters").child(trimester).child(codes.get(0)).child("SubjectName").setValue(SubjectName);
                                                        FirebaseDatabase.getInstance().getReference().child("UNDERGRADUATE PROGRAMMES").child(FileName).child("Trimesters").child(trimester).child(codes.get(0)).child("SubjectHours").setValue(SubjectHours);
                                                        FirebaseDatabase.getInstance().getReference().child("UNDERGRADUATE PROGRAMMES").child(FileName).child("Trimesters").child(trimester).child(codes.get(0)).child("Elective").setValue(elective);
                                                        FirebaseDatabase.getInstance().getReference().child("UNDERGRADUATE PROGRAMMES").child(FileName).child("Trimesters").child(trimester).child(codes.get(0)).child("PreRequisite").setValue(PreRequisite);
                                                    } else {
                                                        FirebaseDatabase.getInstance().getReference().child("UNDERGRADUATE PROGRAMMES").child(FileName).child("Trimesters").child(trimester).child(SubjectCode).child("SubjectName").setValue(SubjectName);
                                                        FirebaseDatabase.getInstance().getReference().child("UNDERGRADUATE PROGRAMMES").child(FileName).child("Trimesters").child(trimester).child(SubjectCode).child("SubjectHours").setValue(SubjectHours);
                                                        FirebaseDatabase.getInstance().getReference().child("UNDERGRADUATE PROGRAMMES").child(FileName).child("Trimesters").child(trimester).child(SubjectCode).child("Elective").setValue(elective);
                                                        FirebaseDatabase.getInstance().getReference().child("UNDERGRADUATE PROGRAMMES").child(FileName).child("Trimesters").child(trimester).child(SubjectCode).child("PreRequisite").setValue(PreRequisite);
                                                    }
//                                                    for (String code : codes) {
//                                                        FirebaseDatabase.getInstance().getReference().child("Subjects").child(code).child("SubjectName").setValue(SubjectName);
//                                                        FirebaseDatabase.getInstance().getReference().child("Subjects").child(code).child("SubjectHours").setValue(SubjectHours);
//                                                        FirebaseDatabase.getInstance().getReference().child("Subjects").child(code).child("Elective").child(FileName).setValue(elective);
//                                                        FirebaseDatabase.getInstance().getReference().child("Subjects").child(code).child("PreRequisite").child(FileName).setValue(PreRequisite);
//                                                        FirebaseDatabase.getInstance().getReference().child("Subjects").child(code).child("Major").setValue(FileName.substring(0, 2));
//                                                    }
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
                            }
                            progressBar.post(() -> {
                                progressBar.setVisibility(View.GONE);
                                busy = false;
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
            } else if (requestCode == 2 && resultCode == RESULT_OK && data != null) {

                progressBar.setVisibility(View.VISIBLE);
                busy = true;
                path = FileUtils.getPath(context, data.getData());
//                Uri uri = data.getData();
                Toast.makeText(getApplicationContext(), "It will take a minute to scan the syllabus.\n please be patient!", Toast.LENGTH_LONG).show();


                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try (PDDocument document = PDDocument.load(new File(path))) {
                            String CodeIndex = "", SubjectNameIndex = "", HoursIndex = "", Category = "";
                            boolean link = false;
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

                                for (String name : names) {
                                    if (link) {
                                        if (name.compareTo(name.charAt(0) + CodeIndex) == 0 && !stripperByArea.getTextForRegion(name.charAt(0) + CodeIndex).replaceAll("\n", "").isEmpty() && !stripperByArea.getTextForRegion(name.charAt(0) + SubjectNameIndex).replaceAll("\n", "").isEmpty()) {
                                            String Code = stripperByArea.getTextForRegion(name.charAt(0) + CodeIndex).replaceAll("\n", "");
                                            String Link = stripperByArea.getTextForRegion(name.charAt(0) + SubjectNameIndex).replaceAll("\n", "");
                                            FirebaseDatabase.getInstance().getReference().child("Subjects").child(Code).child("SubjectLink").setValue(Link);
                                        }
                                    } else {
                                        if (!CodeIndex.isEmpty() && !SubjectNameIndex.isEmpty() && !HoursIndex.isEmpty() && name.compareTo(name.charAt(0) + CodeIndex) == 0) {
                                            if (stripperByArea.getTextForRegion(name.charAt(0) + SubjectNameIndex).toLowerCase().contains("link")) {
                                                link = true;
                                            } else if (stripperByArea.getTextForRegion(name.charAt(0) + CodeIndex).replaceAll("\n", "").isEmpty() && !stripperByArea.getTextForRegion(name.charAt(0) + SubjectNameIndex).replaceAll("\n", "").isEmpty() && stripperByArea.getTextForRegion(name.charAt(0) + HoursIndex).replaceAll("\n", "").isEmpty()) {
                                                Category = stripperByArea.getTextForRegion(name.charAt(0) + SubjectNameIndex).replaceAll("\n", "");
                                            } else {
                                                String Code = stripperByArea.getTextForRegion(name.charAt(0) + CodeIndex).replaceAll("\n", "");
                                                String Name = stripperByArea.getTextForRegion(name.charAt(0) + SubjectNameIndex).replaceAll("\n", "");
                                                String Hour = stripperByArea.getTextForRegion(name.charAt(0) + HoursIndex).replaceAll("\n", "");
//                                                System.out.println(Name);
                                                if (!Code.isEmpty() && !Name.isEmpty() && !Hour.isEmpty() && !Category.isEmpty()) {
                                                    FirebaseDatabase.getInstance().getReference().child("Subjects").child(Code).child("SubjectName").setValue(Name);
                                                    FirebaseDatabase.getInstance().getReference().child("Subjects").child(Code).child("SubjectHours").setValue(Hour);
                                                    FirebaseDatabase.getInstance().getReference().child("Subjects").child(Code).child("Category").setValue(Category);
                                                }
                                            }
                                        } else if (stripperByArea.getTextForRegion(name).toLowerCase().contains("code") && CodeIndex.isEmpty()) {
                                            CodeIndex = name.substring(1);
                                        } else if (stripperByArea.getTextForRegion(name).toLowerCase().contains("subject") && SubjectNameIndex.isEmpty()) {
                                            SubjectNameIndex = name.substring(1);
                                        } else if (stripperByArea.getTextForRegion(name).toLowerCase().contains("credit") && HoursIndex.isEmpty()) {
                                            HoursIndex = name.substring(1);
                                        }
//                                        System.out.println("Category index:  " + Category);
                                    }
                                }
                            }
                            progressBar.post(() -> {
                                progressBar.setVisibility(View.GONE);
                                busy = false;
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
            } else {
                Toast.makeText(getApplicationContext(), "Please select a file.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Your device is too old to do the task", Toast.LENGTH_SHORT).show();
        }
    }

    public RectF fillBounds(Rectangle2D rect) {
        RectF bounds = new RectF();
        bounds.left = (float) rect.getMinX();
        bounds.right = (float) rect.getMaxX();
        bounds.top = (float) rect.getMinY();
        bounds.bottom = (float) rect.getMaxY();
        return bounds;
    }

    public String getPos(String subIn, int trim) {
        try {
            return Integer.toString(Integer.parseInt(subIn) + trim);
        } catch (Exception ignored) {
            return "";
        }
    }

    public boolean checkHours(String firstIndex, String hours, String hoursIndex, String secondIndex) {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}