import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.highgui.HighGui;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    public static void main(String[] args) {
        // Open webcam
        VideoCapture capture = new VideoCapture(0);

        if (!capture.isOpened()) {
            System.out.println("Error: Could not open video stream.");
            return;
        }

        Mat frame = new Mat();
        while (capture.read(frame)) {
            // 1. Pre-processing
            Mat processedFrame = preProcess(frame);

            // 2. Square Detection
            Mat squares = detectSquares(processedFrame);

            // 3. Color Extraction and Classification
            extractColors(squares, frame);

            // Display the processed frame
            HighGui.imshow("Rubik's Cube Detection", frame);
            if (HighGui.waitKey(1) == 27) { // Exit if 'Esc' is pressed
                break;
            }
        }
        capture.release();
    }

    private static Mat preProcess(Mat frame) {
        // Convert to grayscale
        Mat gray = new Mat();
        Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);

        // Denoising with Gaussian Blur
        Mat blurred = new Mat();
        Imgproc.GaussianBlur(gray, blurred, new Size(5, 5), 0);

        // Edge detection using Canny
        Mat edges = new Mat();
        Imgproc.Canny(blurred, edges, 50, 150);

        // Dilation to make edges more pronounced
        Mat dilated = new Mat();
        Imgproc.dilate(edges, dilated, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3)));

        return dilated;
    }

    private static Mat detectSquares(Mat processedFrame) {
        // Find contours
        Mat contoursImage = new Mat();
        processedFrame.copyTo(contoursImage);
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(contoursImage, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        // Filter out non-square contours
        Mat squares = new Mat(contoursImage.size(), CvType.CV_8UC3, new Scalar(0, 0, 0));
        for (MatOfPoint contour : contours) {
            MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
            double perimeter = Imgproc.arcLength(contour2f, true);
            MatOfPoint2f approx = new MatOfPoint2f();
            Imgproc.approxPolyDP(contour2f, approx, 0.02 * perimeter, true);

            // Check if the contour is a square
            if (approx.total() == 4 && Math.abs(Imgproc.contourArea(approx)) > 1000 && Imgproc.isContourConvex(new MatOfPoint(approx.toArray()))) {
                Rect boundingRect = Imgproc.boundingRect(new MatOfPoint(approx.toArray()));
                double aspectRatio = (double) boundingRect.width / boundingRect.height;

                if (aspectRatio >= 0.9 && aspectRatio <= 1.1) {
                    Imgproc.drawContours(squares, java.util.Collections.singletonList(new MatOfPoint(approx.toArray())), -1, new Scalar(0, 255, 0), 2);
                }
            }
        }
        return squares;
    }
    private static void extractColors(Mat squares, Mat frame) {
        // Convert squares to 8-bit single-channel format if it's not already
        if (squares.type() != CvType.CV_8UC1) {
            Mat graySquares = new Mat();
            Imgproc.cvtColor(squares, graySquares, Imgproc.COLOR_BGR2GRAY);
            squares.release(); // Release the old Mat
            squares = graySquares; // Update the reference
        }

        // Convert the frame to HSV color space
        Mat hsvFrame = new Mat();
        Imgproc.cvtColor(frame, hsvFrame, Imgproc.COLOR_BGR2HSV);

        // Define color ranges
        Map<String, Scalar[]> colorRanges = new HashMap<>();
        colorRanges.put("Red", new Scalar[]{new Scalar(0, 100, 100), new Scalar(10, 255, 255)});
        colorRanges.put("Green", new Scalar[]{new Scalar(35, 100, 100), new Scalar(85, 255, 255)});
        colorRanges.put("Blue", new Scalar[]{new Scalar(100, 100, 100), new Scalar(130, 255, 255)});
        colorRanges.put("Yellow", new Scalar[]{new Scalar(20, 100, 100), new Scalar(30, 255, 255)});
        colorRanges.put("White", new Scalar[]{new Scalar(0, 0, 200), new Scalar(180, 30, 255)});

        // Add more colors as needed

        // Find contours
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(squares, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // List to store processed bounding boxes
        List<Rect> processedRects = new ArrayList<>();

        for (MatOfPoint contour : contours) {
            Rect boundingRect = Imgproc.boundingRect(contour);

            // Skip if bounding box is too small
            if (boundingRect.width < 10 || boundingRect.height < 10) {
                continue;
            }

            // Check for overlapping bounding boxes
            boolean isOverlap = false;
            for (Rect processedRect : processedRects) {
                if (isOverlap(boundingRect, processedRect)) {
                    isOverlap = true;
                    // Merge bounding boxes
                    processedRect = mergeRects(processedRect, boundingRect);
                    break;
                }
            }

            // If no overlap, add new bounding box
            if (!isOverlap) {
                processedRects.add(boundingRect);
            } else {
                // Update the processed bounding box list
                processedRects = processedRects.stream()
                        .filter(rect -> !isOverlap(rect, boundingRect))
                        .collect(Collectors.toList());
                processedRects.add(boundingRect);
            }

            // Create mask and extract ROI
            Mat mask = Mat.zeros(frame.size(), CvType.CV_8UC1);
            Imgproc.drawContours(mask, Collections.singletonList(contour), -1, new Scalar(255), -1);
            Mat roi = new Mat(frame, boundingRect);
            Mat maskRoi = new Mat(mask, boundingRect);
            Mat maskedRoi = new Mat();
            roi.copyTo(maskedRoi, maskRoi);

            // Calculate average color
            Scalar meanColor = Core.mean(maskedRoi);

            // Convert the color from BGR to HSV for comparison
            Mat hsvMeanColorMat = new Mat(1, 1, CvType.CV_8UC3, new Scalar(meanColor.val[0], meanColor.val[1], meanColor.val[2]));
            Imgproc.cvtColor(hsvMeanColorMat , hsvMeanColorMat, Imgproc.COLOR_BGR2HSV);
            Scalar meanHsvColor = Core.mean(hsvMeanColorMat);

            // Classify the color
            String colorName = classifyColor(meanHsvColor, colorRanges);

            // Draw the bounding rectangle and color info on the frame
            Imgproc.rectangle(frame, boundingRect, new Scalar(0, 255, 0), 2);

            // Ensure text does not overlap
            Point textPosition = new Point(boundingRect.x, boundingRect.y - 10);
            Imgproc.putText(frame, colorName, textPosition, Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 255, 0), 1);
        }
    }

    // Helper method to check if two rectangles overlap
    private static boolean isOverlap(Rect rect1, Rect rect2) {
        return rect1.x < rect2.x + rect2.width &&
                rect1.x + rect1.width > rect2.x &&
                rect1.y < rect2.y + rect2.height &&
                rect1.y + rect1.height > rect2.y;
    }

    // Helper method to merge two rectangles
    private static Rect mergeRects(Rect rect1, Rect rect2) {
        int x = Math.min(rect1.x, rect2.x);
        int y = Math.min(rect1.y, rect2.y);
        int width = Math.max(rect1.x + rect1.width, rect2.x + rect2.width) - x;
        int height = Math.max(rect1.y + rect1.height, rect2.y + rect2.height) - y;
        return new Rect(x, y, width, height);
    }

    // Helper method to classify color
    private static String classifyColor(Scalar color, Map<String, Scalar[]> colorRanges) {
        for (Map.Entry<String, Scalar[]> entry : colorRanges.entrySet()) {
            Scalar lowerBound = entry.getValue()[0];
            Scalar upperBound = entry.getValue()[1];

            if (isColorInRange(color, lowerBound, upperBound)) {
                return entry.getKey();
            }
        }
        return "Unknown";
    }

    // Helper method to check if color is within range
    private static boolean isColorInRange(Scalar color, Scalar lowerBound, Scalar upperBound) {
        return color.val[0] >= lowerBound.val[0] && color.val[0] <= upperBound.val[0] &&
                color.val[1] >= lowerBound.val[1] && color.val[1] <= upperBound.val[1] &&
                color.val[2] >= lowerBound.val[2] && color.val[2] <= upperBound.val[2];
    }

}

