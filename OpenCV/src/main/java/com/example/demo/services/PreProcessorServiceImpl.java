package com.example.demo.services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

@Service
public class PreProcessorServiceImpl implements PreProcessorService {

	private static final String IMGFORMAT = ".png";

	@Override
	public void processImages(String folderPath) {
		File sourceFolder = new File(folderPath);
		File targetFolder = new File(folderPath + "/PreProcessed");
		if (!targetFolder.exists())
			targetFolder.mkdir();
		for (final File image : sourceFolder.listFiles()) {
			if (image.isFile()) {
				Mat matrix = getImageAsMat(image.getAbsolutePath());
				String imageName = image.getName().substring(0, image.getName().indexOf('.'));
				String imagePath = targetFolder.getAbsolutePath() + '/' + imageName + IMGFORMAT;
				try {
					// Rescaling
					Mat preProcessedMatrix = rescaleMat(matrix);
					matrix = preProcessedMatrix;

					// Found Lines and crop
					// Apply Grayscale
					preProcessedMatrix = removeMatColors(preProcessedMatrix);
					// REMOWE NOISE
					preProcessedMatrix = addGaussianBlur(preProcessedMatrix);
					preProcessedMatrix = addMedianBlur(preProcessedMatrix);

					// Binarisation white text on black
					preProcessedMatrix = threesholdMat(preProcessedMatrix, Imgproc.THRESH_BINARY_INV, 23, 11);

					// Dilation
					preProcessedMatrix = dilateMat(preProcessedMatrix);

					// Erosion
//					preProcessedMatrix = erodeMat(preProcessedMatrix);
					// RESTORE WORDS IF WAS damaged
//				preProcessedMatrix = morphologyClose(preProcessedMatrix);

//				Mat imgAdaptiveThreshold = new Mat();
//				Imgproc.adaptiveThreshold(imgMedian, imgAdaptiveThreshold, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C,
//						Imgproc.THRESH_BINARY_INV, 23, 11);
//
//				Mat dilate = new Mat();
//				Mat kernelD = Mat.ones(new Size(5, 5), CvType.CV_8UC1);
//				Imgproc.dilate(imgAdaptiveThreshold, dilate, kernelD, new Point(-1, -1), 1);
//
//				// Vertical lines removal
//				Mat vertical_kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 40));
//				Mat remove_vertical = new Mat();
//				Imgproc.morphologyEx(dilate, remove_vertical, Imgproc.MORPH_OPEN, vertical_kernel, new Point(), 1);
//				List<MatOfPoint> contours = new ArrayList<>();
//				Mat hierarchy = new Mat();
//				Imgproc.findContours(remove_vertical, contours, hierarchy, Imgproc.RETR_EXTERNAL,
//						Imgproc.CHAIN_APPROX_SIMPLE);
//
//				Double avgHeight = contours.stream()
//						.collect(Collectors.averagingInt(contour -> Imgproc.boundingRect(contour).height));
//				for (MatOfPoint point : contours) {
//					Rect rect = Imgproc.boundingRect(point);
//					if (rect.height > avgHeight * 5) {
//						int biggest = contours.indexOf(point);
//						Imgproc.drawContours(dilate, contours, biggest, new Scalar(0, 0, 0), 30);
//					}
//				}
//
					// Horizontal lines removal
//				Mat horizontal_kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(40, 1));
//				Mat remove_horizontal = new Mat();
//				Imgproc.morphologyEx(preProcessedMatrix, remove_horizontal, Imgproc.MORPH_OPEN, horizontal_kernel, new Point(), 1);
//				contours = new ArrayList<>();
//				hierarchy = new Mat();
//				Imgproc.findContours(remove_horizontal, contours, hierarchy, Imgproc.RETR_EXTERNAL,
//						Imgproc.CHAIN_APPROX_SIMPLE);
//
////				Double avgWidth = contours.stream()
////						.collect(Collectors.averagingInt(contour -> Imgproc.boundingRect(contour).width));
//				for (MatOfPoint point : contours) {
//					Rect rect = Imgproc.boundingRect(point);
////					if (rect.width > avgWidth * 5) {
////						int biggest = contours.indexOf(point);
//					System.out.println(point.width() + " " + point.height() + " ");
//						Imgproc.drawContours(matrix, contours, contours.indexOf(point), new Scalar(255, 0, 0), 10);
////					}
//				}
//
//				preProcessedMatrix = matrix;
//				
//				Mat wLocMat = Mat.zeros(erode.size(), erode.type());
//				Core.findNonZero(erode, wLocMat);
//
//				MatOfPoint matOfPoint = new MatOfPoint(wLocMat);
//				MatOfPoint2f mat2f = new MatOfPoint2f();
//				matOfPoint.convertTo(mat2f, CvType.CV_32FC2);
//
//				RotatedRect rotatedRect = Imgproc.minAreaRect(mat2f);
//
//				Point[] vertices = new Point[4];
//				rotatedRect.points(vertices);
//				List<MatOfPoint> boxContours = new ArrayList<>();
//				boxContours.add(new MatOfPoint(vertices));
//
//				Mat erodeVsContours = new Mat();
//				erode.copyTo(erodeVsContours);
//
//				Imgproc.drawContours(erodeVsContours, boxContours, 0, new Scalar(128, 128, 128), 2);
//
//				double resultAngle = rotatedRect.angle;
//				if (rotatedRect.size.width > rotatedRect.size.height) {
//					resultAngle += 90.f;
//				}
//
//				Mat preProcessedImage = deskew(erode, resultAngle);

//				Mat preProcessedImage = erode;
//				Core.bitwise_not(preProcessedImage, preProcessedImage);
					Core.bitwise_not(preProcessedMatrix, preProcessedMatrix);
					saveMatAsImage(preProcessedMatrix, imagePath);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void processImageForLearning(String folderPath) {
		File sourceFolder = new File(folderPath);
		File targetFolder = new File(folderPath + "/PreProcessedd");
		if (!targetFolder.exists())
			targetFolder.mkdir();
		for (final File image : sourceFolder.listFiles()) {
			if (image.isFile()) {
				Mat matrix = getImageAsMat(image.getAbsolutePath());
				String imageName = image.getName().substring(0, image.getName().indexOf('.'));
				String imagePath = targetFolder.getAbsolutePath() + '/' + imageName + IMGFORMAT;
				if (!new File(imagePath).exists()) {
					try {
						
						Mat preProcessedMatrix = matrix;
						// Rescaling
//						Mat preProcessedMatrix = rescaleMat(matrix);
//						matrix = preProcessedMatrix;

						// Apply Grayscale
						preProcessedMatrix = removeMatColors(preProcessedMatrix);
				
						// REMOWE NOISE
						preProcessedMatrix = bilateralFilter(preProcessedMatrix);

						// Binarisation white text on black
						preProcessedMatrix = threesholdMat(preProcessedMatrix, Imgproc.THRESH_BINARY_INV, 9, 3);
						
						preProcessedMatrix = dilateMat(preProcessedMatrix);
						
						// RESTORE WORDS IF WAS damaged
						preProcessedMatrix = morphologyClose(preProcessedMatrix, 1, 1);

						preProcessedMatrix = erodeMat(preProcessedMatrix);
						
						Core.bitwise_not(preProcessedMatrix, preProcessedMatrix);
						saveMatAsImage(preProcessedMatrix, imagePath);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private Mat bilateralFilter(Mat matrix) {
		Mat bilateralFilter = new Mat();
		Imgproc.bilateralFilter(matrix, bilateralFilter, 13, 17, 17);
		return bilateralFilter;
	}

	private Mat morphologyClose(Mat matrix, double elementSize, int iterations) {
		Mat morphologyEx = new Mat();
		Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT,new Size(elementSize, elementSize));
		Imgproc.morphologyEx(matrix, morphologyEx, Imgproc.MORPH_CLOSE, element, new Point(-1, -1), iterations);
		return morphologyEx;
	}

	private Mat addMedianBlur(Mat matrix) {
		Mat medianBlurMat = new Mat();
		Imgproc.medianBlur(matrix, medianBlurMat, 5);
		return medianBlurMat;
	}

	private Mat addGaussianBlur(Mat matrix) {
		Mat gaussianBlurMat = new Mat();
		Imgproc.GaussianBlur(matrix, gaussianBlurMat, new Size(9, 9), 4);
		return gaussianBlurMat;
	}

	// Make image DPI higher
	private Mat rescaleMat(Mat matrix) {
		Mat resizedMat = new Mat();
		double width = matrix.cols();
		double height = matrix.rows();
		double aspect = width / height;
		Size sz = new Size((width * aspect) / 2, (height * aspect) / 2);
		Imgproc.resize(matrix, resizedMat, sz);
		return resizedMat;
	}

	// Apply GrayScale
	private Mat removeMatColors(Mat matrix) {
		Mat grayScaledMat = new Mat();
		Imgproc.cvtColor(matrix, grayScaledMat, Imgproc.COLOR_RGB2GRAY);
		return grayScaledMat;
	}

	// For 4.x tesseract version use dark text on light background
	private Mat threesholdMat(Mat matrix, int threesholdType, int blockSiez, int C) {
		Mat adaptiveThreshMat = new Mat();
		Imgproc.adaptiveThreshold(matrix, adaptiveThreshMat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, threesholdType,
				blockSiez, C);
		return adaptiveThreshMat;
	}

	// Dilation
	private Mat dilateMat(Mat matrix) {
		Mat dilateMat = new Mat();
		Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(1 * 1 + 1, 1 * 1 + 1));
		Imgproc.dilate(matrix, dilateMat, element);
		return dilateMat;
	}

	// Erosion
	private Mat erodeMat(Mat matrix) {
		Mat erodeMat = new Mat();
		Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(1 * 1 + 1, 1 * 1 + 1));
		Imgproc.erode(matrix, erodeMat, element);
		return erodeMat;
	}

	public Mat deskew(Mat src, double angle) {
		Point center = new Point(src.width() / 2, src.height() / 2);
		Mat rotImage = Imgproc.getRotationMatrix2D(center, angle, 1.0);
		// 1.0 means 100 % scale
		Size size = new Size(src.width(), src.height());
		Imgproc.warpAffine(src, src, rotImage, size, Imgproc.INTER_LINEAR + Imgproc.CV_WARP_FILL_OUTLIERS);
		return src;
	}

	private static Mat getImageAsMat(String imagePath) {
		return Imgcodecs.imread(imagePath);
	}

	private static void saveMatAsImage(Mat imageAsMat, String targetPath) {
		MatOfInt int1 = new MatOfInt(Imgcodecs.IMWRITE_PNG_COMPRESSION, 9);
		Imgcodecs.imwrite(targetPath, imageAsMat, int1);
	}

}
