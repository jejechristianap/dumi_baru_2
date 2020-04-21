package com.fidac.dumi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

//import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.io.InputStream;

public class PdfViewerActivity extends AppCompatActivity {
    private ImageView backIv, picView;
//    private PDFView pdfView;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        backIv = findViewById(R.id.back_pdf);
//        pdfView = findViewById(R.id.pdf_view);
        picView = findViewById(R.id.pdf_image);

        intent = getIntent();
        String pdf = intent.getStringExtra("nameFile");
        if (pdf.equals("skp")){
            picView.setBackgroundResource(R.drawable.pdf_sk);
        } else {
            picView.setBackgroundResource(R.drawable.pdf_sk_rek);
        }


        backIv.setOnClickListener(v -> finish());

    }
}
