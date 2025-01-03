package com.bosonshiggs.webview2file;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.os.Looper;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.view.View;
import android.webkit.WebView;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.ViewGroup;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.AndroidViewComponent;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.EventDispatcher;

import java.io.OutputStream;
import android.os.Handler;

@DesignerComponent(version = 24, versionName = "1.23", description = "Extension to convert WebView content to PDF and image.", iconName = "icon.png")
public class Webview2File extends AndroidNonvisibleComponent {

	private final ComponentContainer container;
	private final Context context;

	public Webview2File(ComponentContainer container) {
		super(container.$form());
		this.container = container;
		this.context = (Context) container.$context();
	}

	@SimpleFunction(description = "Converts the content of the WebView to a PDF file.")
	public void ConvertToPDF(AndroidViewComponent webViewer, String fileName) {
		try {
			WebView webView = (WebView) webViewer.getView();

			PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
			PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(fileName);

			PrintAttributes.Builder builder = new PrintAttributes.Builder();
			builder.setMediaSize(PrintAttributes.MediaSize.ISO_A4);
			builder.setResolution(new PrintAttributes.Resolution("id", "name", 600, 600));
			builder.setMinMargins(PrintAttributes.Margins.NO_MARGINS);

			printManager.print(fileName, printAdapter, builder.build());
			OnPDFGenerated("PDF conversion initiated. Check your printer or PDF storage.");
		} catch (Exception e) {
			OnError("Error converting to PDF: " + e.getMessage());
		}
	}

	@SimpleFunction(description = "Converts the content of the WebView to an image and saves it to the gallery.")
	public void ConvertToImage(AndroidViewComponent webViewer, String dirName) {
		WebView webView = (WebView) webViewer.getView();
		int webHeight = webViewer.Height();
		int webWidth = webViewer.Width();
		webViewer.Height(webView.getContentHeight());

		new Handler(Looper.getMainLooper()).postDelayed(() -> {
			try {
				// Defina o tamanho do bitmap baseado no tamanho do WebView
				int width = webView.getMeasuredWidth();
				int height = webView.getMeasuredHeight();

				if (height <= 0 || width <= 0) {
					OnError("WebView dimensions are invalid.");
					return;
				}

				Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(bitmap);
				webView.draw(canvas);

				String fileName = "WebViewCapture_" + System.currentTimeMillis() + ".png";
				String relativePath = "Pictures/" + dirName;

				ContentResolver resolver = context.getContentResolver();
				ContentValues values = new ContentValues();
				values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
				values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
				values.put(MediaStore.Images.Media.RELATIVE_PATH, relativePath);

				Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
				if (imageUri != null) {
					try (OutputStream outputStream = resolver.openOutputStream(imageUri)) {
						if (outputStream != null) {
							bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
						}
					}
					MediaScannerConnection.scanFile(
							context,
							new String[] {
									Environment.getExternalStorageDirectory() + "/" + relativePath + "/" + fileName },
							null,
							(path, uri) -> {
								container.$form().runOnUiThread(() -> {
									OnImageSaved(uri != null ? uri.toString() : "null");
								});
							});
				} else {
					OnError("Failed to save image to gallery.");
				}
			} catch (Exception e) {
				OnError("Error saving image: " + e.getMessage());
			}
			webViewer.Height(webHeight);
			webViewer.Width(webWidth);
		}, 200);
	}

	@SimpleFunction(description = "Converts the content of all WebViews within a container to images and saves them to the gallery.")
	public void ConvertContainerToImages(AndroidViewComponent layout, String dirName) {
		ViewGroup containerView = (ViewGroup) layout.getView();
		try {
			boolean imageGenerated = false;

			for (int i = 0; i < containerView.getChildCount(); i++) {
				View childView = containerView.getChildAt(i);
				if (childView instanceof WebView) {
					WebView webView = (WebView) childView;

					int width = webView.getWidth();
					int height = webView.getContentHeight();
					if (height <= 0 || width <= 0) {
						OnError("WebView dimensions are invalid for view at index " + i);
						continue;
					}

					Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
					Canvas canvas = new Canvas(bitmap);
					webView.draw(canvas);

					String fileName = "WebViewCapture_" + i + "_" + System.currentTimeMillis() + ".png";
					String relativePath = "Pictures/" + dirName;

					ContentResolver resolver = context.getContentResolver();
					ContentValues values = new ContentValues();
					values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
					values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
					values.put(MediaStore.Images.Media.RELATIVE_PATH, relativePath);

					Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
					if (imageUri != null) {
						try (OutputStream outputStream = resolver.openOutputStream(imageUri)) {
							if (outputStream != null) {
								bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
							}
						}

						MediaScannerConnection.scanFile(
								context,
								new String[] { Environment.getExternalStorageDirectory() + "/" + relativePath + "/"
										+ fileName },
								null,
								(path, uri) -> {
									container.$form().runOnUiThread(() -> {
										OnImageSaved(uri != null ? uri.toString() : "null");
									});
								});
						imageGenerated = true;
					} else {
						OnError("Failed to save image for WebView at index " + i);
					}
				}
			}

			if (imageGenerated) {
				OnImageGenerated("Images generated and saved to gallery for all WebViews in the container.");
			} else {
				OnError("No WebView found in the container.");
			}
		} catch (Exception e) {
			OnError("Error converting container to images: " + e.getMessage());
		}
	}

	@SimpleFunction(description = "Converts the content of all WebViews within a container to a PDF file.")
	public void ConvertContainerToPDF(AndroidViewComponent layout, String fileName) {
		try {
			ViewGroup containerView = (ViewGroup) layout.getView();
			boolean pdfGenerated = false;

			for (int i = 0; i < containerView.getChildCount(); i++) {
				View childView = containerView.getChildAt(i);
				if (childView instanceof WebView) {
					WebView webView = (WebView) childView;

					PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
					PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(fileName);

					PrintAttributes.Builder builder = new PrintAttributes.Builder();
					builder.setMediaSize(PrintAttributes.MediaSize.ISO_A4);
					builder.setResolution(new PrintAttributes.Resolution("id", "name", 600, 600));
					builder.setMinMargins(PrintAttributes.Margins.NO_MARGINS);

					printManager.print(fileName + "_" + i, printAdapter, builder.build());
					pdfGenerated = true;
				}
			}

			if (pdfGenerated) {
				OnPDFGenerated("PDF generation initiated for all WebViews in the container.");
			} else {
				OnError("No WebView found in the container.");
			}
		} catch (Exception e) {
			OnError("Error converting container to PDF: " + e.getMessage());
		}
	}

	@SimpleEvent(description = "Triggered when the image is successfully saved to the gallery.")
	public void OnImageSaved(String uri) {
		EventDispatcher.dispatchEvent(this, "OnImageSaved", uri);
	}

	@SimpleEvent(description = "Triggered when a PDF is generated.")
	public void OnPDFGenerated(String message) {
		EventDispatcher.dispatchEvent(this, "OnPDFGenerated", message);
	}

	@SimpleEvent(description = "Triggered when an image is generated.")
	public void OnImageGenerated(String filePath) {
		EventDispatcher.dispatchEvent(this, "OnImageGenerated", filePath);
	}

	@SimpleEvent(description = "Triggered when an error occurs.")
	public void OnError(String message) {
		EventDispatcher.dispatchEvent(this, "OnError", message);
	}
}
