package com.example.maimyou.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.maimyou.Classes.ActionListener;
import com.example.maimyou.Classes.MyJavaScriptInterface;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.apache.commons.lang3.text.WordUtils;
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
    public static boolean SignIn = true;
    public static int fragmentIndex = 0;
    public static BottomNavigationView bottomNav;
    public static String content = "";
    public static int signin = 0;
    public static int LoadRes = 0;
    public static AdvancedWebView ProfileWebView;

    public void DashBack(View view) {
        onBackPressed();
    }

    public void FragEdit(View view) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                fragmentEdit).commit();
    }

    public void setManually() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                fragmentEdit).commit();
    }

    public void empty(View view) {

    }

    public void addPic(View view) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(DashBoardActivity.this);
    }

    public void OpenCamsys(View view) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new FragmentCamsys(this)).commit();
    }

    public void Camsys(View view) {
        bottomNav.setSelectedItemId(R.id.profile);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new FragmentCamsys(dashBoardActivity)).commit();
    }

    public void save(View view) {
        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("ModifiedInfo").child("UpdatedFrom").setValue("Modified").addOnCompleteListener(task -> {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id"));
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ref.child("Profile").setValue(snapshot.child("ModifiedInfo").getValue()).addOnCompleteListener(task -> {
                        if (!loadData("camsysId").isEmpty() && fragmentEdit.subjects.size() > 0) {
                            SetSubjectsReviewsArr(fragmentEdit.subjects, loadData("camsysId"));
                        }
                        fragmentEdit = new FragmentEdit(loadData("Id"), context, dashBoardActivity);
                        fragmentProfile = new FragmentProfile(loadData("Id"), context, dashBoardActivity);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                fragmentProfile).commit();

                        Toast.makeText(context, "Your profile has been updated successfully!", Toast.LENGTH_LONG).show();
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
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
//        overridePendingTransition(R.anim.slide_in_up, R.anim.slide0);
    }

    public void Planner(View view){
        startActivity(new Intent(getApplicationContext(), PlannerActivity.class));
    }

    public void UploadCourse(View view) {
        startActivity(new Intent(getApplicationContext(), CourseStructure.class));
//        overridePendingTransition(R.anim.slide_in_up, R.anim.slide0);
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
//        overridePendingTransition(R.anim.slide_in_up, R.anim.slide0);
    }

    public void logout(View view) {
        saveData("", "Id");
        saveData("", "camsysPassword");
        saveData("", "camsysId");
        if (webView != null) {
            webView.loadUrl("https://cms.mmu.edu.my/psp/csprd/EMPLOYEE/HRMS/?cmd=logout");
        }
        if (ProfileWebView != null) {
            ProfileWebView.loadUrl("https://cms.mmu.edu.my/psp/csprd/EMPLOYEE/HRMS/?cmd=logout");
        }
        FirebaseAuth.getInstance().signOut();//logout
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        finish();
        CustomIntent.customType(this, "right-to-left");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        InfoAvail = getIntent().getBooleanExtra("InfoAvail", true);


//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int height = displayMetrics.heightPixels;
//        int width = displayMetrics.widthPixels;
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        fragmentProfile = new FragmentProfile(loadData("Id"), context, dashBoardActivity);
        fragmentEdit = new FragmentEdit(loadData("Id"), context, dashBoardActivity);
        setWebView();

//        StartLoop();
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
//                                if (!loadData("Auto").isEmpty()) {
                                bottomNav.setSelectedItemId(R.id.profile);
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                        new FragmentCamsys(dashBoardActivity)).commit();
//                                }
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

    @SuppressLint("SetJavaScriptEnabled")
    public void setWebView() {
        ProfileWebView = findViewById(R.id.ProfileWebView);
        ProfileWebView.setListener(this, this);
        ProfileWebView.setMixedContentAllowed(true);
        ProfileWebView.setThirdPartyCookiesEnabled(true);
        ProfileWebView.getSettings().setAllowContentAccess(true);
        ProfileWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        ProfileWebView.getSettings().setJavaScriptEnabled(true);
        ProfileWebView.addJavascriptInterface(new MyJavaScriptInterface(dashBoardActivity), "HTMLOUT");

        ProfileWebView.setWebViewClient(new WebViewClient() {
//                    @Override
//                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            Toast.makeText(context,error.getDescription(),Toast.LENGTH_LONG).show();
//                        }
//                    }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (dashBoardActivity.loadData("Auto").isEmpty()) {
                    if (url.endsWith(".pdf")) {
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                        CookieManager cookieManager = CookieManager.getInstance();
                        String cookie = cookieManager.getCookie("https://cms.mmu.edu.my");     // which is "http://bookboon.com"
                        request.addRequestHeader("Cookie", cookie);
                        request.setNotificationVisibility(1);
                        request.allowScanningByMediaScanner();
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/camsys.pdf");
                        request.setMimeType("application/pdf");
                        DownloadManager dm = (DownloadManager) dashBoardActivity.getSystemService(DOWNLOAD_SERVICE);
                        dm.enqueue(request);
                        new Handler().postDelayed(() -> {
                            dashBoardActivity.scanResFromPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/camsys.pdf");
                        }, 3000);
                    }
                }
                return true;
            }
        });
//        new Handler().postDelayed(() -> {
//            ProfileWebView.loadUrl("https://cms.mmu.edu.my/psp/csprd_1/EMPLOYEE/HRMS/c/N_SR_STUDENT_RECORDS.N_ON_RSLT_PNL.GBL");
        ProfileWebView.loadUrl("https://cms.mmu.edu.my/psc/csprd/EMPLOYEE/HRMS/c/N_SR_STUDENT_RECORDS.N_ON_RSLT_PNL.GBL?PORTALPARAM_PTCNAV=ONLINE_RESULT&amp;EOPP.SCNode=HRMS&amp;EOPP.SCPortal=EMPLOYEE&amp;EOPP.SCName=CO_EMPLOYEE_SELF_SERVICE&amp;EOPP.SCLabel=Self%20Service&amp;EOPP.SCPTfname=CO_EMPLOYEE_SELF_SERVICE&amp;FolderPath=PORTAL_ROOT_OBJECT.CO_EMPLOYEE_SELF_SERVICE.HCCC_ACADEMIC_RECORDS.ONLINE_RESULT&amp;IsFolder=false&amp;PortalActualURL=https%3a%2f%2fcms.mmu.edu.my%2fpsc%2fcsprd%2fEMPLOYEE%2fHRMS%2fc%2fN_SR_STUDENT_RECORDS.N_ON_RSLT_PNL.GBL&amp;PortalContentURL=https%3a%2f%2fcms.mmu.edu.my%2fpsc%2fcsprd%2fEMPLOYEE%2fHRMS%2fc%2fN_SR_STUDENT_RECORDS.N_ON_RSLT_PNL.GBL&amp;PortalContentProvider=HRMS&amp;PortalCRefLabel=Academic%20Achievement&amp;PortalRegistryName=EMPLOYEE&amp;PortalServletURI=https%3a%2f%2fcms.mmu.edu.my%2fpsp%2fcsprd%2f&amp;PortalURI=https%3a%2f%2fcms.mmu.edu.my%2fpsc%2fcsprd%2f&amp;PortalHostNode=HRMS&amp;NoCrumbs=yes&amp;PortalKeyStruct=yes");
//        }, 3000);

        //        ProfileWebView.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);");
    }

    public void openProfile() {
        fragmentEdit = new FragmentEdit(loadData("Id"), context, dashBoardActivity);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                fragmentProfile).commit();
    }

    public void resetFragmentEdit() {
        fragmentEdit = new FragmentEdit(loadData("Id"), context, dashBoardActivity);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1212 && resultCode == RESULT_OK && data != null) {
            String path = UriUtils.getPathFromUri(context, data.getData());

            scanResFromPath(path);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null) {
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    uploadFile(resultUri);
//                cropImageView.setImageUriAsync(resultUri);
//                    Picasso.get().load(resultUri).error(R.drawable.avatar).into(fragmentEdit.profilePictureAdmin);
//                    Picasso.get().load(resultUri).error(R.drawable.avatar).into(fragmentEdit.profilePictureAdmin);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (result != null) {
//                Picasso.get().load(result.getUri()).into(fragmentEdit.profilePictureAdmin);
//
////                profile.setURI(result.getUri());
////                uploadProfilePic(result.getUri());
//            }
        } else {
            Toast.makeText(getApplicationContext(), "Please select the pdf file.", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (webView != null) {

            webView.onActivityResult(requestCode, resultCode, data);
        }

        if (ProfileWebView != null) {

            ProfileWebView.onActivityResult(requestCode, resultCode, data);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(Uri mImageUri) {
        fragmentEdit.progressBar.setVisibility(View.VISIBLE);
        StorageReference fileReference = FirebaseStorage.getInstance().getReference("uploads").child(loadData("Id")
                + ".jpg");
        fileReference.putFile(mImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {

                        Handler handler = new Handler();
                        handler.postDelayed(() -> fragmentEdit.progressBar.setProgress(0), 500);
                        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("ModifiedInfo").child("PersonalImage").setValue(uri.toString());
                        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("CamsysInfo").child("PersonalImage").setValue(uri.toString());
                        Toast.makeText(context, "Upload successful", Toast.LENGTH_LONG).show();
                        fragmentEdit.progressBar.setVisibility(View.GONE);

                    }).addOnFailureListener(e -> {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        fragmentEdit.progressBar.setVisibility(View.GONE);
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    fragmentEdit.progressBar.setVisibility(View.GONE);
                });
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

        if (ProfileWebView != null) {

            ProfileWebView.onResume();
        }
        // ...
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        if (webView != null) {

            webView.onPause();
        }

        if (ProfileWebView != null) {

            ProfileWebView.onPause();
        }
        // ...
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {

            webView.onDestroy();
        }

        if (ProfileWebView != null) {

            ProfileWebView.onDestroy();
        }
        // ...
        super.onDestroy();
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
    }

    @Override
    public void onPageFinished(String url) {
        if (Build.VERSION.SDK_INT >= 19) {

            if (ProfileWebView != null) {
//                ProfileWebView.loadUrl("javascript:window.INTERFACE.processContent(document.getElementsByTagName('body')[0].innerText);");
                ProfileWebView.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);");
            }

            if (webView != null) {
                if (SignIn) {
                    String js1 = "javascript:document.getElementById('userid').value='" + loadData("camsysId") + "';" +
                            "javascript:document.getElementById('pwd').value='" + loadData("camsysPassword") + "';" +
                            "javascript:document.getElementsByName('Submit')[0].click();";
                    webView.evaluateJavascript(js1, s -> {
                    });
                    webView.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);");

                }
            }


//            webView.loadUrl("javascript: (function() {document.getElementById('userid').value= '"+"1161104336"+"';  document.getElementById('pwd').value='"+"5Lq##KTESJ4"+"';  }) ();" );

        }
    }
//
//    public void StartLoop() {
//
//        new Handler().postDelayed(() -> {
//            if (webView != null) {
//
//                String js3 = "javascript:document.getElementsByName('N_REPORT_WRK_BUTTON')[0].click();";
//                webView.evaluateJavascript(js3, s -> {
//                });
//            }
//            StartLoop();
//        }, 10000);
//    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
//        Toast.makeText(context,description,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
//        Toast.makeText(context, "onDownloadRequested", Toast.LENGTH_LONG).show();


//        DownloadManager.Request request = new DownloadManager.Request(
//                Uri.parse(url));
//        request.setMimeType(mimeType);
//        String cookies = CookieManager.getInstance().getCookie(url);
//        request.addRequestHeader("cookie", cookies);
//        request.addRequestHeader("User-Agent", userAgent);
//        request.setDescription("Downloading file...");
//        request.setTitle(URLUtil.guessFileName(url, contentDisposition,
//                mimeType));
//        request.allowScanningByMediaScanner();
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        request.setDestinationInExternalPublicDir(
//                Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(
//                        url, contentDisposition, mimeType));
//        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//        dm.enqueue(request);
//        Toast.makeText(getApplicationContext(), "Downloading File",
//                Toast.LENGTH_LONG).show();

//        DownloadManager.Request request = new DownloadManager.Request(
//                Uri.parse(url));
//        request.setMimeType(mimeType);
//        String cookies = CookieManager.getInstance().getCookie(url);
//        request.addRequestHeader("cookie", cookies);
//        request.addRequestHeader("User-Agent", userAgent);
//        request.setDescription("Downloading File...");
//        request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
//        request.allowScanningByMediaScanner();
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        request.setDestinationInExternalPublicDir(
//                Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(
//                        url, contentDisposition, mimeType));
//        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//        dm.enqueue(request);


//
//        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//        CookieManager cookieManager = CookieManager.getInstance();
//        String cookie = cookieManager.getCookie("https://cms.mmu.edu.my");     // which is "http://bookboon.com"
//        request.addRequestHeader("Cookie", cookie);
//        request.setTitle(suggestedFilename);
//        request.setNotificationVisibility(1);
//        request.allowScanningByMediaScanner();
//        request.setMimeType("application/pdf");
////        Log.e("Extension with ","UpperCase-->"+"\""+fileName.split("\\.")[0]+"."+fileName.split("\\.")[1].toUpperCase()+"\"");
////        downloadId = downloadManager.enqueue(request);
//        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//        dm.enqueue(request);

//        Map<String, String> headers = new HashMap<>();
//        headers.put("Authorization", "Basic" + Base64.encodeToString("1161104336" + ':' + "5Lq##KTESJ4"), Base64.DEFAULT);


//        HashMap<String, String> headers = new HashMap<>();
//        String basicAuthHeader = android.util.Base64.encodeToString(("1161104336" + ":" + "5Lq##KTESJ4").getBytes(), android.util.Base64.NO_WRAP);
//        headers.put("Authorization", "Basic " + basicAuthHeader);
//        webView.loadUrl("http://docs.google.com/viewer?url=" + url + "&embedded=true", headers);


//        DownloadManager.Request request = new DownloadManager.Request( Uri.parse(url));
//        request.allowScanningByMediaScanner();
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "myPDFfile.pdf");
//        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//        dm.enqueue(request);
        Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();


//        openCustomTab(url);
//
//        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//
//        //This three lines will do your work
//
//        CookieManager cookieManager = CookieManager.getInstance();
//        String cookie = cookieManager.getCookie("https://cms.mmu.edu.my");     // which is "http://bookboon.com"
//        request.addRequestHeader("Cookie", cookie);
//        //................................................
//        request.allowScanningByMediaScanner();
//        Environment.getExternalStorageDirectory();
//        getApplicationContext().getFilesDir().getPath(); //which returns the internal app files directory path
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "download");
//        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//        dm.enqueue(request);

//        new DownloadFileFromURL().execute(url);

//
//        try
//        {
//            Intent intentUrl = new Intent(Intent.ACTION_VIEW);
//            intentUrl.setDataAndType(Uri.parse(url), "application/pdf");
//            intentUrl.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            dashBoardActivity.startActivity(intentUrl);
//        }
//        catch (ActivityNotFoundException e)
//        {
//            e.printStackTrace();
//            Toast.makeText(dashBoardActivity, "No PDF Viewer Installed", Toast.LENGTH_LONG).show();
//        }

//        try {
//            System.out.println("opening connection");
//            URL urll = null;
//            urll = new URL(url);
//
//            InputStream in = urll.openStream();
//            FileOutputStream fos = new FileOutputStream(new File("yourFile.pdf"));
//
//            System.out.println("reading from resource and writing to file...");
//            int length = -1;
//            byte[] buffer = new byte[1024];// buffer for portion of data from connection
//            while ((length = in.read(buffer)) > -1) {
//                fos.write(buffer, 0, length);
//            }
//            fos.close();
//            in.close();
//            System.out.println("File downloaded");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if (webView != null) {
//
//            webView.loadUrl( url );
//        }
//        Intent i = new Intent(Intent.ACTION_VIEW);
//        i.setData(Uri.parse(url));
//        startActivity(i);
//        //        readPdfFileVerify(url);
    }

