package m2t.jobloader;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.api.services.drive.model.File;

import m2t.jobloader.service.controllers.GoogleWrapper;

@RunWith(SpringRunner.class)
@SpringBootTest
public class M2tjobloaderApplicationTests {
	
	@Autowired
	GoogleWrapper googleWrapper;

	@Test
	public void contextLoads() {
	}

	@Test
	public void googleWrap() throws IOException, GeneralSecurityException {
		
		
//		File result = googleWrapper.duplicateFromTemplate("1AV2vM_Zl9LU_V0NCBurSUkE5fo1OlnKI7YMRU0D6Ppc", "A copied one " + (new Date()).getTime());
		
	}
}
