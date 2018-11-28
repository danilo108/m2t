package m2t.jobloader.dao.model.translators;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;


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
      
      public String convertToText(java.io.InputStream inputStream) throws IOException {
          PdfReader reader = new PdfReader(inputStream);
          StringBuffer sb = new StringBuffer();
          sb.append("__cc__ps1__\n");

          for (int page = 1; page <= reader.getNumberOfPages(); page++) {
        	
        	  sb.append(PdfTextExtractor.getTextFromPage(reader, page));
            
          }
          
          return sb.toString();
      }
    }