//    void openCustomTab(String url) {
//        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
//
//        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
//        builder.addDefaultShareMenuItem();
//        builder.addDefaultShareMenuItem();
//        builder.setShowTitle(true);
//        builder.setStartAnimations(this, R.anim.load_up_anim, R.anim.stable);
//        builder.setExitAnimations(this, R.anim.load_down_anim, R.anim.stable);
//
//        CustomTabsIntent customTabsIntent = builder.build();
//        customTabsIntent.launchUrl(this, Uri.parse("https://cms.mmu.edu.my/psp/csprd/?cmd=login?&languageCd=ENG&"));
//        customTabsIntent.launchUrl(this, Uri.parse("javascript:document.getElementById('userid').value='1161104336';" +
//                "javascript:document.getElementById('pwd').value='5Lq##KTESJ4';" +
//                "javascript:document.getElementsByName('Submit')[0].click();"));
//        customTabsIntent.launchUrl(this, Uri.parse(url));
//
//    }

//    public void readPdfFileVerify(String pdfUrl) {
//
//        try (BufferedInputStream inputStream = new BufferedInputStream(new URL(pdfUrl).openStream());
//             FileOutputStream fileOS = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/file_name.txt")) {
//            byte data[] = new byte[1024];
//            int byteContent;
//            while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
//                fileOS.write(data, 0, byteContent);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onExternalPageRequest(String url) {
    }

