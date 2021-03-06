/*
 * Copyright (C) 2012 - 2013 jonas.oreland@gmail.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.runnerup.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.runnerup.R;

@TargetApi(Build.VERSION_CODES.FROYO)
public class AboutPreference extends DialogPreference {

    private Context context;

    public AboutPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context);
    }

    public AboutPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public static boolean isGooglePlayServicesAvailable(Context context) {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;
    }

    private boolean isPlayPref() {
        return this.getKey() != null && this.getKey().contentEquals("googleplayserviceslegalnotices");
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            if (!isPlayPref()) {
                try {
                    Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
                    context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        if (!isPlayPref()) {
            WebView wv = (WebView) view.findViewById(R.id.web_view1);
            wv.loadUrl("file:///android_asset/about.html");
        }
    }

    private void init(Context context) {
        if (isPlayPref()) {
            setNegativeButtonText(null);
            CharSequence msg = GoogleApiAvailability.getInstance().getOpenSourceSoftwareLicenseInfo(this
                    .getContext());
            if (msg != null) {
                this.setDialogMessage(msg);
            }
        } else {
            try {
                PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                this.setDialogTitle(context.getString(R.string.About_RunnerUp)+" v" + pInfo.versionName);
            } catch (NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (isGooglePlayServicesAvailable(context)) {
                setNegativeButtonText(context.getString(R.string.OK));
                setPositiveButtonText(context.getString(R.string.Rate_RunnerUp));
            } else {
                setNegativeButtonText(null);
            }
            setDialogLayoutResource(R.layout.whatsnew);
        }
    }
}
