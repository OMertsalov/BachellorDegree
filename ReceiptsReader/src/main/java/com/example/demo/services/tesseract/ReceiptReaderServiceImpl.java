package com.example.demo.services.tesseract;

import java.awt.image.BufferedImage;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Market;
import com.example.demo.model.Receipt;
import com.example.demo.repository.MarketRepository;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

@Service
public class ReceiptReaderServiceImpl implements ReceiptReaderService {

	private Tesseract tesseract;
	private MarketRepository marketRepository;
	
	@Autowired
	public ReceiptReaderServiceImpl(MarketRepository marketRepository) {
		this.marketRepository = marketRepository;
		tesseract = new Tesseract();
		tesseract.setDatapath("src/main/resources/tessdata");
		tesseract.setOcrEngineMode(1);
		tesseract.setTessVariable("user_defined_dpi", "300");
	}

	@Override
	public List<String> readLines(List<BufferedImage> lines, String language) throws TesseractException {
		List<String> textLines = new ArrayList<>();
		double avgLineHeight = lines.stream().mapToDouble(bi -> bi.getTileHeight()).average().getAsDouble();
		avgLineHeight += avgLineHeight / 2;
		tesseract.setLanguage(language);
		for (BufferedImage image : lines) {
			if (image.getHeight() > avgLineHeight)
				tesseract.setPageSegMode(4);
			else
				tesseract.setPageSegMode(7);
			String result = tesseract.doOCR(image);
			textLines.add(result);
		}
		return textLines;
	}

	@Override
	public Receipt readReceiptData(List<BufferedImage> lines, String language) throws TesseractException {
		tesseract.setLanguage(language);
		Receipt receipt = new Receipt();
		Market market = new Market();	
		
		double avgLineHeight = lines.stream().mapToDouble(bi -> bi.getTileHeight()).average().getAsDouble();
		avgLineHeight += avgLineHeight / 2;
		StringBuilder receiptText = new StringBuilder();
		String lineText = "";
		Iterator<BufferedImage> linesIterator = lines.iterator();
		int lineCounter = 0;
		while (!lineText.toLowerCase().contains("paragon fiskalny")) {
			if (!linesIterator.hasNext())
				throw new TesseractException("Na zdjęciu nie ma paragonu fiskalnego!");
			BufferedImage lineImage = linesIterator.next();
			
			if (lineImage.getHeight() > avgLineHeight) tesseract.setPageSegMode(4);
			else tesseract.setPageSegMode(7);
			
			lineText = tesseract.doOCR(lineImage);
			receiptText.append(lineText).append('\n');
			addMarketData(market, lineText.toLowerCase(),lineCounter);
			Date sellDate = findSellDate(lineText);
			if(sellDate != null) receipt.setSellDate(sellDate);					
		}
		
		if(marketRepository.existsByAddress(market.getAddress())) {
			System.out.println("Exist");
			//check data
		} else {
			market.setKnown(false);
			receipt.setContainNewData(true);
		}
		
		//start with line text it will contain Paragon Fiskalny and maybe some item data
		
		receipt.setText(receiptText.toString());
		return receipt;
	}

	private Date findSellDate(String input) {
		Matcher matcher = Pattern.compile("\\d{4}-\\d{2}-\\d{2}").matcher(input);
		if(matcher.find()) {
			String date = matcher.group();
			try {
				return new SimpleDateFormat("yyyy-MM-dd").parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private void addMarketData(Market market, String textData, int lineCounter) {
		String[] textlines = textData.split(System.getProperty("line.separator"));
		for(String line : textlines) {
			lineCounter++;
			if(market.getPartnership() == null && isPartnershipData(line)) 
				market.setPartnership(line);
			else if(isStreetData(line)){
				if (market.getPartnershipAddress() == null && market.getPartnership() != null) 
					market.setPartnershipAddress(line);
				else if(market.getAddress() == null)
					market.setAddress(line);
			} else {
				if(market.getName() == null && isMarketNameData(line,lineCounter))
					market.setName(line);
			}
		}
	}

	private boolean isMarketNameData(String line,int lineCounter) {
//		jest w pierwszych 3 liniach 
//		minimum 2 symbole
//		Zawiera litery 
//		Albo zawiera slowo sklep lub market
		return lineCounter <= 3  && ((line.length() >= 2 && line.matches("[a-zA-Z]+")) || (line.contains("sklep") || line.contains("market")) );
	}

	private boolean isPartnershipData(String data) {
		return data.contains("sp.z o.o.") || data.contains("s.a.");
	}

	private boolean isStreetData(String data) {
		return data.contains("ul.") || data.contains("al.");
	}

}