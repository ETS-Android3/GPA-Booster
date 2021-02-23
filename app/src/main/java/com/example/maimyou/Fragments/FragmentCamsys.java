package com.example.maimyou.Fragments;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.maimyou.Activities.DashBoardActivity;
import com.example.maimyou.Adapters.camsysWebsitesAdapter;
import com.example.maimyou.CarouselLayout.Tip;
import com.example.maimyou.Classes.camsysPage;
import com.example.maimyou.Dialogs.TipContainerDialog;
import com.example.maimyou.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.just.agentweb.AgentWeb;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.example.maimyou.Activities.DashBoardActivity.InfoAvail;
import static com.example.maimyou.Activities.DashBoardActivity.fragmentIndex;

import java.util.ArrayList;

import im.delight.android.webview.AdvancedWebView;

public class FragmentCamsys extends Fragment {

    //Vars
    boolean tipOpened = false, showUpload = true;
    DashBoardActivity dashBoardActivity;
    TipContainerDialog cdd;

    public FragmentCamsys(DashBoardActivity dashBoardActivity) {
        this.dashBoardActivity = dashBoardActivity;
    }

    //views
    FrameLayout arrow;
    PopupWindow mypopupWindow;
    ImageButton lightBulb, openMenu;
    FloatingActionButton edit;
    public static AdvancedWebView webView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentIndex = 0;
        return inflater.inflate(R.layout.fagment_camsys, container, false);
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility", "SetJavaScriptEnabled"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getView() != null) {
            ImageButton backB = getView().findViewById(R.id.backB);
            if (!InfoAvail) {
                backB.setVisibility(View.GONE);
            }
            FirebaseDatabase.getInstance().getReference().child("Member").child(dashBoardActivity.loadData("Id")).child("Profile").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        backB.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            openMenu = getView().findViewById(R.id.openMenu);
            edit = getView().findViewById(R.id.edit);

//            Container = getView().findViewById(R.id.Container);
//            checkBox = getView().findViewById(R.id.checkBox);
//            closeTip = getView().findViewById(R.id.closeTip);
//            back = getView().findViewById(R.id.back);
//            next = getView().findViewById(R.id.next);
//            pageNumber = getView().findViewById(R.id.pageNumber);
//            cardView = getView().findViewById(R.id.cardView);

            lightBulb = getView().findViewById(R.id.lightBulb);
//            tipContainer = getView().findViewById(R.id.tipContainer);
//            fadeOutNoDelay(tipContainer);
            cdd = new TipContainerDialog(dashBoardActivity);
            ArrayList<Tip> tips = new ArrayList<>();
            tips.add(new Tip(R.mipmap.ic_launcher, "", ""));
            tips.add(new Tip(R.mipmap.ic_launcher, "", ""));
            tips.add(new Tip(R.mipmap.ic_launcher, "", ""));
            tips.add(new Tip(R.mipmap.ic_launcher, "", ""));
            tips.add(new Tip(R.mipmap.ic_launcher, "", ""));
            tips.add(new Tip(R.mipmap.ic_launcher, "", ""));
            tips.add(new Tip(R.mipmap.ic_launcher, "", ""));
            tips.add(new Tip(R.mipmap.ic_launcher, "", ""));
            tips.add(new Tip(R.mipmap.ic_launcher, "", ""));
            tips.add(new Tip(R.mipmap.ic_launcher, "", ""));
            cdd.setTips(tips);

            cdd.setCanceledOnTouchOutside(true);
            cdd.setOnDismissListener(dialog -> {
                tipOpened = false;
                webView.onResume();
            });
//            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


            arrow = getView().findViewById(R.id.arrow);
            fadeOutNoDelay(arrow);
            webView = getView().findViewById(R.id.webView);
            webView.setListener(dashBoardActivity, dashBoardActivity);
            webView.setMixedContentAllowed(true);
            webView.setThirdPartyCookiesEnabled(true);
//            webView.setDesktopMode(true);

