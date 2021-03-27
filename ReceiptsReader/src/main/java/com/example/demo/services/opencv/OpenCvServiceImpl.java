package com.example.demo.services.opencv;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

@Service
public class OpenCvServiceImpl implements OpenCvService {

	@Override
	public List<BufferedImage> getReceiptLines(byte[] image) throws IOException {
		Mat mat = Imgcodecs.imdecode(new MatOfByte(image), Imgcodecs.IMREAD_UNCHANGED);
		Mat preProcessedMat = preProcessImageForOcr(mat);
		return findLines(preProcessedMat);
//		saveMatAsImage(receiptLines.get(14));
	}
	
	@Deprecated
	private static void saveMatAsImage(Mat imageAsMat) {
		MatOfInt int1 = new MatOfInt(Imgcodecs.IMWRITE_PNG_COMPRESSION, 9);
		Imgcodecs.imwrite("/home/alexander/WorkFolder/Tesseract/Images/PreProcessedd/Test/image.png", imageAsMat, int1);
	}

	private List<BufferedImage> findLines(Mat mat) throws IOException {

		Mat preProcessedMat = mat;

		preProcessedMat = threesholdMat(preProcessedMat, Imgproc.THRESH_BINARY_INV, 11, 2);

		preProcessedMat = erodeMat(preProcessedMat, 2, 2);

		preProcessedMat = dilateMat(preProcessedMat, 40, 0);

		drawRectsOfWords(preProcessedMat);

		return cropLinesAndSaveAsBI(preProcessedMat, mat);

	}

	private Mat preProcessImageForOcr(Mat mat) {
		Mat preProcessedMat = mat;
		// Rescaling
//		Mat preProcessedMatrix = rescaleMat(matrix);
//		matrix = preProcessedMatrix;

		// Apply Grayscale
		preProcessedMat = removeMatColors(preProcessedMat);

		// REMOWE NOISE
		preProcessedMat = bilateralFilter(preProcessedMat);

		// Binarisation white text on black
		preProcessedMat = threesholdMat(preProcessedMat, Imgproc.THRESH_BINARY_INV, 9, 3);

		preProcessedMat = dilateMat(preProcessedMat, 1, 1);

		// RESTORE WORDS IF WAS damaged
		preProcessedMat = morphologyClose(preProcessedMat, 1, 1, 1);

		preProcessedMat = erodeMat(preProcessedMat, 1, 1);
		
		removeVerticalLines(preProcessedMat);

		Core.bitwise_not(preProcessedMat, preProcessedMat);
		return preProcessedMat;
	}

	private List<BufferedImage> cropLinesAndSaveAsBI(Mat preProcessedMatrix, Mat original) throws IOException {
		ArrayList<MatOfPoint> contours = new ArrayList<>();
		Imgproc.findContours(preProcessedMatrix, contours, new Mat(), Imgproc.RETR_EXTERNAL,
				Imgproc.CHAIN_APPROX_SIMPLE);

		contours.sort((MatOfPoint o1, MatOfPoint o2) -> {
			Rect rect1 = Imgproc.boundingRect(o1);
			Rect rect2 = Imgproc.boundingRect(o2);
			return Double.compare(rect1.tl().y, rect2.tl().y);
		});
		
		List<BufferedImage> lines = new ArrayList<>();
		for (MatOfPoint point : contours) {
			Rect rect = Imgproc.boundingRect(point);
			Mat line = new Mat(original, rect);

			// add white border
			Mat lineVsBorder = new Mat();
			Core.copyMakeBorder(line, lineVsBorder, 10, 10, 5, 5, Core.BORDER_ISOLATED,
					new Scalar(255, 255, 255));
			lines.add(mat2BufferedImage(lineVsBorder));
		}
		return lines;
	}

	// Apply GrayScale
	private Mat removeMatColors(Mat matrix) {
		Mat grayScaledMat = new Mat();
		Imgproc.cvtColor(matrix, grayScaledMat, Imgproc.COLOR_RGB2GRAY);
		return grayScaledMat;
	}

	private Mat bilateralFilter(Mat matrix) {
		Mat bilateralFilter = new Mat();
		Imgproc.bilateralFilter(matrix, bilateralFilter, 13, 17, 17);
		return bilateralFilter;
	}

	// For 4.x tesseract version use dark text on light background
	private Mat threesholdMat(Mat matrix, int threesholdType, int blockSiez, int C) {
		Mat adaptiveThreshMat = new Mat();
		Imgproc.adaptiveThreshold(matrix, adaptiveThreshMat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, threesholdType,
				blockSiez, C);
		return adaptiveThreshMat;
	}

	// Dilation
	private Mat dilateMat(Mat matrix, double width, double height) {
		Mat dilateMat = new Mat();
		Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(1 * width + 1, 1 * height + 1));
		Imgproc.dilate(matrix, dilateMat, element);
		return dilateMat;
	}

	private Mat morphologyClose(Mat matrix, double elementWidth, double elementHeight, int iterations) {
		Mat morphologyEx = new Mat();
		Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(elementWidth, elementHeight));
		Imgproc.morphologyEx(matrix, morphologyEx, Imgproc.MORPH_CLOSE, element, new Point(-1, -1), iterations);
		return morphologyEx;
	}

	// Erosion
	private Mat erodeMat(Mat matrix, double width, double height) {
		Mat erodeMat = new Mat();
		Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(1 * width + 1, 1 * height + 1));
		Imgproc.erode(matrix, erodeMat, element);
		return erodeMat;
	}

	private Mat removeVerticalLines(Mat matrix) {
		// Vertical lines removal
		Mat verticalKernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 40));
		Mat removeVertical = new Mat();
		Imgproc.morphologyEx(matrix, removeVertical, Imgproc.MORPH_OPEN, verticalKernel, new Point(), 1);
		List<MatOfPoint> contours = new ArrayList<>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(removeVertical, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

		Double avgHeight = contours.stream()
				.collect(Collectors.averagingInt(contour -> Imgproc.boundingRect(contour).height));
		for (MatOfPoint point : contours) {
			Rect rect = Imgproc.boundingRect(point);	
			if (rect.height > avgHeight * 2) {
				Imgproc.line(matrix, new Point(rect.x, 0), new Point(rect.x, matrix.height()), new Scalar(0, 0, 0), 30);
			}
		}
		
		//delete left and right border
		Imgproc.line(matrix, new Point(0, 0), new Point(0, matrix.height()), new Scalar(0, 0, 0), 30);
		Imgproc.line(matrix, new Point(matrix.width(), 0), new Point(matrix.width(), matrix.height()), new Scalar(0, 0, 0), 30);

		return matrix;
	}

	private void drawRectsOfWords(Mat preProcessedMatrix) {
		ArrayList<MatOfPoint> contours = new ArrayList<>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(preProcessedMatrix, contours, hierarchy, Imgproc.RETR_EXTERNAL,
				Imgproc.CHAIN_APPROX_SIMPLE);
		for (MatOfPoint point : contours) {
			Rect rect = Imgproc.boundingRect(point);
			Imgproc.rectangle(preProcessedMatrix, new Point(0, (double) rect.y - 2),
					new Point(preProcessedMatrix.width(), (double) rect.y + rect.height), new Scalar(255, 255, 255), -1);
		}
	}
	
	static BufferedImage mat2BufferedImage(Mat matrix) throws IOException{
	    MatOfByte mob = new MatOfByte();
	    Imgcodecs.imencode(".png", matrix, mob);
	    byte[] ba = mob.toArray();

	    return ImageIO.read(new ByteArrayInputStream(ba));
	}

}
