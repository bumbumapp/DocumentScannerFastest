package com.bumbumapps.documentscannerfastest.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.bumbumapps.documentscannerfastest.AdsHelper;
import com.bumbumapps.documentscannerfastest.R;
import com.bumbumapps.documentscannerfastest.main_utils.Constant;

public class AdsUtils {

    /** Loads an anchored adaptive banner into {@code container} once consent allows ads. */
    public static void showGoogleBannerAd(Context context, ViewGroup container) {
        if (!(context instanceof Activity) || container == null) {
            return;
        }
        Activity activity = (Activity) context;
        container.setVisibility(View.VISIBLE);
        AdsHelper.gatherConsent(activity, () -> {
            AdsHelper.loadBanner(activity, container, activity.getString(R.string.admob_banner_id));
            AdsHelper.preloadInterstitial(activity, activity.getString(R.string.intersitial_id));
        });
    }

    public static void loadGoogleInterstitialAd(Context context, Activity activity) {
        AdsHelper.preloadInterstitial(context, context.getString(R.string.intersitial_id));
    }

    /**
     * Shows the preloaded interstitial (frequency capped by AdsHelper: one per 8 min) and then continues the
     * navigation; navigation also continues when no ad is shown or it fails to show.
     */
    public static void showGoogleInterstitialAd(Activity activity, boolean isShowAd) {
        if (isShowAd) {
            AdsHelper.showInterstitialIfReady(activity, activity.getString(R.string.intersitial_id),
                    () -> jumpNextActivity(activity));
        } else {
            jumpNextActivity(activity);
        }
    }

    /** Package-scoped broadcast: receivers are registered RECEIVER_NOT_EXPORTED. */
    static Intent internalIntent(String packageName, String suffix) {
        Intent intent = new Intent(packageName + suffix);
        intent.setPackage(packageName);
        return intent;
    }

    private static void sendInternal(Activity activity, String suffix) {
        activity.sendBroadcast(internalIntent(activity.getPackageName(), suffix));
    }

    public static void jumpNextActivity(Activity activity) {
        if (Constant.IdentifyActivity.equals("MainActivity")) {
            sendInternal(activity, ".MainActivity");
        }else if (Constant.IdentifyActivity.equals("MainGalleryActivity")) {
            sendInternal(activity, ".MainGalleryActivity");
        } else if (Constant.IdentifyActivity.equals("ScannerActivity")) {
            sendInternal(activity, ".ScannerActivity");
        } else if (Constant.IdentifyActivity.equals("GroupDocumentActivity")) {
            sendInternal(activity, ".GroupDocumentActivity");
        } else if (Constant.IdentifyActivity.equals("CropDocumentActivity")) {
            sendInternal(activity, ".CropDocumentActivity");
        } else if (Constant.IdentifyActivity.equals("ScannerGalleryActivity")) {
            sendInternal(activity, ".ScannerGalleryActivity");
        } else if (Constant.IdentifyActivity.equals("CropDocumentActivity2")) {
            sendInternal(activity, ".CropDocumentActivity2");
        } else if (Constant.IdentifyActivity.equals("DocumentEditorActivity_Crop")) {
            sendInternal(activity, ".DocumentEditorActivity_Crop");
        } else if (Constant.IdentifyActivity.equals("CurrentFilterActivity")) {
            sendInternal(activity, ".CurrentFilterActivity");
        } else if (Constant.IdentifyActivity.equals("ScannerActivity_Retake")) {
            sendInternal(activity, ".ScannerActivity_Retake");
        } else if (Constant.IdentifyActivity.equals("SavedDocumentActivity")) {
            sendInternal(activity, ".SavedDocumentActivity");
        } else if (Constant.IdentifyActivity.equals("ScannerActivity_Retake2")) {
            sendInternal(activity, ".ScannerActivity_Retake2");
        } else if (Constant.IdentifyActivity.equals("DocumentEditorActivity_Saved")) {
            sendInternal(activity, ".DocumentEditorActivity_Saved");
        } else if (Constant.IdentifyActivity.equals("SavedEditDocumentActivity")) {
            sendInternal(activity, ".SavedEditDocumentActivity");
        } else if (Constant.IdentifyActivity.equals("DocumentEditorActivity_SavedEdit")) {
            sendInternal(activity, ".DocumentEditorActivity_SavedEdit");
        } else if (Constant.IdentifyActivity.equals("DocumentEditorActivity_SavedEdit2")) {
            sendInternal(activity, ".DocumentEditorActivity_SavedEdit2");
        } else if (Constant.IdentifyActivity.equals("PDFViewerActivity")) {
            sendInternal(activity, ".PDFViewerActivity");
        } else if (Constant.IdentifyActivity.equals("NoteActivity")) {
            sendInternal(activity, ".NoteActivity");
        } else if (Constant.IdentifyActivity.equals("ImageToTextActivity")) {
            sendInternal(activity, ".ImageToTextActivity");
        } else if (Constant.IdentifyActivity.equals("PDFViewerActivity2")) {
            sendInternal(activity, ".PDFViewerActivity2");
        } else if (Constant.IdentifyActivity.equals("DocumentGalleryActivity")) {
            sendInternal(activity, ".DocumentGalleryActivity");
        } else if (Constant.IdentifyActivity.equals("CropDocumentActivity4")) {
            sendInternal(activity, ".CropDocumentActivity4");
        } else if (Constant.IdentifyActivity.equals("ScannerActivity2")) {
            sendInternal(activity, ".ScannerActivity2");
        } else if (Constant.IdentifyActivity.equals("SavedDocumentPreviewActivity")) {
            sendInternal(activity, ".SavedDocumentPreviewActivity");
        } else if (Constant.IdentifyActivity.equals("IDCardPreviewActivity")) {
            sendInternal(activity, ".IDCardPreviewActivity");
        } else if (Constant.IdentifyActivity.equals("SavedEditDocumentActivity3")) {
            sendInternal(activity, ".SavedEditDocumentActivity3");
        } else if (Constant.IdentifyActivity.equals("UcropActivity")) {
            sendInternal(activity, ".UcropActivity");
        } else if (Constant.IdentifyActivity.equals("DocumentEditorActivity_Scanner")) {
            sendInternal(activity, ".DocumentEditorActivity_Scanner");
        } else if (Constant.IdentifyActivity.equals("IDCardPreviewActivity2")) {
            sendInternal(activity, ".IDCardPreviewActivity2");
        } else if (Constant.IdentifyActivity.equals("DocumentEditorActivity_IDCard")) {
            sendInternal(activity, ".DocumentEditorActivity_IDCard");
        } else if (Constant.IdentifyActivity.equals("IDCardGalleryActivity")) {
            sendInternal(activity, ".IDCardGalleryActivity");
        } else if (Constant.IdentifyActivity.equals("DocumentEditorActivity_SavedPreview")) {
            sendInternal(activity, ".DocumentEditorActivity_SavedPreview");
        } else if (Constant.IdentifyActivity.equals("PDFViewerActivity_Preview")) {
            sendInternal(activity, ".PDFViewerActivity_Preview");
        } else if (Constant.IdentifyActivity.equals("NoteActivity_Preview")) {
            sendInternal(activity, ".NoteActivity_Preview");
        } else if (Constant.IdentifyActivity.equals("ImageToTextActivity_Preview")) {
            sendInternal(activity, ".ImageToTextActivity_Preview");
        }
    }
}
