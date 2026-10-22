package com.bumbumapps.documentscannerfastest.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumbumapps.documentscannerfastest.R;
import com.bumbumapps.documentscannerfastest.utils.AdsUtils;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * PDF preview using the platform {@link PdfRenderer}. Replaces the JCenter-only android-pdf-viewer
 * (its bundled pdfium .so files are not 16 KB page aligned, which Play rejects for targetSdk 35+).
 */
public class PDFViewerActivity extends BaseActivity {
    private static final String TAG = "PDFViewerActivity";
    private RecyclerView pdfView;
    protected Uri pdf_uri;
    protected String title;
    protected TextView tv_page;
    protected TextView tv_title;
    private android.view.ViewGroup adView;

    private ParcelFileDescriptor descriptor;
    private PdfRenderer renderer;
    private final ExecutorService renderExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final LruCache<Integer, Bitmap> pageCache = new LruCache<Integer, Bitmap>(
            (int) Math.min(Runtime.getRuntime().maxMemory() / 8, 64L * 1024 * 1024)) {
        @Override
        protected int sizeOf(Integer key, Bitmap value) {
            return value.getByteCount();
        }
    };

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_pdfviewer);
        init();
    }

    private void init() {
        adView = findViewById(R.id.adView);
        tv_title = findViewById(R.id.tv_title);
        tv_page = findViewById(R.id.tv_page);
        pdfView = findViewById(R.id.pdfView);
        pdfView.setBackgroundColor(getResources().getColor(R.color.bg_color));
        title = getIntent().getStringExtra("title");
        tv_title.setText(title);
        String path = getIntent().getStringExtra("pdf_path");
        AdsUtils.showGoogleBannerAd(this, adView);
        findViewById(R.id.iv_back).setOnClickListener(view -> onBackPressed());
        if (path == null) {
            Toast.makeText(this, R.string.app_name, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        pdf_uri = Uri.parse(path);
        loadPDF(pdf_uri);
    }

    private void loadPDF(Uri uri) {
        try {
            descriptor = getContentResolver().openFileDescriptor(uri, "r");
            if (descriptor == null) {
                throw new IOException("No descriptor for " + uri);
            }
            renderer = new PdfRenderer(descriptor);
        } catch (IOException | SecurityException | IllegalArgumentException e) {
            Log.e(TAG, "Cannot open PDF", e);
            Toast.makeText(this, "Cannot open PDF", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        final int pageCount = renderer.getPageCount();
        updatePageLabel(0, pageCount);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        pdfView.setLayoutManager(lm);
        pdfView.setAdapter(new PageAdapter(pageCount));
        pdfView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int first = lm.findFirstVisibleItemPosition();
                if (first != RecyclerView.NO_POSITION) {
                    updatePageLabel(first, pageCount);
                }
            }
        });
    }

    private void updatePageLabel(int index, int count) {
        tv_page.setText(String.format(Locale.US, "%d / %d", count == 0 ? 0 : index + 1, count));
    }

    private Bitmap renderPage(int index, int width) {
        synchronized (this) {
            if (renderer == null) {
                return null;
            }
            PdfRenderer.Page page = renderer.openPage(index);
            try {
                int height = Math.max(1, (int) ((long) width * page.getHeight() / Math.max(1, page.getWidth())));
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                bitmap.eraseColor(Color.WHITE);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                return bitmap;
            } finally {
                page.close();
            }
        }
    }

    @Override
    protected void onDestroy() {
        renderExecutor.shutdownNow();
        synchronized (this) {
            if (renderer != null) {
                renderer.close();
                renderer = null;
            }
        }
        if (descriptor != null) {
            try {
                descriptor.close();
            } catch (IOException ignored) {
            }
        }
        super.onDestroy();
    }

    private class PageAdapter extends RecyclerView.Adapter<PageAdapter.Holder> {
        private final int count;

        PageAdapter(int count) {
            this.count = count;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ImageView iv = new ImageView(parent.getContext());
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = 12;
            iv.setLayoutParams(lp);
            iv.setAdjustViewBounds(true);
            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
            return new Holder(iv);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            ImageView iv = (ImageView) holder.itemView;
            iv.setTag(position);
            Bitmap cached = pageCache.get(position);
            if (cached != null) {
                iv.setImageBitmap(cached);
                return;
            }
            iv.setImageDrawable(null);
            iv.setMinimumHeight(pdfView.getWidth());
            final int width = Math.max(1, pdfView.getWidth() > 0 ? pdfView.getWidth() : getResources().getDisplayMetrics().widthPixels);
            renderExecutor.execute(() -> {
                Bitmap bmp;
                try {
                    bmp = renderPage(position, width);
                } catch (RuntimeException e) {
                    Log.e(TAG, "Cannot render page " + position, e);
                    return;
                }
                if (bmp == null) {
                    return;
                }
                mainHandler.post(() -> {
                    pageCache.put(position, bmp);
                    if (Integer.valueOf(position).equals(iv.getTag())) {
                        iv.setMinimumHeight(0);
                        iv.setImageBitmap(bmp);
                    }
                });
            });
        }

        @Override
        public int getItemCount() {
            return count;
        }

        class Holder extends RecyclerView.ViewHolder {
            Holder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }
}
