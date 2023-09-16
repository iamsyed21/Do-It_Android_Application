package com.the21codes.do_it;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class licenses_activity extends AppCompatActivity {

    AdRequest adRequest;
    InterstitialAd mInterstitialAd;
    SharedPreferences adsCounterDetails;
    SharedPreferences.Editor adsCounterDetailsEditor;
    int adCounter;
    TextView licenseDescription, licenseHeading;
    private int type=-1;

    String license1 = "DO WHAT THE F**K YOU WANT TO PUBLIC LICENSE Version 2, December 2004\n" +
            "\n" +
            "Copyright (C) 2014 Bruce Lee <bruceinpeking#gmail.com>\n" +
            "\n" +
            "Everyone is permitted to copy and distribute verbatim or modified copies of this license document, and changing it is allowed as long as the name is changed.\n" +
            "\n" +
            "        DO WHAT THE F**K YOU WANT TO PUBLIC LICENSE\n" +
            "TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION\n" +
            "\n" +
            "You just DO WHAT THE F**K YOU WANT TO.";

    String ApacheLicenseVersion2 = "Apache License\n" +
            "Version 2.0, January 2004\n" +
            "http://www.apache.org/licenses/\n" +
            "\n" +
            "TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION\n" +
            "\n" +
            "1. Definitions.\n" +
            "\n" +
            "\"License\" shall mean the terms and conditions for use, reproduction, and distribution as defined by Sections 1 through 9 of this document.\n" +
            "\n" +
            "\"Licensor\" shall mean the copyright owner or entity authorized by the copyright owner that is granting the License.\n" +
            "\n" +
            "\"Legal Entity\" shall mean the union of the acting entity and all other entities that control, are controlled by, or are under common control with that entity. For the purposes of this definition, \"control\" means (i) the power, direct or indirect, to cause the direction or management of such entity, whether by contract or otherwise, or (ii) ownership of fifty percent (50%) or more of the outstanding shares, or (iii) beneficial ownership of such entity.\n" +
            "\n" +
            "\"You\" (or \"Your\") shall mean an individual or Legal Entity exercising permissions granted by this License.\n" +
            "\n" +
            "\"Source\" form shall mean the preferred form for making modifications, including but not limited to software source code, documentation source, and configuration files.\n" +
            "\n" +
            "\"Object\" form shall mean any form resulting from mechanical transformation or translation of a Source form, including but not limited to compiled object code, generated documentation, and conversions to other media types.\n" +
            "\n" +
            "\"Work\" shall mean the work of authorship, whether in Source or Object form, made available under the License, as indicated by a copyright notice that is included in or attached to the work (an example is provided in the Appendix below).\n" +
            "\n" +
            "\"Derivative Works\" shall mean any work, whether in Source or Object form, that is based on (or derived from) the Work and for which the editorial revisions, annotations, elaborations, or other modifications represent, as a whole, an original work of authorship. For the purposes of this License, Derivative Works shall not include works that remain separable from, or merely link (or bind by name) to the interfaces of, the Work and Derivative Works thereof.\n" +
            "\n" +
            "\"Contribution\" shall mean any work of authorship, including the original version of the Work and any modifications or additions to that Work or Derivative Works thereof, that is intentionally submitted to Licensor for inclusion in the Work by the copyright owner or by an individual or Legal Entity authorized to submit on behalf of the copyright owner. For the purposes of this definition, \"submitted\" means any form of electronic, verbal, or written communication sent to the Licensor or its representatives, including but not limited to communication on electronic mailing lists, source code control systems, and issue tracking systems that are managed by, or on behalf of, the Licensor for the purpose of discussing and improving the Work, but excluding communication that is conspicuously marked or otherwise designated in writing by the copyright owner as \"Not a Contribution.\"\n" +
            "\n" +
            "\"Contributor\" shall mean Licensor and any individual or Legal Entity on behalf of whom a Contribution has been received by Licensor and subsequently incorporated within the Work.\n" +
            "\n" +
            "2. Grant of Copyright License. Subject to the terms and conditions of this License, each Contributor hereby grants to You a perpetual, worldwide, non-exclusive, no-charge, royalty-free, irrevocable copyright license to reproduce, prepare Derivative Works of, publicly display, publicly perform, sublicense, and distribute the Work and such Derivative Works in Source or Object form.\n" +
            "\n" +
            "3. Grant of Patent License. Subject to the terms and conditions of this License, each Contributor hereby grants to You a perpetual, worldwide, non-exclusive, no-charge, royalty-free, irrevocable (except as stated in this section) patent license to make, have made, use, offer to sell, sell, import, and otherwise transfer the Work, where such license applies only to those patent claims licensable by such Contributor that are necessarily infringed by their Contribution(s) alone or by combination of their Contribution(s) with the Work to which such Contribution(s) was submitted. If You institute patent litigation against any entity (including a cross-claim or counterclaim in a lawsuit) alleging that the Work or a Contribution incorporated within the Work constitutes direct or contributory patent infringement, then any patent licenses granted to You under this License for that Work shall terminate as of the date such litigation is filed.\n" +
            "\n" +
            "4. Redistribution. You may reproduce and distribute copies of the Work or Derivative Works thereof in any medium, with or without modifications, and in Source or Object form, provided that You meet the following conditions:\n" +
            "\n" +
            "You must give any other recipients of the Work or Derivative Works a copy of this License; and\n" +
            "You must cause any modified files to carry prominent notices stating that You changed the files; and\n" +
            "You must retain, in the Source form of any Derivative Works that You distribute, all copyright, patent, trademark, and attribution notices from the Source form of the Work, excluding those notices that do not pertain to any part of the Derivative Works; and\n" +
            "If the Work includes a \"NOTICE\" text file as part of its distribution, then any Derivative Works that You distribute must include a readable copy of the attribution notices contained within such NOTICE file, excluding those notices that do not pertain to any part of the Derivative Works, in at least one of the following places: within a NOTICE text file distributed as part of the Derivative Works; within the Source form or documentation, if provided along with the Derivative Works; or, within a display generated by the Derivative Works, if and wherever such third-party notices normally appear. The contents of the NOTICE file are for informational purposes only and do not modify the License. You may add Your own attribution notices within Derivative Works that You distribute, alongside or as an addendum to the NOTICE text from the Work, provided that such additional attribution notices cannot be construed as modifying the License.\n" +
            "\n" +
            "You may add Your own copyright statement to Your modifications and may provide additional or different license terms and conditions for use, reproduction, or distribution of Your modifications, or for any such Derivative Works as a whole, provided Your use, reproduction, and distribution of the Work otherwise complies with the conditions stated in this License.\n" +
            "5. Submission of Contributions. Unless You explicitly state otherwise, any Contribution intentionally submitted for inclusion in the Work by You to the Licensor shall be under the terms and conditions of this License, without any additional terms or conditions. Notwithstanding the above, nothing herein shall supersede or modify the terms of any separate license agreement you may have executed with Licensor regarding such Contributions.\n" +
            "\n" +
            "6. Trademarks. This License does not grant permission to use the trade names, trademarks, service marks, or product names of the Licensor, except as required for reasonable and customary use in describing the origin of the Work and reproducing the content of the NOTICE file.\n" +
            "\n" +
            "7. Disclaimer of Warranty. Unless required by applicable law or agreed to in writing, Licensor provides the Work (and each Contributor provides its Contributions) on an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied, including, without limitation, any warranties or conditions of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A PARTICULAR PURPOSE. You are solely responsible for determining the appropriateness of using or redistributing the Work and assume any risks associated with Your exercise of permissions under this License.\n" +
            "\n" +
            "8. Limitation of Liability. In no event and under no legal theory, whether in tort (including negligence), contract, or otherwise, unless required by applicable law (such as deliberate and grossly negligent acts) or agreed to in writing, shall any Contributor be liable to You for damages, including any direct, indirect, special, incidental, or consequential damages of any character arising as a result of this License or out of the use or inability to use the Work (including but not limited to damages for loss of goodwill, work stoppage, computer failure or malfunction, or any and all other commercial damages or losses), even if such Contributor has been advised of the possibility of such damages.\n" +
            "\n" +
            "9. Accepting Warranty or Additional Liability. While redistributing the Work or Derivative Works thereof, You may choose to offer, and charge a fee for, acceptance of support, warranty, indemnity, or other liability obligations and/or rights consistent with this License. However, in accepting such obligations, You may act only on Your own behalf and on Your sole responsibility, not on behalf of any other Contributor, and only if You agree to indemnify, defend, and hold each Contributor harmless for any liability incurred by, or claims asserted against, such Contributor by reason of your accepting any such warranty or additional liability.\n" +
            "\n" +
            "END OF TERMS AND CONDITIONS";

    String MIT_LICENSE = "The MIT License (MIT)\n" +
            "\n" +
            "Copyright (c) 2014 Daimajia\n" +
            "\n" +
            "Permission is hereby granted, free of charge, to any person obtaining a copy\n" +
            "of this software and associated documentation files (the \"Software\"), to deal\n" +
            "in the Software without restriction, including without limitation the rights\n" +
            "to use, copy, modify, merge, publish, distribute, sublicense, and/or sell\n" +
            "copies of the Software, and to permit persons to whom the Software is\n" +
            "furnished to do so, subject to the following conditions:\n" +
            "\n" +
            "The above copyright notice and this permission notice shall be included in all\n" +
            "copies or substantial portions of the Software.\n" +
            "\n" +
            "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\n" +
            "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\n" +
            "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE\n" +
            "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\n" +
            "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\n" +
            "OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE\n" +
            "SOFTWARE.";

    String ISC = "ISC License\n" +
            "\n" +
            "Copyright (c) 2017 Dion Segijn\n" +
            "\n" +
            "Permission to use, copy, modify, and/or distribute this software for any\n" +
            "purpose with or without fee is hereby granted, provided that the above\n" +
            "copyright notice and this permission notice appear in all copies.\n" +
            "\n" +
            "THE SOFTWARE IS PROVIDED \"AS IS\" AND THE AUTHOR DISCLAIMS ALL WARRANTIES\n" +
            "WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF\n" +
            "MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR\n" +
            "ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES\n" +
            "WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN\n" +
            "ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF\n" +
            "OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licenses_activity);
        licenseHeading = (TextView) findViewById(R.id.licenseHeading);
        licenseDescription = (TextView) findViewById(R.id.licenseDescription);
        getSupportActionBar().setTitle("Open Source Licenses We Use");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if(intent != null){
            type = intent.getIntExtra("document", -1);
        }
        adsCounterDetails = getSharedPreferences("UserLoginAndNameDetails", MODE_PRIVATE);
        adCounter = adsCounterDetails.getInt("Licenses_page_ad", 0);
        adsCounterDetailsEditor = adsCounterDetails.edit();

        if(isDeviceConnected(getApplicationContext())) {
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });


            AdView adView = findViewById(R.id.adView);
            ;
            adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
            if (adCounter <= 1) {
                adCounter++;
                adsCounterDetailsEditor.putInt("Licenses_page_ad", adCounter);
                adsCounterDetailsEditor.apply();

            } else {
                adCounter = 0;
                adsCounterDetailsEditor.putInt("Licenses_page_ad", 0);
                adsCounterDetailsEditor.apply();
                showInterstitialAd();
            }
        }
        switch (type){
            case 0:
                licenseHeading.setText("Circle Progress by lzyzsd");
                licenseDescription.setText(license1);
                break;
            case 1:
                licenseHeading.setText("MP Android Chart by PhilJay");
                licenseDescription.setText(ApacheLicenseVersion2);
                break;
            case 2:
                licenseHeading.setText("NotifyMe by jakebonk");
                licenseDescription.setText(ApacheLicenseVersion2);
                break;
            case 3:
                licenseHeading.setText("NumberProgressBar by daimajia");
                licenseDescription.setText(MIT_LICENSE);
                break;
            case 4:
                licenseHeading.setText("TapTargetView by KeepSafe");
                licenseDescription.setText(ApacheLicenseVersion2);
                break;
            case 5:
                licenseHeading.setText("AppIntro by AppIntro");
                licenseDescription.setText(ApacheLicenseVersion2);
                break;
            case 6:
                licenseHeading.setText("picasso by square");
                licenseDescription.setText(ApacheLicenseVersion2);
                break;
            case 7:
                licenseHeading.setText("CircleImageView hdodenhof");
                licenseDescription.setText(ApacheLicenseVersion2);
                break;
            case 8:
                licenseHeading.setText("smart-app-rate by codemybrainsout");
                licenseDescription.setText(ApacheLicenseVersion2);
                break;
            case 9:
                licenseHeading.setText("Konfetti by DanielMartinus");
                licenseDescription.setText(ISC);
                break;
            default:
                licenseHeading.setText("Licenses");
                licenseDescription.setText("All the licenses of the libraries that are used in our application will be shown here! :)");
        }

        adsCounterDetails = getSharedPreferences("UserLoginAndNameDetails", MODE_PRIVATE);
        adCounter = adsCounterDetails.getInt("Licenses_page_ad", 0);
        adsCounterDetailsEditor = adsCounterDetails.edit();


    }

    private static boolean isDeviceConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo!= null && networkInfo.isConnected()){
            return true;
        }
        return false;
    }

    private void showInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-6952672354974833/3121832609", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;

                        mInterstitialAd.show(licenses_activity.this);
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdClicked() {
                                super.onAdClicked();
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                mInterstitialAd = null;
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                mInterstitialAd = null;
                                super.onAdFailedToShowFullScreenContent(adError);
                            }

                            @Override
                            public void onAdImpression() {
                                super.onAdImpression();
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                super.onAdShowedFullScreenContent();
                                mInterstitialAd = null;
                            }
                        });

                    }



                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        mInterstitialAd = null;
                    }
                });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}