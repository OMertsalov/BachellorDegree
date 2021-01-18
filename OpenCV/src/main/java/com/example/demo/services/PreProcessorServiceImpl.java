package com.example.demo.services;

import java.io.File;

import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.stereotype.Service;

import nu.pattern.OpenCV;

@Service
public class PreProcessorServiceImpl implements PreProcessorService {

	@Override
	public void processImages(String folderPath) {
		OpenCV.loadShared();
		File sourceFolder = new File(folderPath);
		File targetFolder = new File(folderPath + "/PreProcessed");
		if (!targetFolder.exists())
			targetFolder.mkdir();
		for (final File image : sourceFolder.listFiles()) {
			if (image.isFile()) {
				Mat matrix = getImageAsMat(image.getAbsolutePath());
				Mat preProcessedImage = new Mat();
				String imagePath = targetFolder.getAbsolutePath() +"/image.png";//+ image.getName();
				saveMatAsImage(preProcessedImage, imagePath);
			}
		}
	}

	private static Mat getImageAsMat(String imagePath) {
		return Imgcodecs.imread(imagePath);
	}

	private static void saveMatAsImage(Mat imageAsMat, String targetPath) {
		MatOfInt int1 = new MatOfInt(Imgcodecs.IMWRITE_PNG_COMPRESSION,9);
		Imgcodecs.imwrite(targetPath, imageAsMat,int1);
	}

}