            WebSettings webSettings= webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setPluginState(WebSettings.PluginState.ON);
//            webSettings.setGeolocationEnabled(true);
//            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            webSettings.setBuiltInZoomControls(true);
//            webSettings.setSaveFormData(true);
//            webSettings.setAppCacheEnabled(true);
//            webSettings.setDatabaseEnabled(true);
//            webSettings.setDomStorageEnabled(true);
//            webSettings.setLoadsImagesAutomatically(true);
//            webSettings.setAllowFileAccess(true);
//            webSettings.setAllowFileAccessFromFileURLs(true);
//            webSettings.setAllowUniversalAccessFromFileURLs(true);
//            webSettings.setUseWideViewPort(true);
//            webSettings.setBuiltInZoomControls(true);
//            webSettings.setSupportMultipleWindows(true);

//            webSettings.setGeolocationDatabasePath(getFilesDir().getPath());
//            webSettings.setAllowFileAccess(true);
//            webSettings.setAllowUniversalAccessFromFileURLs(true);
//            webView.getSettings().setLoadsImagesAutomatically(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//            webView.setWebViewClient(new WebViewClient() {
//                @Override
//                public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                    if (url.endsWith(".pdf")) {
//                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//                        CookieManager cookieManager = CookieManager.getInstance();
//                        String cookie = cookieManager.getCookie("https://cms.mmu.edu.my");     // which is "http://bookboon.com"
//                        request.addRequestHeader("Cookie", cookie);
//                        request.setNotificationVisibility(1);
//                        request.allowScanningByMediaScanner();
//                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/camsys.pdf");
//                        request.setMimeType("application/pdf");
//                        DownloadManager dm = (DownloadManager) dashBoardActivity.getSystemService(DOWNLOAD_SERVICE);
//                        dm.enqueue(request);
//                        new Handler().postDelayed(() -> {
//                            dashBoardActivity.scanResFromPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/camsys.pdf");
//                        }, 2000);
//                    }
//                    return true;
//                }
//            });

//            FrameLayout WebCon=getView().findViewById(R.id.WebCon);
//            AgentWeb  mAgentWeb = AgentWeb.with(this)//传入Activity or Fragment
//                    .setAgentWebParent(WebCon, new FrameLayout.LayoutParams(-1, -1))//Incoming AgentWeb parent control, if the parent control is RelativeLayout, then the second parameter needs to be passed RelativeLayout.LayoutParams, the first parameter and the second parameter should correspond.
//                    .useDefaultIndicator()// use the default onProgress bar
////                    .defaultProgressBarColor() // Use default onProgress bar color
////                    .setWebViewClient(mWebViewClient) //Set the Web page title callback
//
//                    .createAgentWeb()//
//                    .ready()
//                    .go("https://cms.mmu.edu.my/psc/csprd/EMPLOYEE/HRMS/c/N_SR_STUDENT_RECORDS.N_ON_RSLT_PNL.GBL?PORTALPARAM_PTCNAV=ONLINE_RESULT&amp;EOPP.SCNode=HRMS&amp;EOPP.SCPortal=EMPLOYEE&amp;EOPP.SCName=CO_EMPLOYEE_SELF_SERVICE&amp;EOPP.SCLabel=Self%20Service&amp;EOPP.SCPTfname=CO_EMPLOYEE_SELF_SERVICE&amp;FolderPath=PORTAL_ROOT_OBJECT.CO_EMPLOYEE_SELF_SERVICE.HCCC_ACADEMIC_RECORDS.ONLINE_RESULT&amp;IsFolder=false&amp;PortalActualURL=https%3a%2f%2fcms.mmu.edu.my%2fpsc%2fcsprd%2fEMPLOYEE%2fHRMS%2fc%2fN_SR_STUDENT_RECORDS.N_ON_RSLT_PNL.GBL&amp;PortalContentURL=https%3a%2f%2fcms.mmu.edu.my%2fpsc%2fcsprd%2fEMPLOYEE%2fHRMS%2fc%2fN_SR_STUDENT_RECORDS.N_ON_RSLT_PNL.GBL&amp;PortalContentProvider=HRMS&amp;PortalCRefLabel=Academic%20Achievement&amp;PortalRegistryName=EMPLOYEE&amp;PortalServletURI=https%3a%2f%2fcms.mmu.edu.my%2fpsp%2fcsprd%2f&amp;PortalURI=https%3a%2f%2fcms.mmu.edu.my%2fpsc%2fcsprd%2f&amp;PortalHostNode=HRMS&amp;NoCrumbs=yes&amp;PortalKeyStruct=yes");
            FirebaseDatabase.getInstance().getReference().child("Member").child(dashBoardActivity.loadData("Id")).child("DefaultWeb").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && snapshot.getValue() != null) {
                        setWebView(snapshot.getValue().toString());
                    } else {
                        setWebView("https://cms.mmu.edu.my/psc/csprd/EMPLOYEE/HRMS/c/N_SR_STUDENT_RECORDS.N_ON_RSLT_PNL.GBL?PORTALPARAM_PTCNAV=ONLINE_RESULT&amp;EOPP.SCNode=HRMS&amp;EOPP.SCPortal=EMPLOYEE&amp;EOPP.SCName=CO_EMPLOYEE_SELF_SERVICE&amp;EOPP.SCLabel=Self%20Service&amp;EOPP.SCPTfname=CO_EMPLOYEE_SELF_SERVICE&amp;FolderPath=PORTAL_ROOT_OBJECT.CO_EMPLOYEE_SELF_SERVICE.HCCC_ACADEMIC_RECORDS.ONLINE_RESULT&amp;IsFolder=false&amp;PortalActualURL=https%3a%2f%2fcms.mmu.edu.my%2fpsc%2fcsprd%2fEMPLOYEE%2fHRMS%2fc%2fN_SR_STUDENT_RECORDS.N_ON_RSLT_PNL.GBL&amp;PortalContentURL=https%3a%2f%2fcms.mmu.edu.my%2fpsc%2fcsprd%2fEMPLOYEE%2fHRMS%2fc%2fN_SR_STUDENT_RECORDS.N_ON_RSLT_PNL.GBL&amp;PortalContentProvider=HRMS&amp;PortalCRefLabel=Academic%20Achievement&amp;PortalRegistryName=EMPLOYEE&amp;PortalServletURI=https%3a%2f%2fcms.mmu.edu.my%2fpsp%2fcsprd%2f&amp;PortalURI=https%3a%2f%2fcms.mmu.edu.my%2fpsc%2fcsprd%2f&amp;PortalHostNode=HRMS&amp;NoCrumbs=yes&amp;PortalKeyStruct=yes");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            FirebaseDatabase.getInstance().getReference().child("Member").child(dashBoardActivity.loadData("Id")).child("DonnotShowUpload").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        animationOutDOWN(edit, 400);
                        showUpload = false;
                    } else {
                        showUpload = true;
                        animationInUP(edit, 400);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            FirebaseDatabase.getInstance().getReference().child("Member").child(dashBoardActivity.loadData("Id")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.child("DoNotShowUploadTip").exists()) {
                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(() -> {
                            OpenTip(200);
                            fadeIn(arrow, 200);
                            handler.postDelayed(() -> {
                                fadeOut(arrow, 400);
                            }, 2000);

                        }, 1000);

                    } else {
                        cdd.setCheckBox(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

//            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                if (isChecked) {
//                    FirebaseDatabase.getInstance().getReference().child("Member").child(dashBoardActivity.loadData("Id")).child("DoNotShowUploadTip").setValue("1");
//                } else {
//                    FirebaseDatabase.getInstance().getReference().child("Member").child(dashBoardActivity.loadData("Id")).child("DoNotShowUploadTip").removeValue();
//                }
//            });
//            tipContainer.setOnTouchListener((v, event) -> {
//                CloseTip(200);
//                return false;
//            });
//            back.setOnClickListener(v -> {
//                currentPage--;
//                if (currentPage < 0) {
//                    currentPage = games.size() - 1;
//                }
//                pageNumber.setText((currentPage + 1) + "/" + games.size());
//                coverFlow.scrollToPosition(currentPage);
//            });
//            next.setOnClickListener(v -> {
//                currentPage++;
//                if (currentPage >= games.size()) {
//                    currentPage = 0;
//                }
//                pageNumber.setText((currentPage + 1) + "/" + games.size());
//                coverFlow.scrollToPosition(currentPage);
//            });
//            closeTip.setOnClickListener(v -> CloseTip(200));

            try {
                setPopUpWindow();
            } catch (Exception e) {
                e.printStackTrace();
            }

            openMenu.setOnClickListener(v -> {
//                PopupMenu popup = new PopupMenu(getContext(), view);
//                popup.getMenuInflater().inflate(R.menu.course_structure_menu, popup.getMenu());
////                popup.setOnMenuItemClickListener(item -> {
////                    if (item.getTitle().toString().toLowerCase().contains("download")) {
////                        openCustomTab();
////                    } else if (item.getTitle().toString().toLowerCase().contains("upload course structure")) {
////                        if (!busy) {
////                            if (verifyStoragePermissions(this)) {
////                                Intent intent = new Intent();
////                                intent.setType("application/pdf");
////                                intent.setAction(Intent.ACTION_GET_CONTENT);
////                                String[] mimetypes = {"application/pdf"};
////                                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
////                                startActivityForResult(Intent.createChooser(intent, "Choose Pdf"), 1);
////                            }
////                        } else {
////                            Toast.makeText(this, "loading", Toast.LENGTH_SHORT).show();
////                        }
////                    } else if (item.getTitle().toString().toLowerCase().contains("upload syllabus")) {
////                        if (!busy) {
////                            if (verifyStoragePermissions(this)) {
////                                Intent intent = new Intent();
////                                intent.setType("application/pdf");
////                                intent.setAction(Intent.ACTION_GET_CONTENT);
////                                String[] mimetypes = {"application/pdf"};
////                                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
////                                startActivityForResult(Intent.createChooser(intent, "Choose Pdf"), 2);
////                            }
////                        } else {
////                            Toast.makeText(this, "loading", Toast.LENGTH_SHORT).show();
////                        }
////                    } else if (item.getTitle().toString().toLowerCase().contains("help")) {
////                        Toast.makeText(context, "Help", Toast.LENGTH_SHORT).show();
////                    }
////                    return true;
////                });
//                popup.show();

                CloseTip(200);
                mypopupWindow.showAsDropDown(v, -153, 0);
                //showAsDropDown(below which view you want to show as dropdown,horizontal position, vertical position)


            });
            lightBulb.setOnClickListener(v -> OpenCloseTip(200));
        }
    }

    public void setPopUpWindow() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            LayoutInflater inflater = null;
            inflater = (LayoutInflater)
                    getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.camsys_pop_up, null);

            CheckBox checkBox = view.findViewById(R.id.ShowUploadB);
            ListView webSites = view.findViewById(R.id.webSites);
            ArrayList<camsysPage> pages = new ArrayList<>();

            FirebaseDatabase.getInstance().getReference().child("Member").child(dashBoardActivity.loadData("Id")).child("DonnotShowUpload").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    checkBox.setChecked(!snapshot.exists());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    FirebaseDatabase.getInstance().getReference().child("Member").child(dashBoardActivity.loadData("Id")).child("DonnotShowUpload").removeValue();
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Member").child(dashBoardActivity.loadData("Id")).child("DonnotShowUpload").setValue("1");
                }
            });

            FirebaseDatabase.getInstance().getReference().child("Camsys").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        if (snapshot.exists()) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                if (child.getKey() != null && child.getValue() != null) {
                                    pages.add(new camsysPage(child.getKey(), child.getValue().toString()));
                                }
                            }
                            if (pages.size() > 1) {
                                camsysWebsitesAdapter adapter = new camsysWebsitesAdapter(getContext(), R.layout.websites, pages);
                                webSites.setAdapter(adapter);
                                webSites.setOnItemClickListener((parent, view1, position, id) -> {
                                    FirebaseDatabase.getInstance().getReference().child("Member").child(dashBoardActivity.loadData("Id")).child("DefaultWeb").setValue(pages.get(position).getPageAdd());
                                    setWebView(pages.get(position).getPageAdd());
                                });
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            mypopupWindow = new PopupWindow(view, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void setWebView(String url) {
//        String url = "https://cms.mmu.edu.my/psc/csprd/EMPLOYEE/HRMS/c/N_SR_STUDENT_RECORDS.N_ON_RSLT_PNL.GBL?PORTALPARAM_PTCNAV=ONLINE_RESULT&amp;EOPP.SCNode=HRMS&amp;EOPP.SCPortal=EMPLOYEE&amp;EOPP.SCName=CO_EMPLOYEE_SELF_SERVICE&amp;EOPP.SCLabel=Self%20Service&amp;EOPP.SCPTfname=CO_EMPLOYEE_SELF_SERVICE&amp;FolderPath=PORTAL_ROOT_OBJECT.CO_EMPLOYEE_SELF_SERVICE.HCCC_ACADEMIC_RECORDS.ONLINE_RESULT&amp;IsFolder=false&amp;PortalActualURL=https%3a%2f%2fcms.mmu.edu.my%2fpsc%2fcsprd%2fEMPLOYEE%2fHRMS%2fc%2fN_SR_STUDENT_RECORDS.N_ON_RSLT_PNL.GBL&amp;PortalContentURL=https%3a%2f%2fcms.mmu.edu.my%2fpsc%2fcsprd%2fEMPLOYEE%2fHRMS%2fc%2fN_SR_STUDENT_RECORDS.N_ON_RSLT_PNL.GBL&amp;PortalContentProvider=HRMS&amp;PortalCRefLabel=Academic%20Achievement&amp;PortalRegistryName=EMPLOYEE&amp;PortalServletURI=https%3a%2f%2fcms.mmu.edu.my%2fpsp%2fcsprd%2f&amp;PortalURI=https%3a%2f%2fcms.mmu.edu.my%2fpsc%2fcsprd%2f&amp;PortalHostNode=HRMS&amp;NoCrumbs=yes&amp;PortalKeyStruct=yes";
        try {
            webView.loadUrl(url);
//            webView.clearFocus();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                webView.zoomBy((float) 50);
//            }
//            webView.setWebViewClient(new WebViewClient());
//            WebSettings webSettings = webView.getSettings();
//            webSettings.setJavaScriptEnabled(true);
////        setTitle(url);
////        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            webView.loadUrl(url);
//
//            webView.setDownloadListener((url1, userAgent, contentDisposition, mimeType, contentLength) -> {
//                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url1));
//
//                request.setMimeType(mimeType);
//                //------------------------COOKIE!!------------------------
//                String cookies = CookieManager.getInstance().getCookie(url1);
//                request.addRequestHeader("cookie", cookies);
//                //------------------------COOKIE!!------------------------
//                request.addRequestHeader("User-Agent", userAgent);
//                request.setDescription("Downloading file...");
//                request.setTitle(URLUtil.guessFileName(url1, contentDisposition, mimeType));
//                request.allowScanningByMediaScanner();
//                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url1, contentDisposition, mimeType));
//                DownloadManager dm = (DownloadManager) Objects.requireNonNull(getActivity()).getSystemService(DOWNLOAD_SERVICE);
//                dm.enqueue(request);
//                Toast.makeText(getContext(), "Downloading File", Toast.LENGTH_LONG).show();
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @SuppressLint("SetTextI18n")
//    private FeatureCoverFlow.OnScrollPositionListener onScrollListener() {
//        return new FeatureCoverFlow.OnScrollPositionListener() {
//            @Override
//            public void onScrolledToPosition(int position) {
//                Log.v("MainActiivty", "position: " + position);
//                currentPage = position;
//                pageNumber.setText((currentPage + 1) + "/" + games.size());
//            }
//
//            @Override
//            public void onScrolling() {
//                Log.i("MainActivity", "scrolling");
//                pageNumber.setText("-/" + games.size());
//            }
//        };
//    }
//
//    private void settingDummyData() {
//        games = new ArrayList<>();
//        games.add(new Game(R.mipmap.ic_launcher, "Assassin Creed 3"));
//        games.add(new Game(R.mipmap.ic_launcher, "Avatar 3D"));
//        games.add(new Game(R.mipmap.ic_launcher, "Call Of Duty Black Ops 3"));
//        games.add(new Game(R.mipmap.ic_launcher, "DotA 2"));
//        games.add(new Game(R.mipmap.ic_launcher, "Halo 5"));
//        games.add(new Game(R.mipmap.ic_launcher, "Left 4 Dead 2"));
//        games.add(new Game(R.mipmap.ic_launcher, "StarCraft"));
//        games.add(new Game(R.mipmap.ic_launcher, "The Witcher 3"));
//        games.add(new Game(R.mipmap.ic_launcher, "Tom raider 3"));
//        games.add(new Game(R.mipmap.ic_launcher, "Need for Speed Most Wanted"));
//    }

    public void OpenTip(int duration) {
        if (!tipOpened) {
//            expand(cardView, duration);
//            fadeIn(tipContainer, duration);
//            animationOutDOWN(edit, duration);
            cdd.show();
            tipOpened = true;
            webView.onPause();
        }
    }

    public void CloseTip(int duration) {
        if (tipOpened) {
//            contract(cardView, duration);
//            fadeOut(tipContainer, duration);
//            animationInUP(edit, duration);
            cdd.dismiss();
            tipOpened = false;
            webView.onResume();
        }
    }

    public void OpenCloseTip(int duration) {
        if (tipOpened) {
//            contract(cardView, duration / 2);
//            fadeOut(tipContainer, duration);
//            animationInUP(edit, duration / 2);
            webView.onResume();
            tipOpened = false;
            webView.onResume();
        } else {
//            expand(cardView, duration / 2);
//            fadeIn(tipContainer, duration);
//            animationOutDOWN(edit, duration / 2);
            cdd.show();
            tipOpened = true;
            webView.onPause();
        }
    }

//    public void expand(final View view, int duration) {
//        Container.setVisibility(View.INVISIBLE);
//        view.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//        final int targetHeight = view.getMeasuredHeight();
//        final int targetWidth = view.getMeasuredWidth();
//
//        view.getLayoutParams().height = 0;
//        view.getLayoutParams().width = 0;
//        view.setVisibility(View.VISIBLE);
//
//        ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredHeight(), targetHeight);
//
//        anim.setInterpolator(new AccelerateInterpolator());
//        anim.setDuration(duration);
//        anim.addUpdateListener(animation -> {
//            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
//            layoutParams.height = (int) (targetHeight * animation.getAnimatedFraction());
//            layoutParams.width = (int) (targetWidth * animation.getAnimatedFraction());
//            view.setLayoutParams(layoutParams);
//        });
//        anim.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                // At the end of animation, set the height to wrap content
//                // This fix is for long views that are not shown on screen
//                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
//                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
//                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
//                Container.setVisibility(View.VISIBLE);
//            }
//        });
//        anim.start();
//    }
//
//    public void contract(final View view, long duration) {
//        Container.setVisibility(View.INVISIBLE);
//        view.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//        final int targetHeight = view.getMeasuredHeight();
//        final int targetWidth = view.getMeasuredWidth();
//
////        view.getLayoutParams().height = targetHeight;
////        view.getLayoutParams().width = targetWidth;
//        view.setVisibility(View.VISIBLE);
//
//        ValueAnimator anim = ValueAnimator.ofInt(0, 1);
//
//        anim.setInterpolator(new AccelerateInterpolator());
//        anim.setDuration(duration);
//        anim.addUpdateListener(animation -> {
//            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
//            layoutParams.height = (int) (targetHeight * (1 - animation.getAnimatedFraction()));
//            layoutParams.width = (int) (targetWidth * (1 - animation.getAnimatedFraction()));
//            view.setLayoutParams(layoutParams);
//        });
//        anim.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                // At the end of animation, set the height to wrap content
//                // This fix is for long views that are not shown on screen
//                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
//                layoutParams.height = 0;
//                layoutParams.width = 0;
//            }
//        });
//        anim.start();
//    }

    public void fadeIn(View view, int duration) {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(duration);
        fadeIn.setFillAfter(true);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(fadeIn);
    }

    public void fadeOut(View view, int duration) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new DecelerateInterpolator()); //and this
        fadeOut.setDuration(duration);
        fadeOut.setFillAfter(true);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(fadeOut);
    }

    public void fadeOutNoDelay(View view) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new DecelerateInterpolator()); //and this
        fadeOut.setDuration(0);
        fadeOut.setFillAfter(true);
        view.startAnimation(fadeOut);
    }

    public void animationInUP(View view, int Duration) {
        if (showUpload) {
            Animation inFromBottom = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, +1.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f);
            inFromBottom.setDuration(Duration);
            inFromBottom.setInterpolator(new AccelerateInterpolator());
            inFromBottom.setFillAfter(true);
            view.startAnimation(inFromBottom);
        }
    }

    private void animationOutDOWN(View view, int Duration) {
        if (showUpload) {
            Animation outtoBottom = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, +1.0f);
            outtoBottom.setDuration(Duration);
            outtoBottom.setInterpolator(new AccelerateInterpolator());
            outtoBottom.setFillAfter(true);
            view.startAnimation(outtoBottom);
        }
    }
}