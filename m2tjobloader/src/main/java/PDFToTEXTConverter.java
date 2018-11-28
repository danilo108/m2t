import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;

import javax.swing.plaf.synth.SynthSeparatorUI;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PRIndirectReference;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PRTokeniser;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.codec.Base64.InputStream;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import ch.qos.logback.core.net.SyslogOutputStream;


public class PDFToTEXTConverter {

      
      public void parse(String fileName) throws IOException {
          PdfReader reader = new PdfReader(fileName);
          FileWriter fw = new FileWriter(fileName+".txt", false);
          BufferedWriter bw = new BufferedWriter(fw);
          bw.write("__cc__ps1__\n");

          for (int page = 1; page <= reader.getNumberOfPages(); page++) {
        	
			        	  bw.write(PdfTextExtractor.getTextFromPage(reader, page));
            
          }
          bw.flush();
          bw.close();
          
      }
      
      public String convertToText(InputStream is) throws IOException {
          PdfReader reader = new PdfReader(is);
          StringBuffer sb = new StringBuffer();
          sb.append("__cc__ps1__\n");

          for (int page = 1; page <= reader.getNumberOfPages(); page++) {
        	
        	  sb.append(PdfTextExtractor.getTextFromPage(reader, page));
            
          }
          
          return sb.toString();
      }
    }