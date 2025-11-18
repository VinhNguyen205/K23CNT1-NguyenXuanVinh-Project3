package K23CNT1.NguyenXuanVinh.Lesson08.service;

import K23CNT1.NguyenXuanVinh.Lesson08.entity.Configuration;
import K23CNT1.NguyenXuanVinh.Lesson08.repository.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ConfigurationService {
    @Autowired
    private ConfigurationRepository configurationRepository;

    public List<Configuration> getAllConfigurations() { return configurationRepository.findAll(); }
    public Configuration getConfigurationById(Long id) { return configurationRepository.findById(id).orElse(null); }
    public Configuration saveConfiguration(Configuration config) { return configurationRepository.save(config); }
    public void deleteConfiguration(Long id) { configurationRepository.deleteById(id); }
    public List<Configuration> findAllById(List<Long> ids) { return configurationRepository.findAllById(ids); }
}