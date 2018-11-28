package m2t.jobloader.dao.model.translators;

import java.util.List;
import java.util.Map;

public interface DTODAOTranslator<DTO,DAO> {
	
	public DAO toDAO(DTO dto);
	
	public DTO toDTO(DAO dao);
	
	public DTO toDTO(Map<String, String> values);
	
	public Map<String, String> toMap(DTO dto);
	
	public List<String> getColumns();

}