//    private void uploadFile() {
//        if (mImageUri != null) {
//            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
//                    + "." + getFileExtension(mImageUri));
//            mUploadTask = fileReference.putFile(mImageUri)
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            Handler handler = new Handler();
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mProgressBar.setProgress(0);
//                                }
//                            }, 500);
//                            Toast.makeText(MainActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
//                            Upload upload = new Upload(mEditTextFileName.getText().toString().trim(),
//                                    taskSnapshot.getDownloadUrl().toString());
//                            String uploadId = mDatabaseRef.push().getKey();
//                            mDatabaseRef.child(uploadId).setValue(upload);
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
//                            mProgressBar.setProgress((int) progress);
//                        }
//                    });
//        } else {
//            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
//        }
//    }

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
                                if (y < lines.length - 1) {
                                    if (!containCode(lines[y + 1]) && !containCode(lines[y])) {
                                        breakk = true;
                                        break;
                                    }
                                } else if (!containCode(lines[y])) {
                                    breakk = true;
                                    break;
                                }

                                String code = getCode(lines[y]);
                                String name = getName(lines[y]);
                                String grade = getGrade(lines[y]);
                                if (containCode(code) && !name.isEmpty() && !grade.isEmpty()) {
                                    trimester.addSubject(code, name, grade);
                                    subjects.add(new subjects(code, name, grade));
                                }
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
            SaveTo("ModifiedInfo", Name, Id, Degree, "Modified");
            SaveTo("CamsysInfo", Name, Id, Degree, "Camsys");
            SaveTo("Profile", Name, Id, Degree, "Camsys");
            SetSubjectsReviews(Id);
            InfoAvail = true;
            saveData("camsys", "Auto");
            fragmentEdit = new FragmentEdit(loadData("Id"), context, dashBoardActivity);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new FragmentProfile(loadData("Id"), context, dashBoardActivity)).commit();
            Toast.makeText(context, "Your profile has been updated successfully!", Toast.LENGTH_LONG).show();
        }
    }

    public void resetProfileFrag() {
        fragmentEdit = new FragmentEdit(loadData("Id"), context, dashBoardActivity);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                fragmentEdit).commit();
    }

    public void SaveTo(String slotName, String Name, String Id, String Degree, String modifiedFrom) {
        updateFromCamsys = true;
        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child(slotName).removeValue();
        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child(slotName).child("Name").setValue(Name);
        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child(slotName).child("Id").setValue(Id);
        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child(slotName).child("Degree").setValue(Degree);
        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child(slotName).child("UpdatedFrom").setValue(modifiedFrom);
        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child(slotName).child("Trimesters").setValue(trimesters);
        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child(slotName).child("Intake").setValue(trimesters.get(0).getSemesterName());
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

    public void SetSubjectsReviewsArr(final ArrayList<subjects> subjectsArr, final String Id) {
        FirebaseDatabase.getInstance().getReference().child("Subjects").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (subjects subjects : subjectsArr) {
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
                name.append(format(word.trim())).append(" ");
            }
        }
        return name.toString().trim();
    }

    public String format(String word) {
        if (word.length() > 3) {
            return WordUtils.capitalizeFully(word);
        } else {
            return word.toLowerCase();
        }
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