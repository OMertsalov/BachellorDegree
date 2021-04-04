package com.example.demo.services.tesseract;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Item;
import com.example.demo.model.Market;
import com.example.demo.model.Receipt;
import com.example.demo.model.ReceiptItems;
import com.example.demo.model.Result;
import com.example.demo.model.Tax;
import com.example.demo.repository.MarketRepository;
import com.example.demo.repository.TaxRepository;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

@Service
public class ReceiptReaderServiceImpl implements ReceiptReaderService {

	private MarketRepository marketRepository;
	private TaxRepository taxRepository;
	
	private static final int MIN_ITEM_LINELENGTH = 14;

	@Autowired
	public ReceiptReaderServiceImpl(MarketRepository marketRepository,TaxRepository taxRepository) {
		this.marketRepository = marketRepository;
		this.taxRepository = taxRepository;
	}

	@Override
	public Result readReceiptData(List<BufferedImage> lines, String language) throws TesseractException {
		Result result = new Result(new Receipt() ,new LinkedHashMap<>());
		Receipt receipt = result.getReceipt();
		Tesseract tesseract = createTesseractInstanse(language);
		double avgLineHeight = getAvgLineHeight(lines);
		StringBuilder receiptText = new StringBuilder();
		Iterator<BufferedImage> linesIterator = lines.iterator();
		String lineText = "";
		int lineCounter = 0;
		BufferedImage lineImage = null;
		receipt.setMarket(new Market());
		while (!lineText.toLowerCase().contains("paragon fiskalny")) {
			if (!linesIterator.hasNext())
				throw new TesseractException("Na zdjęciu nie ma paragonu fiskalnego!");
			lineImage = linesIterator.next();
			lineText = readTextOnImage(tesseract, lineImage, avgLineHeight);
			addMarketData(receipt, lineText, lineCounter);
			lineCounter++;
			receiptText.append(lineText).append('\n');
		}
		
		while (!lineText.toLowerCase().contains("- - - - - - - -") || lineText.toLowerCase().contains("opod")) {
			addItemData(lineImage ,lineText, result.getReceiptItems(),receipt);
			lineImage = linesIterator.next();
			lineText = readTextOnImage(tesseract, lineImage, avgLineHeight);
			receiptText.append(lineText).append('\n');
		}

		// start with line text it will contain Paragon Fiskalny and maybe some item
		// data

		if (marketRepository.existsByAddress(receipt.getMarket().getAddress())) {
			System.out.println("Exist");
			// check data
		} else {
		}

		receipt.setText(receiptText.toString());
		result.setReceipt(receipt);
		return result;
	}

	private void addItemData(BufferedImage image, String textData, Map<String, ReceiptItems> receiptItems,Receipt receipt) {
		String[] textlines = textData.split(System.getProperty("line.separator"));
		for (String line : textlines) {
			String lineToLowerCase = line.toLowerCase();
			if((!lineToLowerCase.contains("paragon fiskalny") && lineToLowerCase.length() >= MIN_ITEM_LINELENGTH)) {
				Matcher matcher = Pattern.compile("(.*\\S {1,4})([oliOI0-9]{1,3}(?:[,.][oliOI0-9]{3})?) ?x([oliOI0-9]{1,8}[,.][oliOI0-9]{2}) ([oliOI0-9]{1,8}[,.][oliOI0-9]{2})([ABCD])").matcher(line);
				ReceiptItems receiptItem = new ReceiptItems(receipt, new Item("",new Tax(' ',0),true),0,0,0);
				receiptItem.setLineTextByOCR(line);
				if(matcher.find()) {
					String itemName = matcher.group(1).trim();
					double itemAmount = stringToDouble(matcher.group(2));
					double itemPrice = stringToDouble(matcher.group(3));
					double itemPriceSum = stringToDouble(matcher.group(4));
					char itemTaxSign = matcher.group(5).charAt(0);
					Tax itemTax = taxRepository.findBySign(itemTaxSign);
					receiptItem.setItem(new Item(itemName,itemTax));
					receiptItem.setAmount(itemAmount);
					receiptItem.setItemPrice(itemPrice);
					receiptItem.setPriceSum(itemPriceSum);
				}
				receiptItems.put(imageToBase64(image),receiptItem);
			}
		}
	}

	private double stringToDouble(String string) {
		return Double.parseDouble(string.toLowerCase().replace(",",".").replace("o", "0").replace("l", "1").replace("i", "1"));
	}

	private String imageToBase64(BufferedImage image) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", baos);
	        return Base64.getEncoder().encodeToString(baos.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	private String readTextOnImage(Tesseract tesseract, BufferedImage lineImage, double avgLineHeight)
			throws TesseractException {
		if (lineImage.getHeight() > avgLineHeight)
			tesseract.setPageSegMode(4);
		else
			tesseract.setPageSegMode(7);
		return tesseract.doOCR(lineImage);
	}

	private double getAvgLineHeight(List<BufferedImage> lines) {
		return (lines.stream().mapToDouble(bi -> bi.getTileHeight()).average().getAsDouble()) * 1.5;
	}

	private Tesseract createTesseractInstanse(String language) {
		Tesseract tesseract = new Tesseract();
		tesseract.setDatapath("src/main/resources/tessdata");
		tesseract.setOcrEngineMode(1);
		tesseract.setTessVariable("user_defined_dpi", "300");
		tesseract.setLanguage(language);
		return tesseract;
	}

	private void addMarketData(Receipt receipt, String textData, Integer lineCounter) {
		Market market = receipt.getMarket();
		String[] textlines = textData.split(System.getProperty("line.separator"));
		for (String line : textlines) {
			lineCounter++;
			String lineToLowerCase = line.toLowerCase();
			if (market.getPartnership() == null && isPartnershipData(lineToLowerCase))
				market.setPartnership(line);
			else if (isStreetData(lineToLowerCase)) {
				if (market.getPartnershipAddress() == null && market.getPartnership() != null)
					market.setPartnershipAddress(line);
				else if (market.getAddress() == null)
					market.setAddress(line);
			} else if (market.getName() == null && isMarketNameData(lineToLowerCase, lineCounter))
				market.setName(line);
			else if (receipt.getSellDate() == null) {
				Date sellDate = getSellDateOrNull(textData);
				receipt.setSellDate(sellDate);
			}
		}
	}

	private Date getSellDateOrNull(String input) {
		Matcher matcher = Pattern.compile("\\d{4}-\\d{2}-\\d{2}").matcher(input);
		if (matcher.find()) {
			String date = matcher.group();
			try {
				return new SimpleDateFormat("yyyy-MM-dd").parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private boolean isMarketNameData(String line, int lineCounter) {
//		jest w pierwszych 3 liniach, minimum 2 symbole, zawiera litery albo zawiera slowo sklep lub market
		return lineCounter <= 3 && ((line.length() >= 2 && line.matches("[a-zA-Z]+"))
				|| (line.contains("sklep") || line.contains("market")));
	}

	private boolean isPartnershipData(String data) {
		return data.contains("sp.z o.o.") || data.contains("s.a.");
	}

	private boolean isStreetData(String data) {
		return data.contains("ul.") || data.contains("al.");
	}

}