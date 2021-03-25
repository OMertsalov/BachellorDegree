package com.example.demo.services;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
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
//					preProcessedMatrix = dilateMat(preProcessedMatrix);

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
						
						preProcessedMatrix = dilateMat(preProcessedMatrix,1,1);
						
						// RESTORE WORDS IF WAS damaged
						preProcessedMatrix = morphologyClose(preProcessedMatrix, 1, 1, 1);

						preProcessedMatrix = erodeMat(preProcessedMatrix,1,1);										
						
						Core.bitwise_not(preProcessedMatrix, preProcessedMatrix);
						saveMatAsImage(preProcessedMatrix, imagePath);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public void processImageForOCR(String folderPath) {
		File sourceFolder = new File(folderPath);
		File targetFolder = new File(folderPath + "/Test");
		if (!targetFolder.exists())
			targetFolder.mkdir();
		for (final File image : sourceFolder.listFiles()) {
			if (image.isFile()) {
				Mat matrix = getImageAsMat(image.getAbsolutePath());
				String imageName = image.getName().substring(0, image.getName().indexOf('.'));
				String imagePath = targetFolder.getAbsolutePath() + '/' +imageName;
				File imageFolder = new File(imagePath);
				if (!new File(imagePath).exists()) {
					try {
						imageFolder.mkdir();
						Mat preProcessedMatrix = matrix;
						// Rescaling
//						Mat preProcessedMatrix = rescaleMat(matrix);
//						matrix = preProcessedMatrix;

						// Apply Grayscale
						preProcessedMatrix = removeMatColors(preProcessedMatrix);
						
						// Binarisation white text on black
						preProcessedMatrix = threesholdMat(preProcessedMatrix, Imgproc.THRESH_BINARY_INV, 11, 2);
						
						preProcessedMatrix = removeLines(preProcessedMatrix);
						
						preProcessedMatrix = erodeMat(preProcessedMatrix,2,2);			
						
						preProcessedMatrix = dilateMat(preProcessedMatrix,40,0);
						
						// RESTORE WORDS IF WAS damaged
//						preProcessedMatrix = morphologyClose(preProcessedMatrix, preProcessedMatrix.width(),1, 1);

						preProcessedMatrix = findLines(preProcessedMatrix);
						
						cropLines(preProcessedMatrix,matrix,imagePath);
						
//						Core.bitwise_not(preProcessedMatrix, preProcessedMatrix);
//						saveMatAsImage(preProcessedMatrix, imagePath);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private void cropLines(Mat preProcessedMatrix, Mat original,String imagePath) {
		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	    Mat hierarchy = new Mat();
	    Imgproc.findContours(preProcessedMatrix, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
	    //sort from top to bottom
	    contours.sort(new Comparator<MatOfPoint>() {
	    	@Override
	        public int compare(MatOfPoint o1, MatOfPoint o2) {
	            Rect rect1 = Imgproc.boundingRect(o1);
	            Rect rect2 = Imgproc.boundingRect(o2);
	            return Double.compare(rect1.tl().y, rect2.tl().y);
	        }
		});
	    
	    int lineNumber = 0;
	    for (MatOfPoint point : contours) {
		    	Rect rect = Imgproc.boundingRect(point);
	    		Mat line = new Mat(original,rect);
	    		
	    		//add border
	    		Mat lineVsBorder = new Mat();
	    		int top,bottom,left,right;
	    	    top = 10; bottom = top;
	    	    left = 5; right = left;
	            Core.copyMakeBorder( line, lineVsBorder, top, bottom, left, right, Core.BORDER_ISOLATED, new Scalar(255, 255, 255));
	            
	    		StringBuilder path = new StringBuilder(imagePath);
	    		path.append('/').append(lineNumber).append(IMGFORMAT);
	    		saveMatAsImage(lineVsBorder, path.toString());
	    		lineNumber++;
		}
	}

	private Mat findLines(Mat preProcessedMatrix) {
		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	    Mat hierarchy = new Mat();
	    Imgproc.findContours(preProcessedMatrix, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
	    for (MatOfPoint point : contours) {
	    		Rect rect = Imgproc.boundingRect(point);
				Imgproc.rectangle(preProcessedMatrix,new Point(0,rect.y-2),new Point(preProcessedMatrix.width(),rect.y + rect.height),new Scalar(255, 255, 255), -1);
		}
		return preProcessedMatrix;
	}
	
	private Mat removeLines(Mat matrix) {
		// Vertical lines removal
		Mat vertical_kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 40));
		Mat remove_vertical = new Mat();
		Imgproc.morphologyEx(matrix, remove_vertical, Imgproc.MORPH_OPEN, vertical_kernel, new Point(), 1);
		List<MatOfPoint> contours = new ArrayList<>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(remove_vertical, contours, hierarchy, Imgproc.RETR_EXTERNAL,
				Imgproc.CHAIN_APPROX_SIMPLE);

		Double avgHeight = contours.stream()
				.collect(Collectors.averagingInt(contour -> Imgproc.boundingRect(contour).height));
		for (MatOfPoint point : contours) {
			Rect rect = Imgproc.boundingRect(point);
			if (rect.height > avgHeight * 2) {
				Imgproc.line(matrix,new Point(rect.x,0),new Point(rect.x,matrix.height()),new Scalar(0, 0, 0), 30);
			}
		}
		
		return matrix;
	}

	private Mat bilateralFilter(Mat matrix) {
		Mat bilateralFilter = new Mat();
		Imgproc.bilateralFilter(matrix, bilateralFilter, 13, 17, 17);
		return bilateralFilter;
	}

	private Mat morphologyClose(Mat matrix, double elementWidth,double elementHeight, int iterations) {
		Mat morphologyEx = new Mat();
		Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT,new Size(elementWidth, elementHeight));
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
	private Mat dilateMat(Mat matrix,double width,double height) {
		Mat dilateMat = new Mat();
		Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(1 * width + 1, 1 * height + 1));
		Imgproc.dilate(matrix, dilateMat, element);
		return dilateMat;
	}

	// Erosion
	private Mat erodeMat(Mat matrix,double width,double height) {
		Mat erodeMat = new Mat();
		Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(1 * width + 1, 1 * height + 1));
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
