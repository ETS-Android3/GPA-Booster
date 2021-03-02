package com.example.maimyou.Classes;

import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.example.maimyou.Activities.DashBoardActivity;

import static com.example.maimyou.Activities.DashBoardActivity.InfoAvail;
import static com.example.maimyou.Activities.DashBoardActivity.LoadRes;
import static com.example.maimyou.Activities.DashBoardActivity.SignIn;
import static com.example.maimyou.Activities.DashBoardActivity.signin;
import static com.example.maimyou.Activities.DashBoardActivity.ProfileWebView;

public class MyJavaScriptInterface {
    DashBoardActivity dashBoardActivity;

    public MyJavaScriptInterface(DashBoardActivity dashBoardActivity) {
        this.dashBoardActivity = dashBoardActivity;
    }

    public void loadJs(String js) {
        dashBoardActivity.runOnUiThread(() -> {
            if (ProfileWebView != null) ProfileWebView.evaluateJavascript(js, s -> {
//                new Handler().postDelayed(() -> {
//                    ProfileWebView.loadUrl(ProfileWebView.getUrl());
//                }, 5000);
            });
        });
    }

    public void checkBar() {
        dashBoardActivity.runOnUiThread(() -> {
            if (ProfileWebView != null)
                ProfileWebView.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);");
        });
    }

    public void toast(String ms) {
        dashBoardActivity.runOnUiThread(() -> Toast.makeText(dashBoardActivity.getApplicationContext(), ms, Toast.LENGTH_LONG).show());
    }

    public void refresh() {
        dashBoardActivity.runOnUiThread(() -> new Handler().postDelayed(() -> ProfileWebView.loadUrl(ProfileWebView.getUrl()), 2000));
    }

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void processHTML(String html) {
        // process the html as needed by the app

        if (html.contains("Your User ID and/or Password are invalid.")) {
            toast("Your User ID and/or Password are invalid!");
            signin = 10;
            SignIn=false;
        } else if (html.contains("Sign In")) {
            signin++;
            String js = "javascript:document.getElementById('userid').value='" + dashBoardActivity.loadData("camsysId") + "';" +
                    "javascript:document.getElementById('pwd').value='" + dashBoardActivity.loadData("camsysPassword") + "';" +
                    "javascript:document.getElementsByName('Submit')[0].click();";
            loadJs(js);
            if (1 < signin && signin < 10) {
                refresh();
            }
        } else if (html.contains("You have been barred due to outstanding fees.")) {
            toast("You have been barred due to outstanding fees.");
            if (!InfoAvail) toast("please set your info manually.");
            SignIn=true;
            dashBoardActivity.saveData("Barred", "Auto");
        } else if (html.contains("Academic Achievement")) {
            if (LoadRes < 1) {
                String js2 = "javascript:document.getElementById('N_ON_RSLT_VW_ACAD_CAREER').value='UGRD';" +
                        "javascript:document.getElementById('#ICSearch').click();";
                loadJs(js2);
                new Handler().postDelayed(() -> {
                    checkBar();
                    String js3 = "javascript:document.getElementById('N_REPORT_WRK_BUTTON').click();";
                    loadJs(js3);
                }, 500);
            }
            SignIn=true;
            LoadRes++;
        }
    }

//    @SuppressWarnings("unused")
//    public void processContent(String aContent)
//    {
//        content = aContent;
//        System.out.println("COOOOOOOOOOOOOOOOOOOOOOONNNNNNNNNNTEEEEEEEEEEEEEEEEEEENNNNNNNTTTTTTTTT:      "+content);
//
//        if(dashBoardActivity.loadData("camsys").isEmpty()){
//
//
////            if (url.contains("login")) {
////                String js1 = "javascript:document.getElementById('userid').value='1161104336';" +
////                        "javascript:document.getElementById('pwd').value='5Lq##KTESJ4';" +
////                        "javascript:document.getElementsByName('Submit')[0].click();";
////                ProfileWebView.evaluateJavascript(js1, s -> {
////                    new Handler().postDelayed(() -> {
////                        ProfileWebView.loadUrl("https://cms.mmu.edu.my/psc/csprd/EMPLOYEE/HRMS/c/N_SR_STUDENT_RECORDS.N_ON_RSLT_PNL.GBL?PORTALPARAM_PTCNAV=ONLINE_RESULT&amp;EOPP.SCNode=HRMS&amp;EOPP.SCPortal=EMPLOYEE&amp;EOPP.SCName=CO_EMPLOYEE_SELF_SERVICE&amp;EOPP.SCLabel=Self%20Service&amp;EOPP.SCPTfname=CO_EMPLOYEE_SELF_SERVICE&amp;FolderPath=PORTAL_ROOT_OBJECT.CO_EMPLOYEE_SELF_SERVICE.HCCC_ACADEMIC_RECORDS.ONLINE_RESULT&amp;IsFolder=false&amp;PortalActualURL=https%3a%2f%2fcms.mmu.edu.my%2fpsc%2fcsprd%2fEMPLOYEE%2fHRMS%2fc%2fN_SR_STUDENT_RECORDS.N_ON_RSLT_PNL.GBL&amp;PortalContentURL=https%3a%2f%2fcms.mmu.edu.my%2fpsc%2fcsprd%2fEMPLOYEE%2fHRMS%2fc%2fN_SR_STUDENT_RECORDS.N_ON_RSLT_PNL.GBL&amp;PortalContentProvider=HRMS&amp;PortalCRefLabel=Academic%20Achievement&amp;PortalRegistryName=EMPLOYEE&amp;PortalServletURI=https%3a%2f%2fcms.mmu.edu.my%2fpsp%2fcsprd%2f&amp;PortalURI=https%3a%2f%2fcms.mmu.edu.my%2fpsc%2fcsprd%2f&amp;PortalHostNode=HRMS&amp;NoCrumbs=yes&amp;PortalKeyStruct=yes");
////                    }, 2000);
////                });
////            } else {
////                String js2 = "javascript:document.getElementById('N_ON_RSLT_VW_ACAD_CAREER').value='UGRD';" +
////                        "javascript:document.getElementById('#ICSearch').click();";
////                ProfileWebView.evaluateJavascript(js2, s -> {
////                });
////                new Handler().postDelayed(() -> {
////                    if (ProfileWebView != null) {
////                        ProfileWebView.findFocus();
////
////                        new Handler().postDelayed(() -> {
////                            String js3 = "javascript:document.getElementById('N_REPORT_WRK_BUTTON').click();";
////                            ProfileWebView.evaluateJavascript(js3, s -> {
////                            });
////                        }, 500);
////                    }
////                }, 500);
////            }
//        }
//    }
}