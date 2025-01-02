# Webview2File Extension Documentation

## Overview

The **Webview2File** extension allows App Inventor developers to easily convert the content of a WebView or WebViews inside a container into **PDF files** or **images**. The extension is designed for block programming and offers a straightforward way to manage WebView content as files.

---

## Features

- **Convert WebView to PDF**: Save the content of a WebView as a PDF file.
- **Convert WebView to Image**: Save the WebView content as an image in the gallery.
- **Convert All WebViews in a Container to PDF**: Process multiple WebViews within a container and generate individual PDF files.
- **Convert All WebViews in a Container to Images**: Save the content of multiple WebViews as images.
- **Immediate Gallery Update**: Automatically update the gallery when a file is saved.
- **Error Handling**: Triggers events if errors occur during file generation.

---

## Blocks Overview

### Functions

#### 1. **ConvertToPDF**
Converts the content of a single WebView to a PDF file.

**Parameters**:
- `WebViewer`: The WebView component to process.
- `fileName`: The name of the PDF file.

---

#### 2. **ConvertToImage**
Converts the content of a single WebView to an image and saves it in the gallery.

**Parameters**:
- `WebViewer`: The WebView component to process.
- `dirName`: The name of the folder in the gallery where the image will be saved.

---

#### 3. **ConvertContainerToPDF**
Converts the content of all WebViews within a container (e.g., HorizontalArrangement or VerticalArrangement) to PDF files.

**Parameters**:
- `Container`: The container holding WebViews.
- `fileName`: The base name for the generated PDF files. Each WebView will be saved as a separate PDF file with an index appended.

---

#### 4. **ConvertContainerToImages**
Converts the content of all WebViews within a container to images and saves them in the gallery.

**Parameters**:
- `Container`: The container holding WebViews.
- `dirName`: The folder name in the gallery where the images will be saved.

---

### Events

#### 1. **OnImageSaved**
Triggered when an image is successfully saved to the gallery.

**Parameter**:
- `uri`: The URI of the saved image.

---

#### 2. **OnPDFGenerated**
Triggered when a PDF file is successfully generated.

**Parameter**:
- `message`: A message indicating the success of the operation.

---

#### 3. **OnImageGenerated**
Triggered when images from a container are successfully generated.

**Parameter**:
- `filePath`: The path to the saved images.

---

#### 4. **OnError**
Triggered when an error occurs during any operation.

**Parameter**:
- `message`: A description of the error.

---

## Usage Guide

### Converting a Single WebView to PDF
1. Drag and drop a WebViewer component into your screen.
2. Set up the WebViewer to load your desired content.
3. Use the **ConvertToPDF** block to generate a PDF:
    ```plaintext
    call Webview2File.ConvertToPDF(WebViewer1, "MyDocument.pdf")
    ```

---

### Converting a Single WebView to an Image
1. Set up your WebViewer as described above.
2. Use the **ConvertToImage** block:
    ```plaintext
    call Webview2File.ConvertToImage(WebViewer1, "MyImages")
    ```

---

### Converting Multiple WebViews in a Container to PDFs
1. Place multiple WebViewer components inside a container (e.g., HorizontalArrangement).
2. Use the **ConvertContainerToPDF** block:
    ```plaintext
    call Webview2File.ConvertContainerToPDF(HorizontalArrangement1, "ContainerPDF")
    ```

---

### Converting Multiple WebViews in a Container to Images
1. Set up your WebView components inside a container as described above.
2. Use the **ConvertContainerToImages** block:
    ```plaintext
    call Webview2File.ConvertContainerToImages(HorizontalArrangement1, "GalleryFolder")
    ```

---

## Example Workflow

### Scenario: Saving a WebPage as a PDF
1. Drag a WebViewer component into your app.
2. Load the webpage:
    ```plaintext
    set WebViewer1.Url to "https://example.com"
    ```
3. Convert the content to a PDF:
    ```plaintext
    call Webview2File.ConvertToPDF(WebViewer1, "ExamplePage.pdf")
    ```

---

## Notes

1. **Permissions**: The extension requires storage access to save files. Make sure your app includes the necessary permissions.
2. **File Names**: Ensure unique file names to avoid overwriting existing files.
3. **Gallery Update**: Files saved as images are immediately visible in the gallery.

---

## Support

For additional help or questions, please visit the community forum or contact support. Happy coding! 🚀
