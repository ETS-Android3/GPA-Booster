package com.example.maimyou.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.maimyou.Activities.DashBoardActivity;
import com.example.maimyou.CarouselLayout.CoverFlowAdapter;
import com.example.maimyou.CarouselLayout.Game;
import com.example.maimyou.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import it.moondroid.coverflow.components.ui.containers.FeatureCoverFlow;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.example.maimyou.Activities.RegisterActivity.SHARED_PREFS;

public class FragmentCamsys extends Fragment {
    private FeatureCoverFlow coverFlow;
    private ArrayList<Game> games;
    int currentPage = 0, tipHeight = 0, tipWidth = 0;
    boolean tipOpened = false;
    DashBoardActivity dashBoardActivity;

    public FragmentCamsys(DashBoardActivity dashBoardActivity) {
        this.dashBoardActivity = dashBoardActivity;
    }

    //views
    Button back, next;
    TextView pageNumber;
    CardView cardView;
    FrameLayout arrow;
    ImageButton closeTip, lightBulb;
    RelativeLayout tipContainer;
    CheckBox checkBox;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fagment_camsys, container, false);
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getView() != null) {

            checkBox = getView().findViewById(R.id.checkBox);
            lightBulb = getView().findViewById(R.id.lightBulb);
            tipContainer = getView().findViewById(R.id.tipContainer);
            fadeOutNoDelay(tipContainer);
            closeTip = getView().findViewById(R.id.closeTip);
            back = getView().findViewById(R.id.back);
            next = getView().findViewById(R.id.next);
            pageNumber = getView().findViewById(R.id.pageNumber);
            cardView = getView().findViewById(R.id.cardView);
            arrow=getView().findViewById(R.id.arrow);
            fadeOutNoDelay(arrow);

            setWebView(getView().findViewById(R.id.webView));

            coverFlow = getView().findViewById(R.id.coverflow);

            settingDummyData();
            CoverFlowAdapter adapter = new CoverFlowAdapter(getContext(), games);
            coverFlow.setAdapter(adapter);
            coverFlow.setOnScrollPositionListener(onScrollListener());
            pageNumber.setText((currentPage + 1) + "/" + games.size());

            FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.child("DoNotShowUploadTip").exists()) {
                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(() -> {
                            OpenTip(200);
                            fadeIn(arrow,200);
                            handler.postDelayed(() -> {
                                fadeOut(arrow,400);
                            }, 2000);

                        }, 1000);

                    } else {
                        checkBox.setChecked(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("DoNotShowUploadTip").setValue("1");
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("DoNotShowUploadTip").removeValue();
                }
            });
            tipContainer.setOnTouchListener((v, event) -> {
                CloseTip(200);
                return false;
            });
            back.setOnClickListener(v -> {
                currentPage--;
                if (currentPage < 0) {
                    currentPage = games.size() - 1;
                }
                pageNumber.setText((currentPage + 1) + "/" + games.size());
                coverFlow.scrollToPosition(currentPage);
            });
            next.setOnClickListener(v -> {
                currentPage++;
                if (currentPage >= games.size()) {
                    currentPage = 0;
                }
                pageNumber.setText((currentPage + 1) + "/" + games.size());
                coverFlow.scrollToPosition(currentPage);
            });
            closeTip.setOnClickListener(v -> CloseTip(200));
            lightBulb.setOnClickListener(v -> OpenCloseTip(200));
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void setWebView(WebView webView) {
        String url = "https://cms.mmu.edu.my/psc/csprd/EMPLOYEE/HRMS/c/N_SR_STUDENT_RECORDS.N_ON_RSLT_PNL.GBL?PORTALPARAM_PTCNAV=ONLINE_RESULT&amp;EOPP.SCNode=HRMS&amp;EOPP.SCPortal=EMPLOYEE&amp;EOPP.SCName=CO_EMPLOYEE_SELF_SERVICE&amp;EOPP.SCLabel=Self%20Service&amp;EOPP.SCPTfname=CO_EMPLOYEE_SELF_SERVICE&amp;FolderPath=PORTAL_ROOT_OBJECT.CO_EMPLOYEE_SELF_SERVICE.HCCC_ACADEMIC_RECORDS.ONLINE_RESULT&amp;IsFolder=false&amp;PortalActualURL=https%3a%2f%2fcms.mmu.edu.my%2fpsc%2fcsprd%2fEMPLOYEE%2fHRMS%2fc%2fN_SR_STUDENT_RECORDS.N_ON_RSLT_PNL.GBL&amp;PortalContentURL=https%3a%2f%2fcms.mmu.edu.my%2fpsc%2fcsprd%2fEMPLOYEE%2fHRMS%2fc%2fN_SR_STUDENT_RECORDS.N_ON_RSLT_PNL.GBL&amp;PortalContentProvider=HRMS&amp;PortalCRefLabel=Academic%20Achievement&amp;PortalRegistryName=EMPLOYEE&amp;PortalServletURI=https%3a%2f%2fcms.mmu.edu.my%2fpsp%2fcsprd%2f&amp;PortalURI=https%3a%2f%2fcms.mmu.edu.my%2fpsc%2fcsprd%2f&amp;PortalHostNode=HRMS&amp;NoCrumbs=yes&amp;PortalKeyStruct=yes";
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
//        setTitle(url);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        webView.loadUrl(url);

        webView.setDownloadListener((url1, userAgent, contentDisposition, mimeType, contentLength) -> {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url1));

            request.setMimeType(mimeType);
            //------------------------COOKIE!!------------------------
            String cookies = CookieManager.getInstance().getCookie(url1);
            request.addRequestHeader("cookie", cookies);
            //------------------------COOKIE!!------------------------
            request.addRequestHeader("User-Agent", userAgent);
            request.setDescription("Downloading file...");
            request.setTitle(URLUtil.guessFileName(url1, contentDisposition, mimeType));
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url1, contentDisposition, mimeType));
            DownloadManager dm = (DownloadManager) Objects.requireNonNull(getActivity()).getSystemService(DOWNLOAD_SERVICE);
            dm.enqueue(request);
            Toast.makeText(getContext(), "Downloading File", Toast.LENGTH_LONG).show();
        });
    }

    @SuppressLint("SetTextI18n")
    private FeatureCoverFlow.OnScrollPositionListener onScrollListener() {
        return new FeatureCoverFlow.OnScrollPositionListener() {
            @Override
            public void onScrolledToPosition(int position) {
                Log.v("MainActiivty", "position: " + position);
                currentPage = position;
                pageNumber.setText((currentPage + 1) + "/" + games.size());
            }

            @Override
            public void onScrolling() {
                Log.i("MainActivity", "scrolling");
                pageNumber.setText("-/" + games.size());
            }
        };
    }

    private void settingDummyData() {
        games = new ArrayList<>();
        games.add(new Game(R.mipmap.ic_launcher, "Assassin Creed 3"));
        games.add(new Game(R.mipmap.ic_launcher, "Avatar 3D"));
        games.add(new Game(R.mipmap.ic_launcher, "Call Of Duty Black Ops 3"));
        games.add(new Game(R.mipmap.ic_launcher, "DotA 2"));
        games.add(new Game(R.mipmap.ic_launcher, "Halo 5"));
        games.add(new Game(R.mipmap.ic_launcher, "Left 4 Dead 2"));
        games.add(new Game(R.mipmap.ic_launcher, "StarCraft"));
        games.add(new Game(R.mipmap.ic_launcher, "The Witcher 3"));
        games.add(new Game(R.mipmap.ic_launcher, "Tom raider 3"));
        games.add(new Game(R.mipmap.ic_launcher, "Need for Speed Most Wanted"));
    }

    public void OpenTip(int duration) {
        if (!tipOpened) {
            expand(cardView, duration);
            fadeIn(tipContainer, duration);
            tipOpened = true;
        }
    }

    public void CloseTip(int duration) {
        if (tipOpened) {
            contract(cardView, duration);
            fadeOut(tipContainer, duration);
            tipOpened = false;
        }
    }

    public void OpenCloseTip(int duration) {
        if (tipOpened) {
            contract(cardView, duration / 2);
            fadeOut(tipContainer, duration);
            tipOpened = false;
        } else {
            expand(cardView, duration / 2);
            fadeIn(tipContainer, duration);
            tipOpened = true;
        }
    }

    public void expand(final View view, int duration) {
        view.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        final int targetHeight = view.getMeasuredHeight();
        final int targetWidth = view.getMeasuredWidth();

        view.getLayoutParams().height = 0;
        view.getLayoutParams().width = 0;
        view.setVisibility(View.VISIBLE);

        ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredHeight(), targetHeight);

        anim.setInterpolator(new AccelerateInterpolator());
        anim.setDuration(duration);
        anim.addUpdateListener(animation -> {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = (int) (targetHeight * animation.getAnimatedFraction());
            layoutParams.width = (int) (targetWidth * animation.getAnimatedFraction());
            view.setLayoutParams(layoutParams);
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // At the end of animation, set the height to wrap content
                // This fix is for long views that are not shown on screen
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            }
        });
        anim.start();
    }

    public void contract(final View view, long duration) {
        view.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        final int targetHeight = view.getMeasuredHeight();
        final int targetWidth = view.getMeasuredWidth();

//        view.getLayoutParams().height = targetHeight;
//        view.getLayoutParams().width = targetWidth;
        view.setVisibility(View.VISIBLE);

        ValueAnimator anim = ValueAnimator.ofInt(0, 1);

        anim.setInterpolator(new AccelerateInterpolator());
        anim.setDuration(duration);
        anim.addUpdateListener(animation -> {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = (int) (targetHeight * (1 - animation.getAnimatedFraction()));
            layoutParams.width = (int) (targetWidth * (1 - animation.getAnimatedFraction()));
            view.setLayoutParams(layoutParams);
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // At the end of animation, set the height to wrap content
                // This fix is for long views that are not shown on screen
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = 0;
                layoutParams.width = 0;
            }
        });
        anim.start();
    }

    public void fadeIn(View view, int duration) {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(duration);
        fadeIn.setFillAfter(true);
        view.startAnimation(fadeIn);
    }

    public void fadeOut(View view, int duration) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new DecelerateInterpolator()); //and this
        fadeOut.setDuration(duration);
        fadeOut.setFillAfter(true);
        view.startAnimation(fadeOut);
    }

    public void fadeOutNoDelay(View view) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new DecelerateInterpolator()); //and this
        fadeOut.setDuration(0);
        fadeOut.setFillAfter(true);
        view.startAnimation(fadeOut);
    }

    public String loadData(String name) {
        SharedPreferences sharedPreferences = Objects.requireNonNull(this.getActivity()).getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if (sharedPreferences == null) {
            return "";
        }
        return sharedPreferences.getString(name, "");
    }
}